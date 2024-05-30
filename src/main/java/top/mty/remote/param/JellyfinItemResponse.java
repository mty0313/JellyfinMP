package top.mty.remote.param;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class JellyfinItemResponse {

  @JsonProperty("Items")
  private List<ItemsDTO> items;
  @JsonProperty("TotalRecordCount")
  private Integer totalRecordCount;
  @JsonProperty("StartIndex")
  private Integer startIndex;

  @NoArgsConstructor
  @Data
  public static class ItemsDTO {
    @JsonProperty("Name")
    private String name;
    @JsonProperty("ServerId")
    private String serverId;
    @JsonProperty("Id")
    private String id;
    @JsonProperty("Container")
    private String container;
    @JsonProperty("PremiereDate")
    private String premiereDate;
    @JsonProperty("ChannelId")
    private Object channelId;
    @JsonProperty("CommunityRating")
    private String communityRating;
    // "OfficialRating": "TV-MA",
    @JsonProperty("OfficialRating")
    private String officialRating;
    @JsonProperty("RunTimeTicks")
    private Long runTimeTicks;
    @JsonProperty("ProductionYear")
    private Integer productionYear;
    @JsonProperty("IndexNumber")
    private Integer indexNumber;
    @JsonProperty("ParentIndexNumber")
    private Integer parentIndexNumber;
    @JsonProperty("IsFolder")
    private Boolean isFolder;
    @JsonProperty("Type")
    private String type;
    @JsonProperty("UserData")
    private UserDataDTO userData;
    @JsonProperty("SeriesName")
    private String seriesName;
    @JsonProperty("SeriesId")
    private String seriesId;
    @JsonProperty("SeasonId")
    private String seasonId;
    @JsonProperty("PrimaryImageAspectRatio")
    private Double primaryImageAspectRatio;
    @JsonProperty("SeriesPrimaryImageTag")
    private String seriesPrimaryImageTag;
    @JsonProperty("SeasonName")
    private String seasonName;
    @JsonProperty("VideoType")
    private String videoType;
    @JsonProperty("ImageBlurHashes")
    private ImageBlurHashesDTO imageBlurHashes;
    @JsonProperty("LocationType")
    private String locationType;
    @JsonProperty("MediaType")
    private String mediaType;

    @NoArgsConstructor
    @Data
    public static class UserDataDTO {
      @JsonProperty("PlaybackPositionTicks")
      private Integer playbackPositionTicks;
      @JsonProperty("PlayCount")
      private Integer playCount;
      @JsonProperty("IsFavorite")
      private Boolean isFavorite;
      @JsonProperty("Played")
      private Boolean played;
      @JsonProperty("Key")
      private String key;
    }

    @NoArgsConstructor
    @Data
    public static class ImageBlurHashesDTO {
      @JsonProperty("Primary")
      private PrimaryDTO primary;

      @NoArgsConstructor
      @Data
      public static class PrimaryDTO {
        @JsonProperty("abef2b8854a2de3b12f9a28557475a55")
        private String abef2b8854a2de3b12f9a28557475a55;
      }
    }
  }
}
