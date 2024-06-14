package top.mty.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.util.StringUtils;
import top.mty.controller.data.jellyfin.webhook.JellyfinWebhookProperties;
import top.mty.remote.param.JellyfinUserLibraryItem;

import java.util.Date;

@Data
public class JellyfinWebhookEntity {
  @TableId
  private String uuid;
  /**
   * 服务器名称
   */
  @JsonProperty("ServerName")
  private String serverName;
  /**
   * NotificationType 通知类型
   * PlaybackStart
   * PlaybackStop
   * ItemAdded
   */
  @JsonProperty("NotificationType")
  private String notificationType;
  /**
   * 2024-03-19T18:34:38.275254Z
   */
  @JsonProperty("Timestamp")
  private Date timestamp;
  /**
   * 电影/剧集单集名称
   */
  @JsonProperty("Name")
  private String name;

  /**
   * 简介
   */
  @JsonProperty("Overview")
  private String overview;
  /**
   * Episode
   * Movie
   */
  @JsonProperty("ItemType")
  private String itemType;
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
  /**
   * 集
   */
  @JsonProperty("EpisodeNumber")
  private Integer episodeNumber;
  /**
   * S01E01
   */
  private String seasonEpisode;
  /**
   * 单集所属剧集的简介
   */
  @TableField(exist = false)
  private String episodeSeriesOverview;
  /**
   * 1080p HEVC SDR
   */
  @JsonProperty("Video_0_Title")
  private String video0Title;
  /**
   * mty
   */
  @JsonProperty("NotificationUsername")
  private String notificationUsername;
  /**
   * userId
   */
  @JsonProperty("UserId")
  private String userId;
  /**
   * 处理过的标记
   */
  @JsonProperty("processed")
  private boolean processed;
  /**
   * itemId
   */
  @JsonProperty("itemId")
  private String itemId;

  public static JellyfinWebhookEntity toWebhookEntity(JellyfinWebhookProperties properties) {
    JellyfinWebhookEntity entity = new JellyfinWebhookEntity();
    entity.setName(properties.getName());
    entity.setItemType(properties.getItemType());
    if (null != properties.getOverview()) {
      properties.setOverview(properties.getOverview().replaceAll("\\s+", ""));
    }
    entity.setOverview(StringUtils.hasText(properties.getOverview()) && properties.getOverview().length() > 500 ?
        properties.getOverview().substring(0, 500) + "......" : properties.getOverview());
    entity.setYear(properties.getYear());
    entity.setServerName(properties.getServerName());
    entity.setSeriesName(properties.getSeriesName());
    entity.setSeasonNumber(properties.getSeasonNumber());
    entity.setEpisodeNumber(properties.getEpisodeNumber());
    entity.setNotificationType(properties.getNotificationType());
    entity.setTimestamp(properties.getTimestamp());
    if (StringUtils.hasText(properties.getSeasonNumber00()) && !"null".equals(properties.getSeasonNumber00())
        && StringUtils.hasText(properties.getEpisodeNumber00()) && !"null".equals(properties.getEpisodeNumber00())) {
      entity.setSeasonEpisode(String.format("S%sE%s", properties.getSeasonNumber00(), properties.getEpisodeNumber00()));
    }
    entity.setVideo0Title(properties.getVideo0Title());
    entity.setNotificationUsername(properties.getNotificationUsername());
    entity.setUserId(properties.getUserId());
    entity.setItemId(properties.getItemId());
    return entity;
  }

  /**
   * 结合查到的JellyfinUserLibraryItem和已有的orphanEpisode造一个series的JellyfinWebhookEntity
   */
  public static JellyfinWebhookEntity toWebhookEntity(JellyfinUserLibraryItem series, JellyfinWebhookEntity orphanEpisode) {
    JellyfinWebhookEntity entity = new JellyfinWebhookEntity();
    entity.setName(series.getName());
    entity.setItemType(series.getType());
    entity.setOverview(StringUtils.hasText(series.getOverview()) && series.getOverview().length() > 500 ?
        series.getOverview().substring(0, 500) + "......" : series.getOverview());
    entity.setYear(series.getProductionYear());
    entity.setServerName(orphanEpisode.getServerName());
    entity.setSeriesName(series.getName());
    entity.setNotificationType(JellyfinWebhookProperties.ITEM_TYPE_SERIES);
    entity.setTimestamp(orphanEpisode.getTimestamp());
    entity.setVideo0Title(orphanEpisode.getVideo0Title());
    entity.setNotificationUsername(orphanEpisode.getNotificationUsername());
    entity.setUserId(orphanEpisode.getUserId());
    entity.setItemId(series.getId());
    return entity;
  }
}
