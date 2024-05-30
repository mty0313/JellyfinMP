package top.mty.utils;

import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Component
public class DateUtil {

  public static final String YYYYMMDDHHMMSS = "yyyy-MM-dd HH:mm:ss";

  public static final String YYYYMMDD = "yyyy-MM-dd";

  public static final String ISO = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

  public static final String JELLYFIN_UTC = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS'Z'";

  private static final SimpleDateFormat standardFormatter = new SimpleDateFormat(YYYYMMDDHHMMSS);

  public static final SimpleDateFormat dateFormatter = new SimpleDateFormat(YYYYMMDD);

  public static String toStandardDateValue(Date date) {
    return standardFormatter.format(date);
  }

  public static String toStandardYMD(Date date) {
    return dateFormatter.format(date);
  }

  public static Date parseStandardDateTime(String dateValue) throws ParseException {
    return standardFormatter.parse(dateValue);
  }

  public static String formatDate(Date date) {
    return dateFormatter.format(date);
  }

  public static String formatDate(Date date, String pattern) {
    return new SimpleDateFormat(pattern).format(date);
  }

  public static Date timestamp2Date(long timestamp) {
    String timestampStr = String.valueOf(timestamp);
    if (timestampStr.length() == 10) {
      timestampStr += "000";
    }
    timestamp = Long.parseLong(timestampStr);
    return new Date(timestamp);
  }

  // 返回时间格式如：2020-02-17 00:00:00
  public static long getStartOfDay(Date time) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(time);
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    return calendar.getTimeInMillis();
  }
  // 返回时间格式如：2020-02-19 23:59:59
  public static String getEndOfDay(Date time) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(time);
    calendar.set(Calendar.HOUR_OF_DAY, 23);
    calendar.set(Calendar.MINUTE, 59);
    calendar.set(Calendar.SECOND, 59);
    calendar.set(Calendar.MILLISECOND, 999);
    return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());
  }

}
