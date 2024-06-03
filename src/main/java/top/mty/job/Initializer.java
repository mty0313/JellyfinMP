package top.mty.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import top.mty.common.Assert;
import top.mty.common.CustomAppId;
import top.mty.common.JellyfinMPException;
import top.mty.entity.RemoteServerInfo;
import top.mty.service.RemoteServerInfoService;

import javax.annotation.PostConstruct;
import java.util.Arrays;

@Component
@DependsOn("dataSourceInitializer")
@Slf4j
public class Initializer {
  @Autowired
  private RemoteServerInfoService remoteServerInfoService;
  @Autowired
  private WeixinMPJob weixinMPJob;
  @Value("${init.jellyfin.serverUrl}")
  private String initJellyfinServerUrl;
  @Value("${init.jellyfin.token}")
  private String initJellyfinToken;
  @Value("${init.weixinmp.appId}")
  private String initWeixinMPAppId;
  @Value("${init.weixinmp.appSecret}")
  private String initWeixinMPAppSecret;
  @Value("${init.weixinmp.serverUrl:https://api.weixin.qq.com}")
  private String initWeixinMPServerUrl;

  @PostConstruct
  public void init() throws JellyfinMPException {
    try {
      initServerParams();
      tokenRefresh();
      createDraft();
    } catch (Exception e) {
      log.error("初始化任务失败: {}", e.getMessage());
    }

  }

  private void initServerParams() {
    // 运行时必须要有remoteServerInfo的参数，每次启动时都会更新
    Assert.notEmpty(initJellyfinServerUrl, "initJellyfinServerUrl");
    Assert.notEmpty(initJellyfinToken, "initJellyfinToken");
    Assert.notEmpty(initWeixinMPAppId, "initWeixinMPAppId");
    Assert.notEmpty(initWeixinMPAppSecret, "initWeixinMPAppSecret");
    Assert.notEmpty(initWeixinMPServerUrl, "initWeixinMPServerUrl");
    QueryWrapper<RemoteServerInfo> wrapper = new QueryWrapper<>();
    wrapper.in("app_id", Arrays.asList(CustomAppId.Jellyfin.name(), CustomAppId.WeixinMP.name()));
    remoteServerInfoService.delete(wrapper);
    RemoteServerInfo jellyfin = new RemoteServerInfo(CustomAppId.Jellyfin, initJellyfinToken, initJellyfinServerUrl);
    remoteServerInfoService.insertServerInfo(jellyfin);
    RemoteServerInfo weixinMP = new RemoteServerInfo(CustomAppId.WeixinMP,
        initWeixinMPAppId + ";" + initWeixinMPAppSecret, initWeixinMPServerUrl);
    remoteServerInfoService.insertServerInfo(weixinMP);
  }

  private void createDraft() throws JellyfinMPException {
    weixinMPJob.draftCreate();
  }

  private void tokenRefresh() {
    weixinMPJob.tokenRefresh();
  }
}
