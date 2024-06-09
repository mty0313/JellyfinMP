package top.mty.service;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import top.mty.common.CustomAppId;
import top.mty.common.ImageCropType;
import top.mty.controller.data.jellyfin.webhook.JellyfinWebhookProperties;
import top.mty.entity.JellyfinWebhookEntity;
import top.mty.mapper.JellyfinWebhookEntityMapper;
import top.mty.remote.JellyfinFullControlApiClient;
import top.mty.remote.WeixinMPClient;
import top.mty.remote.param.*;
import top.mty.utils.FileUtil;
import top.mty.utils.GuavaCache;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class JellyfinWebhookService extends ServiceImpl<JellyfinWebhookEntityMapper, JellyfinWebhookEntity> {
  @Autowired
  private JellyfinWebhookEntityMapper mapper;
  @Value("${jellyfin.webhook.enabledNotificationTypes}")
  private String enabledNotificationTypes;
  @Value("${jellyfin.webhook.serverTempData:/server-temp/}")
  private String serverTempData;
  @Autowired
  private DynamicFeignClientService dynamicFeignClientService;
  @Value("${weixin.mp.defaultActorImg:http://mmbiz.qpic.cn/mmbiz_png/czaOslr1DNdJV1AZndZVLpyxgOrr1xQg0XDia8hL1XrXq6JcwAF6Z6Y8c9Pq7dhqorBM0Q6cJvTFu5N1Pib5RLlg/0?wx_fmt=png}")
  private String defaultActorImg;
  @Value("${weixin.mp.actorFetchMaxSize:10}")
  private Integer actorFetchMaxSize;
  @Value("${jellyfin.adminId}")
  private String jellyfinAdminId;

  public void itemAdded(JellyfinWebhookProperties properties) {
    if (!notificationTypeEnabled(properties)) {
      log.info("该类型Webhook不被支持: " + properties.getNotificationType());
      return;
    }
    JellyfinWebhookEntity entity = JellyfinWebhookEntity.toWebhookEntity(properties);
    if (!StringUtils.hasText(entity.getNotificationType())) {
      return;
    }
    insertOrUpdate(entity);
  }

  public List<JellyfinWebhookEntity> getEntities(QueryWrapper<JellyfinWebhookEntity> wrapper) {
    return mapper.selectList(wrapper);
  }

  public void updateBatchProcessedByUuids(List<String> uuids) {
    QueryWrapper<JellyfinWebhookEntity> queryWrapper = new QueryWrapper<>();
    queryWrapper.in("uuid", uuids);
    List<JellyfinWebhookEntity> entities = mapper.selectList(queryWrapper);
    if (!CollectionUtils.isEmpty(entities)) {
      entities.forEach(e -> e.setProcessed(true));
    }
    updateBatchById(entities);
  }

  public String uploadItemImage2WeixinMP(String localPath) {
    if (StringUtils.hasText(localPath)) {
      String cachedKey = null;
      try {
        String[] splitPath = localPath.split("\\.")[0].split("/");
        cachedKey = splitPath[splitPath.length - 1];
      } catch (Exception e) {
        log.error("uploadItemImage2WeixinMP localPath abnormal : {}", localPath);
        return null;
      }
      if (!StringUtils.hasText(cachedKey)) {
        log.error("uploadItemImage2WeixinMP localPath abnormal : {}", localPath);
        return null;
      }
      MultipartFile file = FileUtil.convert(localPath);
      // 检查图片是否已经在缓存
      String cachedWeixinImageUrl = (String) GuavaCache.get(cachedKey);
      if (StringUtils.hasText(cachedWeixinImageUrl)) {
        log.debug("缓存命中: {}", cachedKey);
        // 删除本地文件
        if (null != file) {
          FileUtil.deleteFile(localPath);
        }
        return cachedWeixinImageUrl;
      }
      if (null != file) {
        WeixinMPClient client = dynamicFeignClientService.getClient(WeixinMPClient.class, CustomAppId.WeixinMP.name());
        String uploadResponseStr = client.uploadImage(file);
        WeixinMPImageUploadResponse uploadResponse = JSON.to(WeixinMPImageUploadResponse.class, uploadResponseStr);
        if (null != uploadResponse && uploadResponse.success()) {
          FileUtil.deleteFile(localPath);
          GuavaCache.put(cachedKey, uploadResponse.getUrl());
          return uploadResponse.getUrl();
        }
      }
    }
    return null;
  }

  /**
   * 查找item的Primary图片,发送到微信
   */
  public String uploadItemImage2WeixinMP(JellyfinWebhookEntity entity) {
    String localPath = saveItemImage2Local(entity, JellyfinItemImageType.Primary);
    return uploadItemImage2WeixinMP(localPath);
  }

  /**
   * 上传jellyfin item的backdrop到微信永久素材
   * 返回mediaId
   */
  public String uploadBackdrop2WeixinMP(JellyfinWebhookEntity entity, String type) {
    String localPath = saveItemImage2Local(entity, JellyfinItemImageType.Backdrop);
    if (StringUtils.hasText(localPath)) {
      MultipartFile file = FileUtil.convert(localPath);
      if (null != file) {
        WeixinMPClient client = dynamicFeignClientService.getClient(WeixinMPClient.class, CustomAppId.WeixinMP.name());
        String uploadResponseStr = client.addMaterial(type, file);
        WeixinMPAddMaterialResponse uploadResponse = JSON.to(WeixinMPAddMaterialResponse.class, uploadResponseStr);
        if (null != uploadResponse && uploadResponse.success()) {
          FileUtil.deleteFile(localPath);
          return uploadResponse.getMediaId();
        }
      }
    }
    return null;
  }

  public JellyfinItemCount getItemCount() {
    JellyfinFullControlApiClient client = dynamicFeignClientService.getClient(JellyfinFullControlApiClient.class, CustomAppId.Jellyfin.name());
    return client.getItemCount(jellyfinAdminId, null);
  }

  public JellyfinItemExtra4ArticleContent fetchItemExtra(JellyfinWebhookEntity entity) {
    if (null == entity) {
      return null;
    }
    JellyfinFullControlApiClient client = dynamicFeignClientService.getClient(JellyfinFullControlApiClient.class, CustomAppId.Jellyfin.name());
    // 电影可以直接用评分
    if (JellyfinWebhookProperties.ITEM_TYPE_MOVIE.equals(entity.getItemType())) {
      JellyfinItemResponse response = client.getItems(new JellyfinItemRequest(entity.getItemId()));
      if (null != response && !CollectionUtils.isEmpty(response.getItems())) {
        JellyfinItemResponse.ItemsDTO item = response.getItems().get(0);
        JellyfinItemExtra4ArticleContent extra4ArticleContent = new JellyfinItemExtra4ArticleContent();
        extra4ArticleContent.setCommunityRating(item.getCommunityRating());
        extra4ArticleContent.setOfficialRating(item.getOfficialRating());
        fetchActorList(entity.getItemId(), extra4ArticleContent);
        return extra4ArticleContent;
      }
    }
    // 剧集查询series的评分
    if (JellyfinWebhookProperties.ITEM_TYPE_SERIES.equals(entity.getItemType())) {
      // 查询series信息
      JellyfinItemResponse response = client.getItems(new JellyfinItemRequest(entity.getItemId()));
      if (null != response && !CollectionUtils.isEmpty(response.getItems())) {
        // 查询只用一个itemId, 所以返回items中只有一条
        JellyfinItemResponse.ItemsDTO seriesDTO = response.getItems().get(0);
        JellyfinItemExtra4ArticleContent extra4ArticleContent = new JellyfinItemExtra4ArticleContent();
        extra4ArticleContent.setCommunityRating(seriesDTO.getCommunityRating());
        extra4ArticleContent.setOfficialRating(seriesDTO.getOfficialRating());
        fetchActorList(seriesDTO.getId(), extra4ArticleContent);
        return extra4ArticleContent;
      } else {
        log.error("getItems请求失败");
      }
    }
    return null;
  }

  private void fetchActorList(String itemId, JellyfinItemExtra4ArticleContent content) {
    JellyfinFullControlApiClient client = dynamicFeignClientService.getClient(JellyfinFullControlApiClient.class, CustomAppId.Jellyfin.name());
    JellyfinUserLibraryItem userLibraryItem = client.getUserLibraryItem(jellyfinAdminId, itemId);
    if (null == userLibraryItem) {
      return;
    }
    List<JellyfinUserLibraryItem.PeopleDTO> peoples = userLibraryItem.getPeople();
    if (CollectionUtils.isEmpty(peoples)) {
      return;
    }
    List<JellyfinItemExtra4ArticleContent.Actor> actorList = new ArrayList<>();
    int maxSize = Math.min(actorFetchMaxSize, peoples.size());
    int current = 0;
    while (current < maxSize) {
      JellyfinUserLibraryItem.PeopleDTO peopleDTO = peoples.get(current);
      if (null == peopleDTO) {
        break;
      }
      String peopleItemId = peopleDTO.getId();
      String peopleImageLocalPath = null;
      try {
        peopleImageLocalPath = doImageGetAndSave(client, peopleItemId, JellyfinItemImageType.Primary);
      } catch (Exception e) {
        log.error("fetch peopleImageLocalPath failure: ", e);
      }
      JellyfinItemExtra4ArticleContent.Actor actor = new JellyfinItemExtra4ArticleContent.Actor();
      actor.setName(peopleDTO.getName());
      actor.setRole(peopleDTO.getRole());
      actor.setPeopleItemId(peopleItemId);
      if (StringUtils.hasText(peopleImageLocalPath)) {
        actor.setImageUrl(uploadItemImage2WeixinMP(peopleImageLocalPath));
      } else { // 如果没有演员图片, 则使用默认占位符
        actor.setImageUrl(defaultActorImg);
      }
      actorList.add(actor);
      current++;
    }
    content.setActorList(actorList);
  }

  /**
   * @return 本地文件路径
   */
  private String saveItemImage2Local(JellyfinWebhookEntity entity, JellyfinItemImageType itemImageType) {
    JellyfinFullControlApiClient client = dynamicFeignClientService.getClient(JellyfinFullControlApiClient.class, CustomAppId.Jellyfin.name());
    if (!StringUtils.hasText(entity.getItemId())) {
      log.error("entity没有itemId直接退出");
      return null;
    }
    String itemId = entity.getItemId();
    // movie和episode分开处理
    if (JellyfinWebhookProperties.ITEM_TYPE_MOVIE.equals(entity.getItemType())) {
      return doImageGetAndSave(client, itemId, itemImageType);
    }
    if (JellyfinWebhookProperties.ITEM_TYPE_SERIES.equals(entity.getItemType())) {
      return doImageGetAndSave(client, itemId, itemImageType);
    }
    return null;
  }

  private String doImageGetAndCropAndSave(ImageCropType cropType, Integer cropWidth, Integer cropHeight,
                                          JellyfinFullControlApiClient client, String itemId, JellyfinItemImageType itemImageType) {
    String inputFilePath = doImageGetAndSave(client, itemId, itemImageType);
    if (!StringUtils.hasText(inputFilePath)) {
      return null;
    }
    String outputFiletPath = null;
    try {
      // 获取ImageUtils类
      Class<?> imageUtilsClass = Class.forName("top.mty.utils.ImageUtils");
      // 获取对应的方法
      Method method = imageUtilsClass.getMethod(cropType.name(), String.class, String.class, int.class, int.class);
      // 调用方法
      outputFiletPath = inputFilePath.split("\\.")[0] + "_cropSquare.jpg";
      method.invoke(null, inputFilePath, outputFiletPath, cropWidth, cropHeight);
    } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
      log.error("doImageGetAndCropAndSave失败: {}", cropType, e);
    }
    return outputFiletPath;
  }

  /**
   * @return 本地文件路径
   */
  private String doImageGetAndSave(JellyfinFullControlApiClient client, String itemId, JellyfinItemImageType itemImageType) {
    String filePath = serverTempData + itemId + "_" + itemImageType + ".jpg";
    try {
      ResponseEntity<byte[]> response = client.getItemImage(itemId, itemImageType.name(), new JellyfinItemImageRequest());
      if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
        byte[] fileContents = response.getBody();
        FileUtil.saveBytes2File(fileContents, filePath);
        log.info("File saved successfully to: {}", filePath);
        return filePath;
      } else {
        log.error("Failed to download file. Status code: {}, itemId: {}", response.getStatusCodeValue(), itemId);
        return null;
      }
    } catch (Exception e) {
      log.error("client.getItemImage error: ", e);
      return null;
    }

  }

  public void updateEntity(JellyfinWebhookEntity entity) {
    mapper.updateById(entity);
  }

  private boolean notificationTypeEnabled(JellyfinWebhookProperties properties) {
    String[] enabledNotificationTypes = this.enabledNotificationTypes.split(";");
    for (String type : enabledNotificationTypes) {
      if (StringUtils.hasText(type) && type.equals(properties.getNotificationType())) {
        return true;
      }
    }
    return false;
  }

  private void insertOrUpdate(JellyfinWebhookEntity entity) {
    entity.setUuid(UUID.randomUUID().toString());
    if (allowed2Database(entity)) {
      JellyfinWebhookEntity existed = duplicatedItem(entity);
      if (null != existed) {
        log.warn("重复的项目: {}, 重新入库: {}", JSON.toJSONString(existed), JSON.toJSONString(entity));
        existed.setProcessed(false);
        existed.setTimestamp(entity.getTimestamp());
        existed.setName(entity.getName());
        existed.setOverview(entity.getOverview());
        // 新itemId才有图片?
        existed.setItemId(entity.getItemId());
        existed.setYear(entity.getYear());
        existed.setSeriesName(entity.getSeriesName());
        existed.setVideo0Title(entity.getVideo0Title());
        updateEntity(entity);
        return;
      }
      mapper.insert(entity);
    }
  }

  /**
   * 允许入库
   *
   * @return true 允许
   */
  private boolean allowed2Database(JellyfinWebhookEntity entity) {
    // 只有刮削过的媒体才有元数据, 才可以计入数据库

    log.debug("MEDIA_BEFORE_DB: {}", entity);

    if (StringUtils.hasText(entity.getOverview())
        && StringUtils.hasText(entity.getName())) {
      // 如果是episode, 还要注意在tmm中重命名之前不能入库
      // 目前发现的区别是没有season_episode
      if (JellyfinWebhookProperties.ITEM_TYPE_EPISODE.equals(entity.getItemType())) {
        if (!StringUtils.hasText(entity.getSeasonEpisode()) || !StringUtils.hasText(entity.getSeriesName())) {
          return false;
        }
      }
      return true;
    }
    return false;
  }

  /**
   * 判断是否是重复的项目
   *
   * @param entity 当前项目
   * @return 已经存在的项目
   */
  private JellyfinWebhookEntity duplicatedItem(JellyfinWebhookEntity entity) {
    if (!entity.getNotificationType().equals(JellyfinWebhookProperties.NOTIFICATION_TYPE_PLAYBACK_ITEM_ADDED)) {
      // 非itemAdded先不处理
      return null;
    }
    JellyfinWebhookEntity existed = null;
    if (JellyfinWebhookProperties.ITEM_TYPE_MOVIE.equals(entity.getItemType())) {
      QueryWrapper<JellyfinWebhookEntity> wrapper = new QueryWrapper<>();
      wrapper.eq("name", entity.getName());
      wrapper.eq("server_name", entity.getServerName());
      wrapper.eq("notification_type", entity.getNotificationType());
      existed = mapper.selectOne(wrapper);
    }
    if (JellyfinWebhookProperties.ITEM_TYPE_EPISODE.equals(entity.getItemType())) {
      QueryWrapper<JellyfinWebhookEntity> wrapper = new QueryWrapper<>();
      wrapper.eq("name", entity.getName());
      wrapper.eq("server_name", entity.getServerName());
      wrapper.eq("series_name", entity.getSeriesName());
      existed = mapper.selectOne(wrapper);
    }
    if (JellyfinWebhookProperties.ITEM_TYPE_SEASON.equals(entity.getItemType())) {
      QueryWrapper<JellyfinWebhookEntity> wrapper = new QueryWrapper<>();
      wrapper.eq("name", entity.getName());
      wrapper.eq("server_name", entity.getServerName());
      existed = mapper.selectOne(wrapper);
    }
    if (null != existed) {
      log.info("{}已存在, 准备调用更新", entity.getName());
      return existed;
    }
    return null;
  }


}
