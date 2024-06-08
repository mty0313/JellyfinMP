package top.mty.service;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import top.mty.common.CustomAppId;
import top.mty.common.JellyfinMPException;
import top.mty.controller.data.jellyfin.webhook.JellyfinWebhookProperties;
import top.mty.entity.JellyfinWebhookEntity;
import top.mty.job.params.WeixinMPAfterDraft;
import top.mty.remote.JellyfinFullControlApiClient;
import top.mty.remote.param.JellyfinItemCount;
import top.mty.remote.param.JellyfinItemExtra4ArticleContent;
import top.mty.remote.param.JellyfinUserLibraryItem;
import top.mty.remote.param.WeixinMPDraftCreateRequest;
import top.mty.service.params.WeixinMPDraftCreateAndPost;
import top.mty.utils.DateUtil;
import top.mty.utils.RegexUtil;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class WeixinMPDraftService {

  @Autowired
  private BarkService barkService;
  @Autowired
  private JellyfinWebhookService webhookService;
  @Autowired
  private WeixinMPPostService postService;
  @Autowired
  private DynamicFeignClientService dynamicFeignClientService;
  @Value("${bark.weixinMP.draftPostedNotifyDevices:\"\"}")
  private String draftPostedNotifyDevices;
  @Value("${jellyfin.adminId}")
  private String jellyfinAdminId;

  public static final String MATERIAL_TYPE_THUMB = "thumb";

  private static final String RESOLUTION = "\\b\\d{3,4}(?i)p\\b|\\b4(?i)k\\b";

  public void createDraft(WeixinMPAfterDraft afterDraft) throws JellyfinMPException {
    Date now = new Date();
    Date start = DateUtils.addDays(now, -2);
    QueryWrapper<JellyfinWebhookEntity> wrapper = new QueryWrapper<>();
    wrapper.ge("timestamp", start);
    wrapper.eq("processed", false);
    wrapper.eq("notification_type", JellyfinWebhookProperties.NOTIFICATION_TYPE_PLAYBACK_ITEM_ADDED);
    List<JellyfinWebhookEntity> itemAddedRawList = webhookService.getEntities(wrapper);
    if (CollectionUtils.isEmpty(itemAddedRawList)) {
      log.info("没有新增媒体: {} - {}", start, now);
      String[] draftPostedNotifyDevices = this.draftPostedNotifyDevices.split(",");
      if (CollectionUtils.isEmpty(Arrays.asList(draftPostedNotifyDevices))) {
        log.info("Bark推送设备列表为空忽略推送");
      }
      for (String device : draftPostedNotifyDevices) {
        barkService.pushMsg(device, String.format("Jellyfin %s 更新", DateUtil.toStandardYMD(new Date())), "没有新增媒体内容");
      }
      return;
    }
    WeixinMPDraftCreateRequest draftCreateRequest = webhookEntity2DraftCreate(itemAddedRawList);
    if (null != draftCreateRequest) {
      List<String> processedUuids = itemAddedRawList.stream().map(JellyfinWebhookEntity::getUuid).collect(Collectors.toList());
      WeixinMPDraftCreateAndPost draft = postService.draftCreateAndPost(draftCreateRequest, afterDraft, processedUuids);
      log.debug("draft is: {}", JSON.toJSONString(draft));
    }
  }

  private WeixinMPDraftCreateRequest webhookEntity2DraftCreate(List<JellyfinWebhookEntity> rawEntityList) throws JellyfinMPException {
    List<JellyfinWebhookEntity> processedEntities = processEntities(rawEntityList);
    if (CollectionUtils.isEmpty(processedEntities)) {
      log.info("Jellyfin新增媒体为空");
      return null;
    }
    WeixinMPDraftCreateRequest draftCreate = new WeixinMPDraftCreateRequest();
    WeixinMPDraftCreateRequest.ArticlesDTO article = new WeixinMPDraftCreateRequest.ArticlesDTO().defaultJellyfinItemAddArticle();
    article.setThumbMediaId(generateThumbMediaId(processedEntities));
    draftCreate.getArticles().add(article);
    // 生成文章内容
    String articleContent = processArticleContent(processedEntities);
    if (!StringUtils.hasText(articleContent)) {
      return null;
    }
    article.setContent(articleContent);
    return draftCreate;
  }

  private String generateThumbMediaId(List<JellyfinWebhookEntity> entityList) throws JellyfinMPException {
    for (JellyfinWebhookEntity entity : entityList) {
      String mediaId = webhookService.uploadBackdrop2WeixinMP(entity, MATERIAL_TYPE_THUMB);
      if (StringUtils.hasText(mediaId)) {
        return mediaId;
      }
    }
    throw new JellyfinMPException("生成封面失败");
  }

  /**
   * 处理最终合并处理过的JellyfinWebhookEntity, 用于生成文章内容
   */
  private String processArticleContent(List<JellyfinWebhookEntity> processedEntities) {
    // 将日期更早的排在前面
    processedEntities.sort(Comparator.comparing(JellyfinWebhookEntity::getTimestamp));
    StringBuilder sb = new StringBuilder();
    // 添加电影项目
    StringBuilder movieContent = new StringBuilder();
    if (processedEntities.stream().anyMatch(p -> JellyfinWebhookProperties.ITEM_TYPE_MOVIE.equals(p.getItemType()))) {
      movieContent.append("<h2>电影:</h2><br>");
      int index = 1;
      for (JellyfinWebhookEntity entity : processedEntities) {
        if (JellyfinWebhookProperties.ITEM_TYPE_MOVIE.equals(entity.getItemType())) {
          // 添加标题
          movieContent.append(index).append(". ").append(entity.getName()).append("(").append(entity.getYear()).append(") ")
              .append(findResolution(entity));
          // 添加分级和评分
          fetchExtra4ArticleContent(entity, movieContent);
          movieContent.append("<br>");
          // 添加图片
          try {
            String imageUrl = processEntityImage(entity);
            if (StringUtils.hasText(imageUrl)) {
              movieContent.append(imageUrl).append("<br>");
            }
          } catch (Exception e) {
            log.error("处理entity图片出现错误: {}", entity.getName(), e);
          } finally {
            movieContent.append("<br>");
          }
          // 添加描述
          movieContent.append(entity.getOverview()).append("<br>");
          // 添加演员图片
          fetchActors4ArticleContent(entity, movieContent);
          index++;
        }
      }
    }
    // 添加电视剧项目
    StringBuilder episodesContent = new StringBuilder();
    if (processedEntities.stream().anyMatch(p -> JellyfinWebhookProperties.ITEM_TYPE_SERIES.equals(p.getItemType()))) {
      episodesContent.append("<h2>剧集:</h2><br>");
      int index = 1;
      for (JellyfinWebhookEntity entity : processedEntities) {
        if (JellyfinWebhookProperties.ITEM_TYPE_SERIES.equals(entity.getItemType())) {
          // 添加标题
          episodesContent.append(index).append(". ").append(entity.getName()).append("(").append(entity.getYear()).append(") ");
          // 添加分级和评分
          fetchExtra4ArticleContent(entity, episodesContent);
          // 添加视频格式
          episodesContent.append(" 🎬").append(findResolution(entity));

          episodesContent.append("<br>");
          // 添加图片
          try {
            String imageUrl = processEntityImage(entity);
            if (StringUtils.hasText(imageUrl)) {
              episodesContent.append(imageUrl).append("<br>");
            }
          } catch (Exception e) {
            log.error("处理entity图片出现错误: {}", entity.getName(), e);
          } finally {
            episodesContent.append("<br>");
          }
          // 添加描述
          if (StringUtils.hasText(entity.getOverview())) {
            episodesContent.append(entity.getOverview()).append("<br>");
          }
          // 添加演员图片
          fetchActors4ArticleContent(entity, episodesContent);
          if (StringUtils.hasText(entity.getSeasonEpisode())) {
            episodesContent.append("更新单集如下: ").append(entity.getSeasonEpisode()).append("<br>");
          }
          index++;
        }
      }
    }
    sb.append(StringUtils.hasText(movieContent.toString()) ? movieContent.toString() : "")
        .append(StringUtils.hasText(episodesContent.toString()) ? episodesContent.toString() : "");
    if (!StringUtils.hasText(sb.toString())) {
      return null;
    }
    addFooter(sb);
    return sb.toString();
  }

  private void addFooter(StringBuilder sb) {
    JellyfinItemCount itemCount = webhookService.getItemCount();
    if (null != itemCount) {
      sb.append("<br><strong>Jellyfin已收录").append(itemCount.getMovieCount()).append("部电影, ")
          .append(itemCount.getSeriesCount()).append("个剧集(").append(itemCount.getEpisodeCount()).append("单集). <strong><br>")
          .append("<br><i>以上内容依据 <strong>TMDB</strong> 由 <strong>JellyfinMP:jaxwang-branch</strong> 自动生成. </i><br>");
    }
  }

  private String findResolution(JellyfinWebhookEntity entity) {
    if (!StringUtils.hasText(entity.getVideo0Title())) {
      return "";
    }
    // todo 从userLibraryItem中获取准确的视频信息
    // String resolution = RegexUtil.findStrByPattern(entity.getVideo0Title(), RESOLUTION);
    String resolution = entity.getVideo0Title(); //获取全部信息， 不需要正则匹配
    if (StringUtils.hasText(resolution)) {
      return resolution + " ";
    }
    return "";
  }

  private void fetchActors4ArticleContent(JellyfinWebhookEntity entity, StringBuilder articleContent) {
    JellyfinItemExtra4ArticleContent extra4ArticleContent = webhookService.fetchItemExtra(entity);
    if (null == extra4ArticleContent) {
      return;
    }
    if (CollectionUtils.isEmpty(extra4ArticleContent.getActorList())) {
      return;
    }
    articleContent.append("<strong>演职人员(滑动查看):</strong><br><br>");
    articleContent.append("<section style=\"overflow-x: auto; white-space: nowrap;\">");
    String actorImage = "<section style=\"display: inline-block; vertical-align: top; text-align: center; padding: 10px; width: 120px; white-space: normal;\"><img src=\"%s\" alt=\"%s\" style=\"width: 100px; height: 100px;\"><p style=\"font-size: 10px;\">%s<br>饰演: %s</p></section>";
    extra4ArticleContent.getActorList().forEach(a -> articleContent.append(String.format(actorImage, a.getImageUrl(), a.getName(), a.getName(), a.getRole())));
    articleContent.append("</section><br>");
  }

  private void fetchExtra4ArticleContent (JellyfinWebhookEntity entity, StringBuilder articleContent) {
    JellyfinItemExtra4ArticleContent extra4ArticleContent = webhookService.fetchItemExtra(entity);
    if (null == extra4ArticleContent) {
      return;
    }
    if (StringUtils.hasText(extra4ArticleContent.getOfficialRating())) {
      articleContent.append("[").append(extra4ArticleContent.getOfficialRating()).append("]").append(" ");
    }
    if (null != extra4ArticleContent.getCommunityRating() && !BigDecimal.ZERO.equals(extra4ArticleContent.getCommunityRating())) {
      articleContent.append("⭐️").append(extra4ArticleContent.getCommunityRating());
    }
  }

  /**
   一个一个获取图片, 一个一个上传, 返回上传成功的url
   */
  private String processEntityImage(JellyfinWebhookEntity entity) {
    String imgTag = "<img src=\"%s\">";
    String imgUrl = webhookService.uploadItemImage2WeixinMP(entity);
    return String.format(imgTag, imgUrl);
  }

  private List<JellyfinWebhookEntity> processEntities(List<JellyfinWebhookEntity> rawEntityList) {
    List<JellyfinWebhookEntity> movies = new ArrayList<>();
    List<JellyfinWebhookEntity> episodes = new ArrayList<>();
    List<JellyfinWebhookEntity> seriesList = new ArrayList<>();
    List<JellyfinWebhookEntity> seasons = new ArrayList<>();
    List<JellyfinWebhookEntity> filterEntities = filterEntitiesByItemId(rawEntityList);
    // 按照type添加到各自的list中
    for (JellyfinWebhookEntity entity : filterEntities) {
      entity.setProcessed(true);
      if (JellyfinWebhookProperties.ITEM_TYPE_MOVIE.equals(entity.getItemType())) {
        movies.add(entity);
      }
      if (JellyfinWebhookProperties.ITEM_TYPE_SERIES.equals(entity.getItemType())) {
        seriesList.add(entity);
      }
      // 单集添加到单集
      if (JellyfinWebhookProperties.ITEM_TYPE_EPISODE.equals(entity.getItemType())) {
        episodes.add(entity);
      }
      if (JellyfinWebhookProperties.ITEM_TYPE_SEASON.equals(entity.getItemType())) {
        seasons.add(entity);
      }
    }
    // 初始化结果列表
    // 电影直接添加
    List<JellyfinWebhookEntity> processedEntities = new ArrayList<>(movies);
    // 系列检查本次查出的记录中是否有单集的记录
    // 按照单集循环处理
    Map<String, List<JellyfinWebhookEntity>> seriesEpisodesMap = new HashMap<>();
    List<JellyfinWebhookEntity> orphanEpisodes = new ArrayList<>();
    for (JellyfinWebhookEntity episode : episodes) {
      boolean episodeIsOrphan = true;
      for (JellyfinWebhookEntity series : seriesList) {
        String seriesName = series.getName();
        if (episode.getSeriesName().equals(series.getName())) {
          if (CollectionUtils.isEmpty(seriesEpisodesMap.get(seriesName))) {
            seriesEpisodesMap.put(seriesName, new ArrayList<>());
          }
          seriesEpisodesMap.get(seriesName).add(episode);
          episodeIsOrphan = false;
          break;
        }
      }
      if (episodeIsOrphan) {
        orphanEpisodes.add(episode);
      }
    }
    // 处理seriesEpisodesMap, 修改series数据, 添加剧集信息S01E01, S01E02这样, 最终有用的,用于生成文章的只是series信息.
    // 现在处理的series都是seriesList中已有的记录, 所以直接做set操作就行
    for (Map.Entry<String, List<JellyfinWebhookEntity>> entry : seriesEpisodesMap.entrySet()) {
      String seriesName = entry.getKey();
      for (JellyfinWebhookEntity seriesEntity : seriesList) {
        if (seriesName.equals(seriesEntity.getName())) {
          seriesEntity.setVideo0Title(entry.getValue().stream().map(JellyfinWebhookEntity::getVideo0Title).findAny().orElse(null));
          String addedSeasonEpisode = entry.getValue().stream().map(JellyfinWebhookEntity::getSeasonEpisode).collect(Collectors.joining(","));
          seriesEntity.setSeasonEpisode(addedSeasonEpisode);
        }
      }
    }
    // orphanEpisodes 只有单集没有剧集信息的给他查出来series信息, 最终构造series添加到series列表中, 因为最终用来生成文章的还是series信息.
    JellyfinFullControlApiClient client = dynamicFeignClientService.getClient(JellyfinFullControlApiClient.class, CustomAppId.Jellyfin.name());
    for (JellyfinWebhookEntity orphanEpisode : orphanEpisodes) {
      JellyfinUserLibraryItem item = client.getUserLibraryItem(jellyfinAdminId, orphanEpisode.getItemId());
      if (null == item) {
        log.error("查无此episode: {}", orphanEpisode.getItemId());
        continue;
      }
      JellyfinUserLibraryItem orphanEpisodeSeriesItem = client.getUserLibraryItem(jellyfinAdminId, item.getSeriesId());
      // 查出来的series添加到series列表
      JellyfinWebhookEntity madeSeries = JellyfinWebhookEntity.toWebhookEntity(orphanEpisodeSeriesItem, orphanEpisode);
      JellyfinWebhookEntity seriesExistedInList = seriesList.stream().filter(s -> madeSeries.getSeriesName().equals(s.getName())).findFirst().orElse(null);
      if (null != seriesExistedInList) {
        String seriesSeasonEpisode = seriesExistedInList.getSeasonEpisode();
        if (StringUtils.hasText(seriesSeasonEpisode)) {
          seriesExistedInList.setSeasonEpisode(seriesExistedInList.getSeasonEpisode() + "," + orphanEpisode.getSeasonEpisode());
        } else {
          seriesExistedInList.setSeasonEpisode(orphanEpisode.getSeasonEpisode());
        }
      } else {
        madeSeries.setSeasonEpisode(orphanEpisode.getSeasonEpisode());
        seriesList.add(madeSeries);
      }
    }
    for (JellyfinWebhookEntity entity : seriesList) {
      if (!StringUtils.hasText(entity.getSeasonEpisode())) {
        continue;
      }
      String[] seasonEpisodesArray = entity.getSeasonEpisode().split(",");
      List<String> seasonEpisodeList = Arrays.asList(seasonEpisodesArray);
      if (CollectionUtils.isEmpty(seasonEpisodeList)) {
        continue;
      }
      seasonEpisodeList.sort(String::compareTo);
      entity.setSeasonEpisode(String.join(", ", seasonEpisodeList));
    }
    processedEntities.addAll(seriesList);
    return processedEntities;
  }

  /**
   * 过滤相同的itemId, 保留timestamp更近的
   */
  private List<JellyfinWebhookEntity> filterEntitiesByItemId(List<JellyfinWebhookEntity> entityList) {
    return new ArrayList<>(entityList.stream()
        .collect(Collectors.toMap(JellyfinWebhookEntity::getItemId, entity -> entity, (existing, replacement) -> {
          // 如果有相同的itemId，比较timestamp并保留更新的
          return existing.getTimestamp().after(replacement.getTimestamp()) ? existing : replacement;
        }))
        .values());
  }
}
