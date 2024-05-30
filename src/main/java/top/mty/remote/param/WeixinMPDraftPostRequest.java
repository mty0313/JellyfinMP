package top.mty.remote.param;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class WeixinMPDraftPostRequest {
  @JsonProperty("media_id")
  private String mediaId;
}
