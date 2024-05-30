package top.mty.remote;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import top.mty.config.JellyfinClientConfiguration;
import top.mty.remote.param.*;

@FeignClient(name = "jellyfin-full-control-client", configuration = JellyfinClientConfiguration.class)
public interface JellyfinFullControlApiClient {
  /**
   * 获取item
   */
  @GetMapping(value = "Items", consumes = MediaType.APPLICATION_JSON_VALUE)
  JellyfinItemResponse getItems(@SpringQueryMap JellyfinItemRequest query);

  /**
   * 根据itemId获取图片
   * @param itemId itemId
   * @param type {@link JellyfinItemImageType}
   * @return 图片文件
   */
  @GetMapping(value = "Items/{itemId}/Images/{type}")
  ResponseEntity<byte[]> getItemImage(@PathVariable("itemId") String itemId, @PathVariable("type") String type,
                                      @SpringQueryMap JellyfinItemImageRequest request);

  /**
   * 获取用户library中的某个item
   */
  @GetMapping(value = "/Users/{userId}/Items/{itemId}")
  JellyfinUserLibraryItem getUserLibraryItem(@PathVariable("userId") String userId, @PathVariable("itemId") String itemId);

  /**
   * 返回所有项目数量
   */
  @GetMapping(value = "/Items/Counts")
  JellyfinItemCount getItemCount(@RequestParam("userId") String userId, @RequestParam("isFavorite") Boolean isFavorite);
}
