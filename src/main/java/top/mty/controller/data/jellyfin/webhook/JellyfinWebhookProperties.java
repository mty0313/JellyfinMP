package top.mty.controller.data.jellyfin.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@NoArgsConstructor
@Data
@Slf4j
public class JellyfinWebhookProperties {
  public static final String NOTIFICATION_TYPE_PLAYBACK_START = "PlaybackStart";
  public static final String NOTIFICATION_TYPE_PLAYBACK_STOP = "PlaybackStop";
  public static final String NOTIFICATION_TYPE_PLAYBACK_ITEM_ADDED = "ItemAdded";
  public static final String ITEM_TYPE_EPISODE = "Episode";
  public static final String ITEM_TYPE_MOVIE = "Movie";
  public static final String ITEM_TYPE_SERIES = "Series";
  public static final String ITEM_TYPE_SEASON = "Season";


  @JsonProperty("ServerId")
  private String serverId;
  /**
   * 服务器名称
   */
  @JsonProperty("ServerName")
  private String serverName;
  @JsonProperty("ServerVersion")
  private String serverVersion;
  @JsonProperty("ServerUrl")
  private String serverUrl;
  /**
   * NotificationType 通知类型
   * PlaybackStart
   * PlaybackStop
   * ItemAdded
   */
  @JsonProperty("NotificationType")
  private String notificationType;
  @JsonProperty("Timestamp")
  private Date timestamp;
  /**
   * 2024-03-19T18:34:38.275254Z
   */
  @JsonProperty("UtcTimestamp")
  private String utcTimestamp;
  /**
   * 电影/剧集单集名称
   */
  @JsonProperty("Name")
  private String name;
  /**
   * 电影/剧集 文件路径
   */
  @JsonProperty("Path")
  private String path; //add by jaxwang
  /**
   * 简介
   */
  @JsonProperty("Overview")
  private String overview;
  @JsonProperty("Tagline")
  private String tagline;
  @JsonProperty("ItemId")
  private String itemId;
  /**
   * Episode
   * Movie
   */
  @JsonProperty("ItemType")
  private String itemType;
  @JsonProperty("RunTimeTicks")
  private Long runTimeTicks;
  @JsonProperty("RunTime")
  private String runTime;
  /**
   * 2024
   */
  @JsonProperty("Year")
  private Integer year;
  /**
   * 剧集名称
   */
  @JsonProperty("SeriesName")
  private String seriesName;
  /**
   * 季
   */
  @JsonProperty("SeasonNumber")
  private Integer seasonNumber;
  @JsonProperty("SeasonNumber00")
  private String seasonNumber00;
  @JsonProperty("SeasonNumber000")
  private String seasonNumber000;
  /**
   * 集
   */
  @JsonProperty("EpisodeNumber")
  private Integer episodeNumber;
  @JsonProperty("EpisodeNumber00")
  private String episodeNumber00;
  @JsonProperty("EpisodeNumber000")
  private String episodeNumber000;
  @JsonProperty("Provider_tmdb")
  private String providerTmdb;
  @JsonProperty("Provider_tvrage")
  private String providerTvrage;
  @JsonProperty("Provider_imdb")
  private String providerImdb;
  @JsonProperty("Provider_tvdb")
  private String providerTvdb;
  /**
   * 1080p HEVC SDR
   */
  @JsonProperty("Video_0_Title")
  private String video0Title;
  @JsonProperty("Video_0_Type")
  private String video0Type;
  @JsonProperty("Video_0_Codec")
  private String video0Codec;
  @JsonProperty("Video_0_Profile")
  private String video0Profile;
  @JsonProperty("Video_0_Level")
  private Integer video0Level;
  @JsonProperty("Video_0_Height")
  private Integer video0Height;
  @JsonProperty("Video_0_Width")
  private Integer video0Width;
  @JsonProperty("Video_0_AspectRatio")
  private String video0Aspectratio;
  @JsonProperty("Video_0_Interlaced")
  private Boolean video0Interlaced;
  @JsonProperty("Video_0_FrameRate")
  private Double video0Framerate;
  @JsonProperty("Video_0_VideoRange")
  private String video0Videorange;
  @JsonProperty("Video_0_ColorSpace")
  private Object video0Colorspace;
  @JsonProperty("Video_0_ColorTransfer")
  private Object video0Colortransfer;
  @JsonProperty("Video_0_ColorPrimaries")
  private Object video0Colorprimaries;
  @JsonProperty("Video_0_PixelFormat")
  private String video0Pixelformat;
  @JsonProperty("Video_0_RefFrames")
  private Integer video0Refframes;
  @JsonProperty("Audio_0_Title")
  private String audio0Title;
  @JsonProperty("Audio_0_Type")
  private String audio0Type;
  @JsonProperty("Audio_0_Language")
  private Object audio0Language;
  @JsonProperty("Audio_0_Codec")
  private String audio0Codec;
  @JsonProperty("Audio_0_Channels")
  private Integer audio0Channels;
  @JsonProperty("Audio_0_Bitrate")
  private Integer audio0Bitrate;
  @JsonProperty("Audio_0_SampleRate")
  private Integer audio0Samplerate;
  @JsonProperty("Audio_0_Default")
  private Boolean audio0Default;
  @JsonProperty("PlaybackPositionTicks")
  private Long playbackPositionTicks;
  @JsonProperty("PlaybackPosition")
  private String playbackPosition;
  @JsonProperty("MediaSourceId")
  private String mediaSourceId;
  @JsonProperty("IsPaused")
  private Boolean isPaused;
  @JsonProperty("IsAutomated")
  private Boolean isAutomated;
  @JsonProperty("DeviceId")
  private String deviceId;
  @JsonProperty("DeviceName")
  private String deviceName;
  @JsonProperty("ClientName")
  private String clientName;
  @JsonProperty("PlayedToCompletion")
  private Boolean playedToCompletion;
  @JsonProperty("NotificationUsername")
  private String notificationUsername;
  @JsonProperty("UserId")
  private String userId;
}
