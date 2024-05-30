package top.mty.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class WeixinMPAccessToken {

  @JsonProperty("access_token")
  private String accessToken;
  @JsonProperty("expires_in")
  private Integer expiresIn;
}
