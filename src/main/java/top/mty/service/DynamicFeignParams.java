package top.mty.service;

import feign.RequestInterceptor;

public interface DynamicFeignParams<T> {

  String getUrl(T params);

  RequestInterceptor getDynamicInterceptor(T params);

}