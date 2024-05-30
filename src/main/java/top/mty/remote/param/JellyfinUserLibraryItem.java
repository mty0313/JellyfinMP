package top.mty.remote.param;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Data
public class JellyfinUserLibraryItem {

  @JsonProperty("Name")
  private String name;
  @JsonProperty("OriginalTitle")
  private String originalTitle;
  @JsonProperty("ServerId")
  private String serverId;
  @JsonProperty("Id")
  private String id;
  @JsonProperty("Etag")
  private String etag;
  @JsonProperty("DateCreated")
  private String dateCreated;
  @JsonProperty("CanDelete")
  private Boolean canDelete;
  @JsonProperty("CanDownload")
  private Boolean canDownload;
  @JsonProperty("Container")
  private String container;
  @JsonProperty("SortName")
  private String sortName;
  @JsonProperty("PremiereDate")
  private String premiereDate;
  @JsonProperty("ExternalUrls")
  private List<ExternalUrlsDTO> externalUrls;
  @JsonProperty("MediaSources")
  private List<MediaSourcesDTO> mediaSources;
  @JsonProperty("ProductionLocations")
  private List<String> productionLocations;
  @JsonProperty("Path")
  private String path;
  @JsonProperty("EnableMediaSourceDisplay")
  private Boolean enableMediaSourceDisplay;
  @JsonProperty("ChannelId")
  private Object channelId;
  @JsonProperty("Overview")
  private String overview;
  @JsonProperty("Taglines")
  private List<?> taglines;
  @JsonProperty("Genres")
  private List<String> genres;
  @JsonProperty("CommunityRating")
  private Double communityRating;
  @JsonProperty("RunTimeTicks")
  private Long runTimeTicks;
  @JsonProperty("PlayAccess")
  private String playAccess;
  @JsonProperty("ProductionYear")
  private int productionYear;
  @JsonProperty("RemoteTrailers")
  private List<?> remoteTrailers;
  @JsonProperty("ProviderIds")
  private ProviderIdsDTO providerIds;
  @JsonProperty("IsHD")
  private Boolean isHD;
  @JsonProperty("IsFolder")
  private Boolean isFolder;
  @JsonProperty("ParentId")
  private String parentId;
  @JsonProperty("Type")
  private String type;
  @JsonProperty("People")
  private List<PeopleDTO> people = new ArrayList<>();
  @JsonProperty("Studios")
  private List<?> studios;
  @JsonProperty("GenreItems")
  private List<GenreItemsDTO> genreItems;
  @JsonProperty("LocalTrailerCount")
  private String localTrailerCount;
  @JsonProperty("UserData")
  private UserDataDTO userData;
  @JsonProperty("SpecialFeatureCount")
  private String specialFeatureCount;
  @JsonProperty("DisplayPreferencesId")
  private String displayPreferencesId;
  @JsonProperty("Tags")
  private List<?> tags;
  @JsonProperty("PrimaryImageAspectRatio")
  private Double primaryImageAspectRatio;
  @JsonProperty("MediaStreams")
  private List<MediaStreamsDTO> mediaStreams;
  @JsonProperty("VideoType")
  private String videoType;
  @JsonProperty("ImageTags")
  private ImageTagsDTO imageTags;
  @JsonProperty("BackdropImageTags")
  private List<String> backdropImageTags;
  @JsonProperty("ImageBlurHashes")
  private ImageBlurHashesDTO imageBlurHashes;
  @JsonProperty("Chapters")
  private List<ChaptersDTO> chapters;
  @JsonProperty("LocationType")
  private String locationType;
  @JsonProperty("MediaType")
  private String mediaType;
  @JsonProperty("LockedFields")
  private List<?> lockedFields;
  @JsonProperty("LockData")
  private Boolean lockData;
  @JsonProperty("Width")
  private String width;
  @JsonProperty("Height")
  private String height;
  @JsonProperty("SeriesName")
  private String seriesName;
  @JsonProperty("SeriesId")
  private String seriesId;
  @JsonProperty("SeasonId")
  private String seasonId;


  @NoArgsConstructor
  @Data
  public static class ProviderIdsDTO {
    @JsonProperty("Imdb")
    private String imdb;
    @JsonProperty("Tmdb")
    private String tmdb;
  }

  @NoArgsConstructor
  @Data
  public static class UserDataDTO {
    @JsonProperty("PlaybackPositionTicks")
    private String playbackPositionTicks;
    @JsonProperty("PlayCount")
    private String playCount;
    @JsonProperty("IsFavorite")
    private Boolean isFavorite;
    @JsonProperty("LastPlayedDate")
    private String lastPlayedDate;
    @JsonProperty("Played")
    private Boolean played;
    @JsonProperty("Key")
    private String key;
  }

  @NoArgsConstructor
  @Data
  public static class ImageTagsDTO {
    @JsonProperty("Primary")
    private String primary;
  }

