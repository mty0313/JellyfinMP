package top.mty.config;

import feign.Client;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import top.mty.remote.param.JellyfinServerParams;
import top.mty.service.DynamicFeignParams;

public class JellyfinClientConfiguration extends FeignClientConfiguration {

  @Bean
  public DynamicFeignParams<JellyfinServerParams> getDynamicParams() {
    return new DynamicFeignParams<JellyfinServerParams>() {
      @Override
      public String getUrl(JellyfinServerParams params) {
        return params.getServerUrl();
      }

      @Override
      public RequestInterceptor getDynamicInterceptor(JellyfinServerParams params) {
        return new FeignTraceInterceptor(params);
      }
    };
  }

  // 禁用负载均衡
  @Bean
  public Client feignClient() {
    return new Client.Default(null, null);
  }

  static class FeignTraceInterceptor implements RequestInterceptor {

    private final JellyfinServerParams params;

    public FeignTraceInterceptor(JellyfinServerParams params) {
      this.params = params;
    }

    @Override
    public void apply(RequestTemplate requestTemplate) {
      requestTemplate.header("Authorization", params.getJellyfinToken());
    }
  }
}