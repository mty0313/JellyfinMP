package top.mty.remote.param;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class WeixinMPPublishResultQueryResponse {
  @JsonProperty("publish_id")
  private String publishId;
  @JsonProperty("publish_status")
  private int publishStatus;
  @JsonProperty("article_id")
  private String articleId;
  @JsonProperty("article_detail")
  private ArticleDetailDTO articleDetail;
  @JsonProperty("fail_idx")
  private List<Integer> failIdx;

  @NoArgsConstructor
  @Data
  public static class ArticleDetailDTO {
    @JsonProperty("count")
    private Integer count;
    @JsonProperty("item")
    private List<ItemDTO> item;

    @NoArgsConstructor
    @Data
    public static class ItemDTO {
      @JsonProperty("idx")
      private Integer idx;
      @JsonProperty("article_url")
      private String articleUrl;
    }
  }

  public boolean publishSucceeded() {
    return 0 == this.publishStatus;
  }
}
