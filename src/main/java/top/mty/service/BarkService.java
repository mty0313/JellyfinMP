package top.mty.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.mty.remote.BarkClient;

@Service
@Slf4j
public class BarkService {
  @Autowired
  private BarkClient barkClient;

  public void pushMsg(String device, String title, String content) {
    try {
      barkClient.pushMsg(device, title, content);
    } catch (Exception e) {
      log.warn("Bark推送失败", e);
    }
  }
}
