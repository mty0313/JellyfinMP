package top.mty.remote.param;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class WeixinMPDraftCreateResponse {
  /**
   * 上传后的获取标志，长度不固定，但不会超过 128 字符
   */
  @JsonProperty("media_id")
  private String mediaId;
  @JsonProperty("errcode")
  private String errCode;
  @JsonProperty("errmsg")
  private String errMsg;
}
