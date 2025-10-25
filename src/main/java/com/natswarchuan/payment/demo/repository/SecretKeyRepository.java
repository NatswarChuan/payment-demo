package com.natswarchuan.payment.demo.repository;

import com.natswarchuan.payment.demo.constant.AppConfigConstant;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SecretKeyRepository {

  private final StringRedisTemplate redisTemplate;

  @Value("${app.secret-key.expiration-minutes}")
  private long secretKeyExpirationMinutes;

  public void saveSecretKey(String secretKey, String hashedPin) {
    String key = AppConfigConstant.REDIS_SECRET_KEY_PREFIX + secretKey;
    redisTemplate.opsForValue().set(key, hashedPin, secretKeyExpirationMinutes, TimeUnit.MINUTES);
  }

  public Optional<String> getHashedPinBySecretKey(String secretKey) {
    String key = AppConfigConstant.REDIS_SECRET_KEY_PREFIX + secretKey;
    return Optional.ofNullable(redisTemplate.opsForValue().get(key));
  }

  public void deleteSecretKey(String secretKey) {
    String key = AppConfigConstant.REDIS_SECRET_KEY_PREFIX + secretKey;
    redisTemplate.delete(key);
  }
}