package top.mty.remote.param;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@NoArgsConstructor
@Data
public class JellyfinItemExtra4ArticleContent {
  @JsonProperty("OfficialRating")
  private String officialRating;
  @JsonProperty("CommunityRating")
  private BigDecimal communityRating = BigDecimal.ZERO;
  private List<Actor> actorList;

  public void setCommunityRating(String rating) {
    if (StringUtils.hasText(rating)) {
      this.communityRating = new BigDecimal(rating).setScale(1, RoundingMode.HALF_UP);
    }
  }

  @Data
  public static class Actor {
    String peopleItemId;
    String name;
    String imageUrl;
    String role;
  }
}
