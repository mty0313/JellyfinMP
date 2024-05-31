package top.mty.job.params;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeixinMPAfterDraft {
  /**
   * 发送图文
   */
  private boolean post2MpNews;
  /**
   * 群发消息
   */
  private boolean send2All;
  /**
   * 更新数据库processed为true
   */
  private boolean updateDatabase;
}
