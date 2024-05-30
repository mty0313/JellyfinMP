package top.mty.common;

import org.apache.commons.lang3.StringUtils;

public class Assert {
  public static void notEmpty(String text) {
    if (StringUtils.isEmpty(text)) {
      throw new RuntimeException("param cannot be empty");
    }
  }

  public static void notEmpty(String text, String variable) {
    if (StringUtils.isEmpty(text)) {
      throw new RuntimeException(variable + "cannot be empty");
    }
  }
}
