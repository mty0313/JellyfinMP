package top.mty.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import top.mty.common.Assert;
import top.mty.common.CustomAppId;
import top.mty.entity.RemoteServerInfo;
import top.mty.mapper.RemoteServerInfoMapper;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class RemoteServerInfoService extends ServiceImpl<RemoteServerInfoMapper, RemoteServerInfo> {
  @Autowired
  private RemoteServerInfoMapper remoteServerInfoMapper;
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
  public void init() {
    // 运行时必须要有remoteServerInfo的参数，每次启动时都会更新
    Assert.notEmpty(initJellyfinServerUrl, "initJellyfinServerUrl");
    Assert.notEmpty(initJellyfinToken, "initJellyfinToken");
    Assert.notEmpty(initWeixinMPAppId, "initWeixinMPAppId");
    Assert.notEmpty(initWeixinMPAppSecret, "initWeixinMPAppSecret");
    Assert.notEmpty(initWeixinMPServerUrl, "initWeixinMPServerUrl");
    remoteServerInfoMapper.delete(new QueryWrapper<>());
    RemoteServerInfo jellyfin = new RemoteServerInfo(CustomAppId.Jellyfin, initJellyfinToken, initJellyfinServerUrl);
    insertServerInfo(jellyfin);
    RemoteServerInfo weixinMP = new RemoteServerInfo(CustomAppId.WeixinMP,
        initWeixinMPAppId + "," + initWeixinMPAppSecret, initWeixinMPServerUrl);
    insertServerInfo(weixinMP);
  }

  public RemoteServerInfo getServerInfoByAppId(String appId) {
    QueryWrapper<RemoteServerInfo> wrapper = new QueryWrapper<>();
    wrapper.eq("app_id", appId);
    List<RemoteServerInfo> result = remoteServerInfoMapper.selectList(wrapper);
    if (CollectionUtils.isEmpty(result)) {
      return null;
    }
    return result.get(0);
  }

  public void insertServerInfo(RemoteServerInfo remoteServerInfo) {
    if (!StringUtils.hasText(remoteServerInfo.getUuid())) {
      remoteServerInfo.setUuid(UUID.randomUUID().toString());
    }
    Date now = new Date();
    remoteServerInfo.setCreated(now);
    remoteServerInfo.setModified(now);
    if (!remoteServerInfo.valid()) {
      throw new RuntimeException("token param not valid");
    }
    remoteServerInfoMapper.insert(remoteServerInfo);
  }

  public void updateAccessToken(String uuid, String accessToken) {
    if (!StringUtils.hasText(uuid)) {
      log.error("需要更新的remoteServerInfo没有uuid");
      return;
    }
    RemoteServerInfo remoteServerInfo = remoteServerInfoMapper.selectById(uuid);
    if (null == remoteServerInfo) {
      log.error("remoteServerInfo没有查到: " + uuid);
      return;
    }
    remoteServerInfo.setAccessToken(accessToken);
    remoteServerInfo.setModified(new Date());
    remoteServerInfoMapper.updateById(remoteServerInfo);
  }

}
