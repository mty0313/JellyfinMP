package top.mty.controller;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.mty.common.R;
import top.mty.controller.data.jellyfin.webhook.JellyfinWebhookProperties;
import top.mty.remote.BarkClient;
import top.mty.service.JellyfinWebhookService;
import top.mty.utils.HttpUtil;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/webhook")
@Slf4j
public class WebhookController {
  @Autowired
  private BarkClient barkClient;
  @Autowired
  private JellyfinWebhookService jellyfinWebhookService;

  @RequestMapping("/jellyfin")
  public R<String> jellyfin(HttpServletRequest request) {
    String bodyString = HttpUtil.getBodyString(request);
    log.debug("Webhook_Request_Body: {}",bodyString);
    JellyfinWebhookProperties properties = JSON.to(JellyfinWebhookProperties.class, bodyString);
    if (null == properties) {
      log.warn("Jellyfin Webhook接收转换异常");
      return R.error();
    }
    jellyfinWebhookService.itemAdded(properties);
    return R.success();
  }

}
