package top.mty.remote.param;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.List;

@NoArgsConstructor
@Data
public class WeixinMPAddMaterialResponse {

  @JsonProperty("media_id")
  private String mediaId;
  @JsonProperty("url")
  private String url;
  @JsonProperty("item")
  private List<?> item;

  public boolean success() {
    return StringUtils.hasText(this.mediaId);
  }
}
