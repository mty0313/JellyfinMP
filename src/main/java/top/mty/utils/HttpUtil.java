package top.mty.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import javax.servlet.ServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
public class HttpUtil {
  public static String getBodyString(ServletRequest request) {
    String body = null;
    InputStream inputStream = null;
    try {
      inputStream = request.getInputStream();
      body = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
    } catch (IOException e) {
      log.error("request获取body失败", e);
    } finally {
      if (inputStream != null) {
        try {
          inputStream.close();
        } catch (IOException e) {
          log.error("request.inputStream关闭失败", e);
        }
      }
    }
    return body;
  }
}
