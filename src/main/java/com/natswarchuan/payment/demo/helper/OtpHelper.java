package com.natswarchuan.payment.demo.helper;

import com.natswarchuan.payment.demo.constant.AppConfigConstant;
import com.natswarchuan.payment.demo.constant.LogConstant;
import com.natswarchuan.payment.demo.constant.MessageConstant;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * Lớp helper chứa các logic phụ trợ cho {@link com.natswarchuan.payment.demo.service.OtpService}.
 *
 * <p>Lớp này chịu trách nhiệm cho các công việc cụ thể như tạo mã OTP và gửi chúng qua các kênh
 * khác nhau (Email, SMS), giúp lớp service chính gọn gàng hơn.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class OtpHelper {

  private final JavaMailSender emailSender;

  @Value("${spring.mail.from}")
  private String mailFrom;

  /**
   * Gửi một mã OTP đến một địa chỉ email.
   *
   * @param email Địa chỉ email của người nhận.
   * @param otp Mã OTP cần gửi.
   */
  public void sendOtpViaEmail(String email, String otp) {
    log.info(LogConstant.SENDING_OTP_EMAIL, email);
    try {
      SimpleMailMessage message = new SimpleMailMessage();
      message.setFrom(mailFrom);
      message.setTo(email);
      message.setSubject(MessageConstant.Otp.EMAIL_SUBJECT);
      message.setText(String.format(MessageConstant.Otp.EMAIL_BODY, otp));
      emailSender.send(message);
      log.info(LogConstant.OTP_EMAIL_SENT_SUCCESS, email);
    } catch (Exception e) {
      log.error(LogConstant.OTP_EMAIL_SEND_ERROR, email, e.getMessage());
    }
  }

  /**
   * Gửi một mã OTP đến một số điện thoại qua SMS.
   *
   * @param phoneNumber Số điện thoại của người nhận.
   * @param otp Mã OTP cần gửi.
   */
  public void sendOtpViaSms(String phoneNumber, String otp) {
    String message = String.format(MessageConstant.Otp.SMS_BODY, otp);
  }

  /**
   * Tạo một mã OTP ngẫu nhiên gồm 6 chữ số.
   *
   * @return Một chuỗi chứa mã OTP.
   */
  public String generateOtp() {
    return new DecimalFormat(AppConfigConstant.OTP_FORMAT)
        .format(new SecureRandom().nextInt(999999));
  }
}
