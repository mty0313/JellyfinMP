package top.mty.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import top.mty.common.JellyfinMPException;
import top.mty.job.params.WeixinMPAfterDraft;
import top.mty.service.WeixinMPDraftService;
import top.mty.service.WeixinTokenService;

@Component
public class WeixinMPJob {

  @Value("${weixin.mp.draft.post2MpNews:false}")
  private boolean post2MpNews;
  @Value("${weixin.mp.draft.send2All:false}")
  private boolean send2All;
  @Value("${weixin.mp.draft.updateDatabase:true}")
  private boolean updateDatabase;

  @Autowired
  private WeixinTokenService weixinTokenService;
  @Autowired
  private WeixinMPDraftService weixinMPDraftService;

  @Scheduled(initialDelay = 0, fixedRate = 7000000)
  public void tokenRefresh() {
    weixinTokenService.refreshAccessToken();
  }

  @Scheduled(cron = "0 0 9 * * ?")
  public void draftCreate() throws JellyfinMPException {
    WeixinMPAfterDraft afterDraft = new WeixinMPAfterDraft(post2MpNews, send2All, updateDatabase);
    weixinMPDraftService.createDraft(afterDraft);
  }
}
