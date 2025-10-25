package com.natswarchuan.payment.demo.entity;

import com.natswarchuan.payment.demo.constant.AppConfigConstant;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RedisHash(AppConfigConstant.REDIS_USER_SESSION_HASH)
public class UserSession {

  @Id private String id;
  @Indexed private Long userId;
  private String ipAddress;
  private String userAgent;
  private Instant createdAt;
  private Instant expiresAt;
}
