package com.natswarchuan.payment.demo.service;

import com.natswarchuan.payment.demo.constant.AppConfigConstant;
import com.natswarchuan.payment.demo.interfaces.services.IRateLimiterService;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * Lớp dịch vụ triển khai chức năng giới hạn tần suất (rate limiting) sử dụng Redis.
 *
 * <p>Service này sử dụng Redis để theo dõi số lượng yêu cầu từ một khóa (key) nhất định trong một
 * khoảng thời gian được cấu hình, giúp ngăn chặn việc lạm dụng hệ thống.
 */
@Service
@RequiredArgsConstructor
public class RateLimiterService implements IRateLimiterService {

  private final StringRedisTemplate redisTemplate;

  @Value("${app.otp.rate-limit.max-requests}")
  private int maxRequests;

  @Value("${app.otp.rate-limit.window-minutes}")
  private long windowInMinutes;

  /** {@inheritDoc} */
  @Override
  public boolean isAllowed(final String key, final String ipAddress) {
    final String redisKey = AppConfigConstant.REDIS_RATE_LIMIT_PREFIX + key + ":" + ipAddress;
    final Long currentCount = redisTemplate.opsForValue().increment(redisKey);

    if (currentCount == null) {
      return false;
    }

    if (currentCount == 1) {
      redisTemplate.expire(redisKey, Duration.ofMinutes(windowInMinutes));
    }

    return currentCount <= maxRequests;
  }
}
