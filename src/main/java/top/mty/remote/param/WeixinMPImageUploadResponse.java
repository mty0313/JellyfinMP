package top.mty.remote.param;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@NoArgsConstructor
@Data
public class WeixinMPImageUploadResponse {

  @JsonProperty("url")
  private String url;

  public boolean success() {
    return StringUtils.hasText(this.url);
  }
}
