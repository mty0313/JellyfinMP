package top.mty.service;

import feign.*;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignContext;
import org.springframework.cloud.openfeign.FeignLoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Service;
import top.mty.common.JellyfinMPException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class DynamicFeignClientService {
  @Autowired
  private ApplicationContext applicationContext;
  @Autowired
  private RemoteServerInfoService remoteServerInfoService;
  @Autowired
  private FeignClientParamService feignClientParamService;



  public <T> T getClient(Class<T> feignClientClazz, String appId, RequestInterceptor... otherRequestInterceptors) {
    try {
      Class<?> paramsClass = getClientParamsClass(feignClientClazz);
      Object filledValueParams = feignClientParamService.getFeignParam(appId, paramsClass);
      return this.getClient(feignClientClazz, filledValueParams, otherRequestInterceptors);
    } catch (Exception e) {
      log.error("DynamicFeignClientService.getClient error: ", e);
      return null;
    }
  }

  private Class<?> getClientParamsClass(Class<?> feignClientClazz) throws Exception {
    FeignContext context = applicationContext.getBean(FeignContext.class);
    FeignClient[] annotation = feignClientClazz.getAnnotationsByType(FeignClient.class);
    if (annotation.length == 0) {
      throw new JellyfinMPException("非FeignClient客户端");
    }
    String name = annotation[0].name();
    DynamicFeignParams<?> dynamicFeignParams = getOption(context, name, DynamicFeignParams.class);
    ResolvableType type = ResolvableType.forInstance(dynamicFeignParams).getInterfaces()[0].getGenerics()[0];
    return (Class<?>) type.getType();
  }

  @SuppressWarnings("unchecked")
  private <T> T getClient(Class<T> feignClientClazz, Object params, RequestInterceptor... otherRequestInterceptors) throws Exception {
    FeignClient[] annotation = feignClientClazz.getAnnotationsByType(FeignClient.class);
    if (annotation.length == 0) {
      throw new JellyfinMPException("不是FeignClient");
    }
    String name = annotation[0].name();
    String url = annotation[0].url();
    String path = annotation[0].path();
    FeignContext context = applicationContext.getBean(FeignContext.class);
    FeignLoggerFactory loggerFactory = getBean(context, name, FeignLoggerFactory.class);
    Logger logger = loggerFactory.create(feignClientClazz);
    Feign.Builder builder = getBean(context, name, Feign.Builder.class).logger(logger)
        .encoder(getBean(context, name, Encoder.class))
        .decoder(getBean(context, name, Decoder.class))
        .contract(getBean(context, name, Contract.class));
    Logger.Level level = getOption(context, name, Logger.Level.class);
    if (level != null) {
      builder.logLevel(level);
    }
    Retryer retryer = getOption(context, name, Retryer.class);
    if (retryer != null) {
      builder.retryer(retryer);
    }
    ErrorDecoder errorDecoder = getOption(context, name, ErrorDecoder.class);
    if (errorDecoder != null) {
      builder.errorDecoder(errorDecoder);
    }

    Map<String, RequestInterceptor> requestInterceptors = context.getInstances(name, RequestInterceptor.class);
    List<RequestInterceptor> interceptorList = new ArrayList<>();
    if (requestInterceptors != null) {
      interceptorList.addAll(requestInterceptors.values());
    }
    if (otherRequestInterceptors.length != 0) {
      interceptorList.addAll(Arrays.asList(otherRequestInterceptors));
    }
    val dynamicFeignParams = getOption(context, name, DynamicFeignParams.class);
    if (dynamicFeignParams != null) {
      String target = dynamicFeignParams.getUrl(params);
      if (StringUtils.isNotBlank(target)) {
        url = target;
      }

      RequestInterceptor interceptor = dynamicFeignParams.getDynamicInterceptor(params);
      if (interceptor != null) {
        interceptorList.add(interceptor);
      }
    }
    builder.requestInterceptors(interceptorList);
    Client client = getOption(context, name, Client.class);
    if (client != null) {
      builder.client(client);
    }
    if (StringUtils.isNotBlank(path)) {
      url = url + "/" + path;
    }
    return builder.target(new Target.HardCodedTarget<>(feignClientClazz, name, url));
  }

  private <T> T getBean(FeignContext context, String name, Class<T> type) {
    T instance = context.getInstance(name, type);
    if (instance == null) {
      throw new IllegalStateException("No bean found of type " + type + " for " + name);
    }
    return instance;
  }

  private <T> T getOption(FeignContext context, String name, Class<T> type) {
    return context.getInstance(name, type);
  }

}
