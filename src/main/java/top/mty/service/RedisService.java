package top.mty.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

@Service
public class RedisService {
  @Autowired
  private RedisTemplate<String, String> redisTemplate;

  public void setValue(String key, String value) {
    redisTemplate.opsForValue().set(key, value);
  }

  public String getValue(String key) {
    return redisTemplate.opsForValue().get(key);
  }

  public void deleteKey(String key) {
    redisTemplate.delete(key);
  }

  public void deleteKeys(Collection<String> keys) {
    redisTemplate.delete(keys);
  }

  /**
   * 自动删除key
   * @param key key
   * @param value value
   * @param timeout seconds
   */
  public void setValueWithTTL(String key, String value, long timeout) {
    redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
  }
}
