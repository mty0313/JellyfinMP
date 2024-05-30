package top.mty.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;

public class FeignClientConfiguration {
  @Bean
  public Logger.Level feignLoggerLevel() {
    return Logger.Level.FULL;
  }
}
