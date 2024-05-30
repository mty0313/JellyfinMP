package top.mty.remote.param;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class WeixinMPSend2AllRequest {

  @JsonProperty("filter")
  private FilterDTO filter;
  @JsonProperty("mpnews")
  private MpnewsDTO mpnews;
  @JsonProperty("msgtype")
  private String msgtype = "mpnews";
  @JsonProperty("send_ignore_reprint")
  private Integer sendIgnoreReprint = 1;

  @NoArgsConstructor
  @Data
  public static class FilterDTO {
    @JsonProperty("is_to_all")
    private Boolean isToAll = true;
  }

  @NoArgsConstructor
  @Data
  public static class MpnewsDTO {
    @JsonProperty("media_id")
    private String mediaId;
  }

  public WeixinMPSend2AllRequest(String mediaId) {
    MpnewsDTO mpNews = new MpnewsDTO();
    mpNews.setMediaId(mediaId);
    this.filter = new FilterDTO();
    this.mpnews = mpNews;
  }
}
