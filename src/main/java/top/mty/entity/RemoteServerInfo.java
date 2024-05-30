package top.mty.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;
import top.mty.common.CustomAppId;

import java.util.Date;
import java.util.UUID;

@Data
public class RemoteServerInfo {
  @TableId
  private String uuid;
  private Date created;
  private Date modified;
  private String appId;
  private String accessToken;
  private String serverUrl;

  public RemoteServerInfo () {
    this.uuid = UUID.randomUUID().toString();
    this.created = new Date();
    this.modified = new Date();
  }

  public RemoteServerInfo(CustomAppId customAppId, String accessToken, String serverUrl) {
    this.uuid = UUID.randomUUID().toString();
    this.created = new Date();
    this.modified = new Date();
    this.appId = customAppId.name();
    this.accessToken = accessToken;
    this.serverUrl = serverUrl;
  }

  public boolean valid() {
    return StringUtils.hasText(appId) && StringUtils.hasText(accessToken);
  }
}
