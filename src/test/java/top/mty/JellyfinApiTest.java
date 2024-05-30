package top.mty;

import com.alibaba.fastjson2.JSON;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import top.mty.common.CustomAppId;
import top.mty.remote.JellyfinFullControlApiClient;
import top.mty.remote.param.JellyfinItemRequest;
import top.mty.remote.param.JellyfinItemResponse;
import top.mty.service.DynamicFeignClientService;

@SpringBootTest
public class JellyfinApiTest {
  @Autowired
  private DynamicFeignClientService dynamicFeignClientService;

  @Test
  public void testGetItem() {
    JellyfinFullControlApiClient nasClient = dynamicFeignClientService.getClient(JellyfinFullControlApiClient.class, CustomAppId.Jellyfin.name());
    JellyfinItemResponse response = nasClient.getItems(new JellyfinItemRequest("8027339afde7d37ea4d504ba292f72f7"));
    System.out.println(JSON.toJSONString(response));
  }
}
