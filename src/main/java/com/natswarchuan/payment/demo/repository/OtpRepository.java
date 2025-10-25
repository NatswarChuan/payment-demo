package com.natswarchuan.payment.demo.repository;

import com.natswarchuan.payment.demo.constant.AppConfigConstant;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OtpRepository {

  private final StringRedisTemplate redisTemplate;

  @Value("${app.otp.expiration-minutes}")
  private long otpExpirationMinutes;

  public void saveOtp(String email, String otp) {
    String key = AppConfigConstant.REDIS_OTP_PREFIX + email;
    redisTemplate.opsForValue().set(key, otp, otpExpirationMinutes, TimeUnit.MINUTES);
  }

  public Optional<String> getOtp(String email) {
    String key = AppConfigConstant.REDIS_OTP_PREFIX + email;
    return Optional.ofNullable(redisTemplate.opsForValue().get(key));
  }

  public void deleteOtp(String email) {
    String key = AppConfigConstant.REDIS_OTP_PREFIX + email;
    redisTemplate.delete(key);
  }
}
