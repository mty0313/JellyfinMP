package top.mty.remote.param;

import lombok.Data;

@Data
public class JellyfinItemImageRequest {
  /**
   * Optional. The MediaBrowser.Model.Drawing.ImageFormat of the returned image.
   */
  private String format = "jpeg";

  /**
   * Optional. Quality setting, from 0-100. Defaults to 90 and should suffice in most cases.
   */
  private Integer quality = 90;

  /**
   * Width of box to fill.
   */
  private Integer fillWidth;

  /**
   * Height of box to fill.
   */
  private Integer fillHeight;
}
