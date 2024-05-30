package top.mty.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import top.mty.common.JellyfinMPException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class FileUtil {

  /**
   * 保存字节到文件
   */
  public static void saveBytes2File(byte[] bytes, String filePath) {
    try (FileOutputStream fos = new FileOutputStream(filePath)) {
      if (bytes.length == 0) {
        throw new JellyfinMPException("saveBytes2File: bytes为空");
      }
      if (!StringUtils.hasText(filePath)) {
        throw new JellyfinMPException("saveBytes2File: 没有文件保存路径");
      }
      fos.write(bytes);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * 删除文件
   */
  public static void deleteFile(String filePath) {
    Path path = Paths.get(filePath);

    try {
      Files.delete(path);
      log.info("文件删除成功：" + filePath);
    } catch (Exception e) {
      log.error("文件删除失败: ", e);
    }
  }

  /**
   * 将filePath转为MultipartFile
   */
  public static MultipartFile convert(String filePath) {
    try {
      File file = new File(filePath);
      // 创建一个DiskFileItem对象，传入文件名和文件类型
      DiskFileItem fileItem = new DiskFileItem("file", "application/octet-stream", false, file.getName(), (int) file.length(), file.getParentFile());
      // 读取文件内容
      FileInputStream fileInputStream = new FileInputStream(file);
      byte[] bytes = new byte[(int) file.length()];
      fileInputStream.read(bytes);
      fileInputStream.close();
      // 设置文件内容到DiskFileItem
      fileItem.getOutputStream().write(bytes);
      // 创建CommonsMultipartFile对象
      return new CommonsMultipartFile(fileItem);
    } catch (Exception e) {
      log.error("文件转换失败: {}", filePath, e);
    }
    return null;
  }
}
