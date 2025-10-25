package com.natswarchuan.payment.demo.controller;

import com.natswarchuan.payment.demo.constant.ApiConstant;
import com.natswarchuan.payment.demo.constant.MessageConstant;
import com.natswarchuan.payment.demo.constant.SecurityConstant;
import com.natswarchuan.payment.demo.dto.request.transaction.DepositRequest;
import com.natswarchuan.payment.demo.dto.request.transaction.WithdrawRequest;
import com.natswarchuan.payment.demo.dto.response.gateway.CreatePaymentResponse;
import com.natswarchuan.payment.demo.dto.response.gateway.ProcessIpnResponse;
import com.natswarchuan.payment.demo.exception.custom.ForbiddenException;
import com.natswarchuan.payment.demo.interfaces.services.IPaymentGatewayService;
import com.natswarchuan.payment.demo.interfaces.services.ITransactionService;
import com.natswarchuan.payment.demo.service.PaymentGatewayFactory;
import com.natswarchuan.payment.demo.util.HttpUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller chịu trách nhiệm xử lý các yêu cầu thanh toán qua các cổng thanh toán bên ngoài.
 *
 * <p>Bao gồm các endpoint để khởi tạo giao dịch nạp/rút tiền và nhận thông báo IPN (Instant Payment
 * Notification) từ nhà cung cấp dịch vụ.
 */
@RestController
@RequestMapping(ApiConstant.PAYMENTS_ENDPOINT)
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

  private final ITransactionService transactionService;
  private final PaymentGatewayFactory paymentGatewayFactory;

  @Value("${vnpay.allowed-ips}")
  private List<String> allowedIps;

  /**
   * Endpoint để khởi tạo một yêu cầu nạp tiền.
   *
   * @param provider nhà cung cấp cổng thanh toán (ví dụ: VNPAY).
   * @param depositRequest dữ liệu yêu cầu nạp tiền.
   * @param httpServletRequest đối tượng request để lấy địa chỉ IP của client.
   * @return phản hồi chứa URL để chuyển hướng người dùng đến trang thanh toán.
   */
  @PostMapping("/deposit/{provider}")
  @PreAuthorize(
      "isAuthenticated() and hasAuthority('" + SecurityConstant.PERMISSION_FINANCE_TRANSACT + "')")
  public ResponseEntity<CreatePaymentResponse> deposit(
      @PathVariable final String provider,
      @Valid @RequestBody final DepositRequest depositRequest,
      final HttpServletRequest httpServletRequest) {
    final CreatePaymentResponse paymentResponse =
        transactionService.initiateDeposit(depositRequest, provider, httpServletRequest);
    return ResponseEntity.ok(paymentResponse);
  }

  /**
   * Endpoint để khởi tạo một yêu cầu rút tiền.
   *
   * @param provider nhà cung cấp dịch vụ rút tiền (ví dụ: VNPAY).
   * @param withdrawRequest dữ liệu yêu cầu rút tiền.
   * @param httpServletRequest đối tượng request để lấy địa chỉ IP của client.
   * @return phản hồi với trạng thái 202 (Accepted) để cho biết yêu cầu đã được chấp nhận và đang
   * được xử lý.
   */
  @PostMapping("/withdraw/{provider}")
  @PreAuthorize(
      "isAuthenticated() and hasAuthority('" + SecurityConstant.PERMISSION_FINANCE_TRANSACT + "')")
  public ResponseEntity<Void> withdraw(
      @PathVariable final String provider,
      @Valid @RequestBody final WithdrawRequest withdrawRequest,
      final HttpServletRequest httpServletRequest) {
    transactionService.initiateWithdrawal(withdrawRequest, provider, httpServletRequest);
    return ResponseEntity.accepted().build();
  }

  /**
   * Endpoint để nhận và xử lý thông báo IPN cho giao dịch nạp tiền.
   *
   * <p><b>Bảo mật:</b> Endpoint này được bảo vệ bằng cách chỉ cho phép các địa chỉ IP nằm trong
   * danh sách trắng (whitelist) được cấu hình.
   *
   * @param provider nhà cung cấp đã gửi IPN.
   * @param request đối tượng request chứa dữ liệu IPN.
   * @return phản hồi cho nhà cung cấp dịch vụ.
   */
  @PostMapping("/ipn/{provider}")
  public ResponseEntity<String> handleIpn(
      @PathVariable final String provider, final HttpServletRequest request) {
    checkIpAllowed(request);
    log.info("Nhận được IPN nạp tiền từ provider: {}", provider);
    final IPaymentGatewayService gatewayService = paymentGatewayFactory.getGateway(provider);
    final ProcessIpnResponse ipnResponse = gatewayService.processIpn(request);

    transactionService.completeDeposit(ipnResponse);

    return ResponseEntity.ok(MessageConstant.Payment.IPN_RECEIVED);
  }

  /**
   * Endpoint để nhận và xử lý thông báo IPN cho giao dịch rút tiền.
   *
   * @param provider nhà cung cấp đã gửi IPN.
   * @param request đối tượng request chứa dữ liệu IPN.
   * @return phản hồi cho nhà cung cấp dịch vụ.
   */
  @PostMapping("/ipn/withdraw/{provider}")
  public ResponseEntity<String> handleWithdrawalIpn(
      @PathVariable final String provider, final HttpServletRequest request) {
    checkIpAllowed(request);
    log.info("Nhận được IPN rút tiền từ provider: {}", provider);
    final IPaymentGatewayService gatewayService = paymentGatewayFactory.getGateway(provider);
    final ProcessIpnResponse ipnResponse = gatewayService.processIpn(request);

    transactionService.completeWithdrawal(ipnResponse);

    return ResponseEntity.ok(MessageConstant.Payment.IPN_RECEIVED);
  }

  /**
   * Kiểm tra xem IP của client có nằm trong danh sách trắng được phép gọi IPN hay không.
   *
   * @param request đối tượng HttpServletRequest.
   * @throws ForbiddenException nếu IP không được phép.
   */
  private void checkIpAllowed(final HttpServletRequest request) {
    final String clientIp = HttpUtil.getClientIp(request);
    if (allowedIps == null || !allowedIps.contains(clientIp)) {
      log.warn("Yêu cầu IPN bị cấm từ IP: {}", clientIp);
      throw new ForbiddenException(MessageConstant.Auth.UNAUTHORIZED);
    }
  }
}