  @NoArgsConstructor
  @Data
  public static class ImageBlurHashesDTO {
    @JsonProperty("Backdrop")
    private BackdropDTO backdrop;
    @JsonProperty("Primary")
    private PrimaryDTO primary;

    @NoArgsConstructor
    @Data
    public static class BackdropDTO {
      @JsonProperty("aed278230e04af35754e6fd1730fc9c7")
      private String aed278230e04af35754e6fd1730fc9c7;
      @JsonProperty("5d7c17e769dcf6d58c97be7d350fc9fd")
      private String $5d7c17e769dcf6d58c97be7d350fc9fd;
      @JsonProperty("9c2f3f85a84ffe6b1cba96d3fcc1c329")
      private String $9c2f3f85a84ffe6b1cba96d3fcc1c329;
      @JsonProperty("9118b541e91a1971a82f31e80eb8c27d")
      private String $9118b541e91a1971a82f31e80eb8c27d;
      @JsonProperty("b1ac762e456973271ccc8c2fbbd35b59")
      private String b1ac762e456973271ccc8c2fbbd35b59;
      @JsonProperty("2267e07eb5cc74f4a4cd57a75f919212")
      private String $2267e07eb5cc74f4a4cd57a75f919212;
    }

    @NoArgsConstructor
    @Data
    public static class PrimaryDTO {
      @JsonProperty("27178f52fdc96b085378ff26b6c294ea")
      private String $27178f52fdc96b085378ff26b6c294ea;
    }
  }

  @NoArgsConstructor
  @Data
  public static class ExternalUrlsDTO {
    @JsonProperty("Name")
    private String name;
    @JsonProperty("Url")
    private String url;
  }

  @NoArgsConstructor
  @Data
  public static class PeopleDTO {
    @JsonProperty("Name")
    private String name;
    @JsonProperty("Id")
    private String id;
    @JsonProperty("Role")
    private String role;
    @JsonProperty("Type")
    private String type;
    @JsonProperty("PrimaryImageTag")
    private String primaryImageTag;
    @JsonProperty("ImageBlurHashes")
    private ImageBlurHashesDTO imageBlurHashes;

    @NoArgsConstructor
    @Data
    public static class ImageBlurHashesDTO {
      @JsonProperty("Primary")
      private PrimaryDTO primary;

      @NoArgsConstructor
      @Data
      public static class PrimaryDTO {
        @JsonProperty("e52e7c43e28d18cc06a0b5a26ae84ddb")
        private String e52e7c43e28d18cc06a0b5a26ae84ddb;
      }
    }
  }

  @NoArgsConstructor
  @Data
  public static class MediaSourcesDTO {

    @JsonProperty("Protocol")
    private String protocol;
    @JsonProperty("Id")
    private String id;
    @JsonProperty("Path")
    private String path;
    @JsonProperty("Type")
    private String type;
    @JsonProperty("Container")
    private String container;
    @JsonProperty("Size")
    private Long size;
    @JsonProperty("Name")
    private String name;
    @JsonProperty("IsRemote")
    private Boolean isRemote;
    @JsonProperty("ETag")
    private String eTag;
    @JsonProperty("RunTimeTicks")
    private Long runTimeTicks;
    @JsonProperty("ReadAtNativeFramerate")
    private Boolean readAtNativeFramerate;
    @JsonProperty("IgnoreDts")
    private Boolean ignoreDts;
    @JsonProperty("IgnoreIndex")
    private Boolean ignoreIndex;
    @JsonProperty("GenPtsInput")
    private Boolean genPtsInput;
    @JsonProperty("SupportsTranscoding")
    private Boolean supportsTranscoding;
    @JsonProperty("SupportsDirectStream")
    private Boolean supportsDirectStream;
    @JsonProperty("SupportsDirectPlay")
    private Boolean supportsDirectPlay;
    @JsonProperty("IsInfiniteStream")
    private Boolean isInfiniteStream;
    @JsonProperty("RequiresOpening")
    private Boolean requiresOpening;
    @JsonProperty("RequiresClosing")
    private Boolean requiresClosing;
    @JsonProperty("RequiresLooping")
    private Boolean requiresLooping;
    @JsonProperty("SupportsProbing")
    private Boolean supportsProbing;
    @JsonProperty("VideoType")
    private String videoType;
    @JsonProperty("MediaStreams")
    private List<MediaStreamsDTO> mediaStreams;
    @JsonProperty("MediaAttachments")
    private List<?> mediaAttachments;
    @JsonProperty("Formats")
    private List<?> formats;
    @JsonProperty("Bitrate")
    private String bitrate;
    @JsonProperty("RequiredHttpHeaders")
    private RequiredHttpHeadersDTO requiredHttpHeaders;
    @JsonProperty("DefaultAudioStreamIndex")
    private String defaultAudioStreamIndex;
    @JsonProperty("DefaultSubtitleStreamIndex")
    private String defaultSubtitleStreamIndex;

