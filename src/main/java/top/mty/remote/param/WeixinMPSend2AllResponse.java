package top.mty.remote.param;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class WeixinMPSend2AllResponse {

  @JsonProperty("errcode")
  private int errcode;
  @JsonProperty("errmsg")
  private String errmsg;
  @JsonProperty("msg_id")
  private Integer msgId;
  @JsonProperty("msg_data_id")
  private Integer msgDataId;

  public boolean success() {
    return this.errcode == 0;
  }
}
