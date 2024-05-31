package top.mty;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.test.context.junit4.SpringRunner;
import top.mty.common.JellyfinMPException;
import top.mty.job.WeixinMPJob;
import top.mty.service.WeixinTokenService;

@SpringBootTest
@RunWith(SpringRunner.class)
public class DraftCreateTest {
  @Autowired
  private WeixinMPJob weixinMPJob;

  @Test
  public void testCreate() throws JellyfinMPException {
    weixinMPJob.draftCreate();
  }
}
