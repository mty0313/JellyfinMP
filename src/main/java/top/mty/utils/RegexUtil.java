package top.mty.utils;

import org.springframework.util.StringUtils;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil {

  /**
   * 在给定的原始字符串 raw 中使用给定的正则表达式 pattern 进行查找，并返回第一个匹配的子字符串。
   * @param raw 原始
   * @param pattern 正则表达式
   * @return 第一个匹配, 没有则返回""
   */
  public static String findStrByPattern(String raw, String pattern) {
    if (!StringUtils.hasText(raw) || !StringUtils.hasText(pattern)) {
      return "";
    }
    // 创建 Pattern 对象
    Pattern p = Pattern.compile(pattern);

    // 创建 Matcher 对象
    Matcher m = p.matcher(raw);

    // 查找匹配
    if (m.find()) {
      return m.group().toUpperCase(Locale.ROOT);
    } else {
      return "";
    }
  }
}
