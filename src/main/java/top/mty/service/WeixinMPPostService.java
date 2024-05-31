package top.mty.service;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import top.mty.common.CustomAppId;
import top.mty.entity.WeixinMPDraftPost;
import top.mty.job.params.WeixinMPAfterDraft;
import top.mty.mapper.WeixinMPDraftPostMapper;
import top.mty.remote.BarkClient;
import top.mty.remote.WeixinMPClient;
import top.mty.remote.param.*;
import top.mty.service.params.WeixinMPDraftCreateAndPost;
import top.mty.utils.DateUtil;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class WeixinMPPostService {
  @Autowired
  private DynamicFeignClientService feignClientService;
  @Autowired
  private WeixinMPDraftPostMapper draftPostMapper;
  @Autowired
  private JellyfinWebhookService jellyfinWebhookService;
  @Autowired
  private BarkClient barkClient;
  @Value("${bark.weixinMP.draftPostedNotifyDevices:\"\"}")
  private String draftPostedNotifyDevices;
  /**
   * 创建和发布草稿
   */
  public WeixinMPDraftCreateAndPost draftCreateAndPost(WeixinMPDraftCreateRequest request, WeixinMPAfterDraft afterDraft,
                                                       List<String> processedUuids) {
    if (!request.valid()) {
      log.error("草稿箱创建参数不合法: {}", JSON.toJSONString(request));
      return null;
    }
    WeixinMPClient client = feignClientService.getClient(WeixinMPClient.class, CustomAppId.WeixinMP.name());
    String response = client.createDraft(request);
    WeixinMPDraftCreateResponse responseEntity = JSON.to(WeixinMPDraftCreateResponse.class, response);
    if (null == responseEntity || !StringUtils.hasText(responseEntity.getMediaId())) {
      log.error("草稿创建请求失败: {}", JSON.toJSONString(responseEntity));
      return null;
    }
    String mediaId = responseEntity.getMediaId();
    log.info("草稿创建请求成功: " + mediaId);
    // 草稿创建成功后更新数据库中的item的状态为已处理
    if (afterDraft.isUpdateDatabase()) {
      jellyfinWebhookService.updateBatchProcessedByUuids(processedUuids);
    }
    String publishId = "None";
    if (afterDraft.isPost2MpNews()) {
      // 发布草稿到图文, 成功会返回publishId用于轮询是否成功
      publishId = postDraft(mediaId).getPublishId();
      if (StringUtils.hasText(publishId)) {
        WeixinMPDraftPost draftPost = new WeixinMPDraftPost(UUID.randomUUID().toString(),
            mediaId, publishId, false);
        draftPostMapper.insert(draftPost);
      }
    }
    if (afterDraft.isSend2All()) {
      // 群发草稿中的内容
      WeixinMPSend2AllResponse send2AllResponse = weixinMPSend2All(mediaId);
      if (send2AllResponse != null && send2AllResponse.success()) {
        log.info("群发任务提交成功: mediaId: {}, msgId: {}", mediaId, send2AllResponse.getMsgId());
      } else {
        log.error("群发任务提交失败: {}", JSON.toJSONString(send2AllResponse));
      }
    }
    // 如果既不推送为图文也不群发, 则推送bark通知手动处理
    if (!afterDraft.isPost2MpNews() && !afterDraft.isSend2All()) {
      String[] draftPostedNotifyDevices = this.draftPostedNotifyDevices.split(",");
      for (String device : draftPostedNotifyDevices) {
        barkClient.pushMsg(device, String.format("Jellyfin %s 更新", DateUtil.toStandardYMD(new Date())), String.format("共计处理%s条记录", processedUuids.size()));
      }
    }
    return new WeixinMPDraftCreateAndPost(afterDraft.isPost2MpNews(), afterDraft.isSend2All(), mediaId, publishId);
  }

  /**
   * 根据publishId查询是否发布成功
   */
  public boolean publishQuery(String publishId) {
    if (!StringUtils.hasText(publishId)) {
      log.warn("没有publishId退出查询");
      return false;
    }
    WeixinMPClient client = feignClientService.getClient(WeixinMPClient.class, CustomAppId.WeixinMP.name());
    String response = client.queryPublishResult(new WeixinMPPublishResultQueryRequest(publishId));
    WeixinMPPublishResultQueryResponse responseEntity = JSON.to(WeixinMPPublishResultQueryResponse.class, response);
    if (null == responseEntity) {
      log.warn("WeixinMPPublishResultQueryResponse转换失败: {}", response);
      return false;
    }
    if (!StringUtils.hasText(responseEntity.getPublishId())) {
      log.warn("queryPublishResult没有返回publish_id, 查询参数: {}", publishId);
      return false;
    }
    if (!responseEntity.publishSucceeded()) {
      log.warn("发布失败: {}, {}", publishId, response);
    }
    return responseEntity.publishSucceeded();
  }

  public List<WeixinMPDraftPost> queryByWrapper(QueryWrapper<WeixinMPDraftPost> wrapper) {
    return draftPostMapper.selectList(wrapper);
  }

  public void updateDraftPost(WeixinMPDraftPost draftPost) {
    draftPostMapper.updateById(draftPost);
  }

  public WeixinMPDraftPost getByPublishId(String publishId) {
    QueryWrapper<WeixinMPDraftPost> wrapper = new QueryWrapper<>();
    wrapper.eq("publish_id", publishId);
    return draftPostMapper.selectOne(wrapper);
  }

  /**
   * 发布草稿, 任务提交成功则返回publish_id
   */
  private WeixinMPDraftPost postDraft(String mediaId) {
    WeixinMPClient client = feignClientService.getClient(WeixinMPClient.class, CustomAppId.WeixinMP.name());
    String response = client.draftPost(new WeixinMPDraftPostRequest(mediaId));
    WeixinMPDraftPostResponse responseEntity = JSON.to(WeixinMPDraftPostResponse.class, response);
    WeixinMPDraftPost draftPost = new WeixinMPDraftPost(UUID.randomUUID().toString(),
        mediaId, null, false);
    if (null != responseEntity && responseEntity.success()) {
      log.info("图文发布成功: {}", responseEntity.getPublishId());
      draftPost.setPublishId(responseEntity.getPublishId());
      return draftPost;
    }
    log.info("图文发布失败: {}", mediaId);
    return draftPost;
  }

  private WeixinMPSend2AllResponse weixinMPSend2All(String mediaId) {
    WeixinMPClient client = feignClientService.getClient(WeixinMPClient.class, CustomAppId.WeixinMP.name());
    return client.send2All(new WeixinMPSend2AllRequest(mediaId));
  }
}
