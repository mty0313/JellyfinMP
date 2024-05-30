package top.mty.remote.param;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class WeixinMPParams extends BaseServerParams {
  private String appId;
  private String appSecret;
  private String accessToken;
}
