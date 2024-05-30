package top.mty.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class R<T> implements Serializable {

  private static final long serialVersionUID = 1L;

  private int code;

  private String msg;

  private T data;

  public static final Integer SUCCESS = 0;

  public static final Integer ERROR = 1;

  public static <T> R<T> success() {
    return new R<>(SUCCESS, "success", null);
  }

  public static <T> R<T> success(T data) {
    return new R<T>(SUCCESS, "success", data);
  }

  public static <T> R<T> error() {
    return new R<>(ERROR, "error", null);
  }

  public static <T> R<T> error(T data) {
    return new R<>(ERROR, "error", data);
  }

  public static String getMethodName(String fieldName) {
    byte[] items = fieldName.getBytes(StandardCharsets.UTF_8);
    items[0] = (byte) ((char) items[0] - 'a' + 'A');
    return new String(items);
  }

  public boolean isSuccess() {
    return SUCCESS == this.code;
  }
}
