package top.mty.common;

import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public enum CustomAppId {
  Jellyfin, WeixinMP, WeixinMPAccessToken;

  public static CustomAppId getByName(String name) {
    if (!StringUtils.hasText(name)) {
      return null;
    }
    for (CustomAppId instanceId : CustomAppId.values()) {
      if (name.equals(instanceId.name())) {
        return instanceId;
      }
    }
    return null;
  }

  public static List<CustomAppId> getByNames(List<String> names) {
    List<CustomAppId> instanceIds = new ArrayList<>();
    for (String name : names) {
      CustomAppId instanceId = getByName(name);
      if (null != instanceId) {
        instanceIds.add(instanceId);
      }
    }
    return instanceIds;
  }
}
