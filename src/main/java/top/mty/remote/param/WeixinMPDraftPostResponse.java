package top.mty.remote.param;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@NoArgsConstructor
@Data
public class WeixinMPDraftPostResponse {

  @JsonProperty("errcode")
  private int errCode;
  @JsonProperty("errmsg")
  private String errMsg;
  @JsonProperty("publish_id")
  private String publishId;

  public boolean success() {
    return this.errCode == 0 && StringUtils.hasText(this.publishId);
  }
}