    @NoArgsConstructor
    @Data
    public static class RequiredHttpHeadersDTO {
    }

    @NoArgsConstructor
    @Data
    public static class MediaStreamsDTO {
      @JsonProperty("Codec")
      private String codec;
      @JsonProperty("CodecTag")
      private String codecTag;
      @JsonProperty("Language")
      private String language;
      @JsonProperty("TimeBase")
      private String timeBase;
      @JsonProperty("VideoRange")
      private String videoRange;
      @JsonProperty("VideoRangeType")
      private String videoRangeType;
      @JsonProperty("DisplayTitle")
      private String displayTitle;
      @JsonProperty("NalLengthSize")
      private String nalLengthSize;
      @JsonProperty("IsInterlaced")
      private Boolean isInterlaced;
      @JsonProperty("IsAVC")
      private Boolean isAVC;
      @JsonProperty("BitRate")
      private String bitRate;
      @JsonProperty("BitDepth")
      private String bitDepth;
      @JsonProperty("RefFrames")
      private String refFrames;
      @JsonProperty("IsDefault")
      private Boolean isDefault;
      @JsonProperty("IsForced")
      private Boolean isForced;
      @JsonProperty("Height")
      private String height;
      @JsonProperty("Width")
      private String width;
      @JsonProperty("AverageFrameRate")
      private Double averageFrameRate;
      @JsonProperty("RealFrameRate")
      private Double realFrameRate;
      @JsonProperty("Profile")
      private String profile;
      @JsonProperty("Type")
      private String type;
      @JsonProperty("AspectRatio")
      private String aspectRatio;
      @JsonProperty("Index")
      private String index;
      @JsonProperty("IsExternal")
      private Boolean isExternal;
      @JsonProperty("IsTextSubtitleStream")
      private Boolean isTextSubtitleStream;
      @JsonProperty("SupportsExternalStream")
      private Boolean supportsExternalStream;
      @JsonProperty("PixelFormat")
      private String pixelFormat;
      @JsonProperty("Level")
      private String level;
      @JsonProperty("ChannelLayout")
      private String channelLayout;
      @JsonProperty("Channels")
      private String channels;
      @JsonProperty("SampleRate")
      private String sampleRate;
    }
  }

  @NoArgsConstructor
  @Data
  public static class GenreItemsDTO {
    @JsonProperty("Name")
    private String name;
    @JsonProperty("Id")
    private String id;
  }

  @NoArgsConstructor
  @Data
  public static class MediaStreamsDTO {
    @JsonProperty("Codec")
    private String codec;
    @JsonProperty("CodecTag")
    private String codecTag;
    @JsonProperty("Language")
    private String language;
    @JsonProperty("TimeBase")
    private String timeBase;
    @JsonProperty("VideoRange")
    private String videoRange;
    @JsonProperty("VideoRangeType")
    private String videoRangeType;
    @JsonProperty("DisplayTitle")
    private String displayTitle;
    @JsonProperty("NalLengthSize")
    private String nalLengthSize;
    @JsonProperty("IsInterlaced")
    private Boolean isInterlaced;
    @JsonProperty("IsAVC")
    private Boolean isAVC;
    @JsonProperty("BitRate")
    private String bitRate;
    @JsonProperty("BitDepth")
    private String bitDepth;
    @JsonProperty("RefFrames")
    private String refFrames;
    @JsonProperty("IsDefault")
    private Boolean isDefault;
    @JsonProperty("IsForced")
    private Boolean isForced;
    @JsonProperty("Height")
    private String height;
    @JsonProperty("Width")
    private String width;
    @JsonProperty("AverageFrameRate")
    private Double averageFrameRate;
    @JsonProperty("RealFrameRate")
    private Double realFrameRate;
    @JsonProperty("Profile")
    private String profile;
    @JsonProperty("Type")
    private String type;
    @JsonProperty("AspectRatio")
    private String aspectRatio;
    @JsonProperty("Index")
    private String index;
    @JsonProperty("IsExternal")
    private Boolean isExternal;
    @JsonProperty("IsTextSubtitleStream")
    private Boolean isTextSubtitleStream;
    @JsonProperty("SupportsExternalStream")
    private Boolean supportsExternalStream;
    @JsonProperty("PixelFormat")
    private String pixelFormat;
    @JsonProperty("Level")
    private String level;
  }

  @NoArgsConstructor
  @Data
  public static class ChaptersDTO {
    @JsonProperty("StartPositionTicks")
    private String startPositionTicks;
    @JsonProperty("Name")
    private String name;
    @JsonProperty("ImageDateModified")
    private String imageDateModified;
  }
}
