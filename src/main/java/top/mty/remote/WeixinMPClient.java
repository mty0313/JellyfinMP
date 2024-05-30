package top.mty.remote;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.mty.config.WeixinMpClientConfiguration;
import top.mty.entity.WeixinMPAccessToken;
import top.mty.remote.param.*;

@FeignClient(name = "weixin-mp-client", configuration = WeixinMpClientConfiguration.class)
public interface WeixinMPClient {
  /**
   * 获取token
   * @return
   */
  @GetMapping(value = "cgi-bin/token", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  WeixinMPAccessToken queryAccessToken();

  /**
   * 创建草稿
   * @param request
   * @return
   */
  @PostMapping(value = "cgi-bin/draft/add", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  String createDraft(@RequestBody WeixinMPDraftCreateRequest request);

  /**
   * 发布草稿为图文
   * @param request
   * @return
   */
  @PostMapping(value = "cgi-bin/freepublish/submit", consumes = MediaType.APPLICATION_JSON_VALUE)
  String draftPost(@RequestBody WeixinMPDraftPostRequest request);

  /**
   * 查询发布状态
   * @param request
   * @return
   */
  @PostMapping(value = "cgi-bin/freepublish/get", consumes = MediaType.APPLICATION_JSON_VALUE)
  String queryPublishResult(@RequestBody WeixinMPPublishResultQueryRequest request);

  /**
   * 群发接口
   */
  @PostMapping(value = "cgi-bin/message/mass/sendall", consumes = MediaType.APPLICATION_JSON_VALUE)
  WeixinMPSend2AllResponse send2All(@RequestBody WeixinMPSend2AllRequest request);

  /**
   * 上传图片接口
   */
  @PostMapping(value = "cgi-bin/media/uploadimg", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  String uploadImage(@RequestPart("media") MultipartFile file);

  /**
   * 永久素材接口
   * @param type 媒体文件类型，分别有图片（image）、语音（voice）、视频（video）和缩略图（thumb）
   * @param file 文件
   * @return media_id, url
   */
  @PostMapping(value = "cgi-bin/material/add_material", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  String addMaterial(@RequestParam("type") String type, @RequestPart("media") MultipartFile file);
}
