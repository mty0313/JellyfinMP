package top.mty.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeixinMPDraftPost {
  @TableId
  private String uuid;

  private String mediaId;

  private String publishId;

  private boolean published;
}
