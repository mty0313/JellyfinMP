package top.mty.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import top.mty.common.CustomAppId;
import top.mty.entity.RemoteServerInfo;
import top.mty.entity.WeixinMPAccessToken;
import top.mty.remote.WeixinMPClient;

@Service
@Slf4j
public class WeixinTokenService {
  @Autowired
  private DynamicFeignClientService feignClientService;
  @Autowired
  private RemoteServerInfoService remoteServerInfoService;
  @Value("${init.weixinmp.serverUrl:https://api.weixin.qq.com}")
  private String initWeixinMPServerUrl;

  public void refreshAccessToken() {
    WeixinMPClient weixinMPClient = feignClientService.getClient(WeixinMPClient.class, CustomAppId.WeixinMP.name());
    WeixinMPAccessToken tokenResponse = weixinMPClient.queryAccessToken();
    if (null != tokenResponse && StringUtils.isNotBlank(tokenResponse.getAccessToken())) {
      RemoteServerInfo remoteServerInfo = remoteServerInfoService.getServerInfoByAppId(CustomAppId.WeixinMPAccessToken.name());
      // 存在accessToken, 且没有对应appId的记录时新增
      if (null == remoteServerInfo) {
        RemoteServerInfo insert = new RemoteServerInfo(CustomAppId.WeixinMPAccessToken, tokenResponse.getAccessToken(), initWeixinMPServerUrl);
        remoteServerInfoService.insertServerInfo(insert);
        log.info("微信公众号accessToken成功新增记录");
        return;
      }
      // 否则更新记录
      remoteServerInfoService.updateAccessToken(remoteServerInfo.getUuid(), tokenResponse.getAccessToken());
      log.info("微信公众号accessToken成功刷新");
    }
  }
}
