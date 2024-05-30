package top.mty.config;

import feign.Client;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import top.mty.remote.param.WeixinMPParams;
import top.mty.service.DynamicFeignParams;

public class WeixinMpClientConfiguration extends FeignClientConfiguration{
  @Bean
  public DynamicFeignParams<WeixinMPParams> getDynamicParams() {
    return new DynamicFeignParams<WeixinMPParams>() {
      @Override
      public String getUrl(WeixinMPParams params) {
        return params.getServerUrl();
      }

      @Override
      public RequestInterceptor getDynamicInterceptor(WeixinMPParams params) {
        return new FeignTraceInterceptor(params);
      }
    };
  }

  @Bean
  public Client feignClient() {
    return new Client.Default(null, null);
  }

  static class FeignTraceInterceptor implements RequestInterceptor {
    private final WeixinMPParams params;
    public FeignTraceInterceptor(WeixinMPParams params) {
      this.params = params;
    }

    public void apply(RequestTemplate requestTemplate) {
      if ("/cgi-bin/token".equals(requestTemplate.url())) {
        String appId = params.getAppId();
        String appSecret = params.getAppSecret();
        requestTemplate.query("grant_type", "client_credential");
        requestTemplate.query("appid", appId);
        requestTemplate.query("secret", appSecret);
      } else {
        String accessToken = params.getAccessToken();
        requestTemplate.query("access_token", accessToken);
      }
    }

  }
}
