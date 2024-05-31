package top.mty.service.params;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeixinMPDraftCreateAndPost {
  /**
   * 创建草稿后是否直接发布图文
   */
  private boolean post2MpNews;
  /**
   * 创建草稿后是否群发
   */
  private boolean send2All;
  /**
   * 草稿id
   */
  private String mediaId;
  /**
   * 图文发布任务id
   */
  private String publishId;
}
