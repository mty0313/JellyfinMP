package top.mty;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import top.mty.common.JellyfinMPException;
import top.mty.job.WeixinMPJob;

@SpringBootTest
@Import(ScheduleTestConfig.class)
public class DraftCreateTest {
  @Autowired
  private WeixinMPJob weixinMPJob;

  @Test
  public void testCreate() throws JellyfinMPException {
    weixinMPJob.draftCreate();
  }
}
