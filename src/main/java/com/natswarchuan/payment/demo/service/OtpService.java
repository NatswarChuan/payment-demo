package com.natswarchuan.payment.demo.service;

import com.natswarchuan.payment.demo.constant.LogConstant;
import com.natswarchuan.payment.demo.constant.MessageConstant;
import com.natswarchuan.payment.demo.exception.custom.RateLimitExceededException;
import com.natswarchuan.payment.demo.helper.OtpHelper;
import com.natswarchuan.payment.demo.interfaces.services.IOtpService;
import com.natswarchuan.payment.demo.interfaces.services.IRateLimiterService;
import com.natswarchuan.payment.demo.repository.OtpRepository;
import com.natswarchuan.payment.demo.util.HttpUtil;
import com.natswarchuan.payment.demo.util.IdentifierValidator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Cung cấp các dịch vụ liên quan đến việc tạo, gửi và xác thực Mã xác thực một lần (OTP).
 *
 * <p>Dịch vụ này tích hợp cơ chế giới hạn tần suất (rate limiting) để ngăn chặn việc lạm dụng tính
 * năng gửi OTP và sử dụng các dịch vụ bên ngoài để gửi OTP qua Email và SMS.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OtpService implements IOtpService {

  private final OtpRepository otpRepository;
  private final IRateLimiterService rateLimiterService;
  private final OtpHelper otpServiceHelper;

  @Value("${app.otp.rate-limit.window-minutes}")
  private long rateLimitWindowMinutes;

  /** {@inheritDoc} */
  @Override
  public void sendOtp(final String identifier, final HttpServletRequest request) {
    final String ipAddress = HttpUtil.getClientIp(request);
    if (!rateLimiterService.isAllowed(identifier, ipAddress)) {
      throw new RateLimitExceededException(
          MessageConstant.Otp.OTP_RATE_LIMIT_EXCEEDED, rateLimitWindowMinutes);
    }

    final String otp = otpServiceHelper.generateOtp();
    otpRepository.saveOtp(identifier, otp);

    if (IdentifierValidator.isEmail(identifier)) {
      otpServiceHelper.sendOtpViaEmail(identifier, otp);
    } else if (IdentifierValidator.isPhoneNumber(identifier)) {
      final String normalizedPhone = IdentifierValidator.normalizePhoneNumber(identifier);
      if (normalizedPhone != null) {
        otpServiceHelper.sendOtpViaSms(normalizedPhone, otp);
      }
    } else {
      log.warn(LogConstant.INVALID_IDENTIFIER_FOR_OTP, identifier);
    }
  }

  /** {@inheritDoc} */
  @Override
  public boolean verifyOtp(final String identifier, final String otp) {
    return otpRepository
        .getOtp(identifier)
        .map(
            savedOtp -> {
              final boolean isValid = savedOtp.equals(otp);
              if (isValid) {
                otpRepository.deleteOtp(identifier);
                log.info(LogConstant.OTP_VERIFIED_SUCCESS, identifier);
              } else {
                log.warn(LogConstant.INVALID_OTP_PROVIDED, identifier);
              }
              return isValid;
            })
        .orElse(false);
  }
}
