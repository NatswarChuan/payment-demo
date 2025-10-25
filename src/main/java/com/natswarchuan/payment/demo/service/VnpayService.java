package com.natswarchuan.payment.demo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.natswarchuan.payment.demo.constant.AppConfigConstant;
import com.natswarchuan.payment.demo.constant.LogConstant;
import com.natswarchuan.payment.demo.constant.MessageConstant;
import com.natswarchuan.payment.demo.constant.TransactionConstant;
import com.natswarchuan.payment.demo.constant.VnpayConstant;
import com.natswarchuan.payment.demo.dto.request.gateway.CreatePaymentRequest;
import com.natswarchuan.payment.demo.dto.request.gateway.VnpayPaymentRequest;
import com.natswarchuan.payment.demo.dto.request.gateway.VnpayPayoutRequest;
import com.natswarchuan.payment.demo.dto.response.gateway.CreatePaymentResponse;
import com.natswarchuan.payment.demo.dto.response.gateway.PayoutResponse;
import com.natswarchuan.payment.demo.dto.response.gateway.ProcessIpnResponse;
import com.natswarchuan.payment.demo.dto.response.gateway.VnpayApiResponse;
import com.natswarchuan.payment.demo.entity.Transaction;
import com.natswarchuan.payment.demo.helper.VnpayHelper;
import com.natswarchuan.payment.demo.interfaces.services.IPaymentGatewayService;
import com.natswarchuan.payment.demo.util.VnpayUtil;
import jakarta.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Lớp dịch vụ triển khai các nghiệp vụ liên quan đến cổng thanh toán VNPAY.
 *
 * <p>Chịu trách nhiệm tạo yêu cầu thanh toán, xử lý yêu cầu rút tiền và xử lý thông báo IPN từ
 * VNPAY.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class VnpayService implements IPaymentGatewayService {

  @Value("${vnpay.url}")
  private String vnpayUrl;

  @Value("${vnpay.payout-url}")
  private String vnpayPayoutUrl;

  @Value("${vnpay.tmn-code}")
  private String tmnCode;

  @Value("${vnpay.hash-secret}")
  private String hashSecret;

  @Value("${vnpay.version}")
  private String version;

  private final RestTemplate restTemplate;
  private final ObjectMapper objectMapper;
  private final VnpayHelper vnpayServiceHelper;

  @Override
  public String getProviderName() {
    return "VNPAY";
  }

  /** {@inheritDoc} */
  @Override
  public CreatePaymentResponse createPayment(CreatePaymentRequest request) {
    Calendar cld = Calendar.getInstance(TimeZone.getTimeZone(AppConfigConstant.TIMEZONE_GMT7));
    SimpleDateFormat formatter = new SimpleDateFormat(AppConfigConstant.DATE_FORMAT_YYYYMMDDHHMMSS);

    VnpayPaymentRequest vnpayRequest =
        VnpayPaymentRequest.builder()
            .vnp_Version(version)
            .vnp_Command(VnpayConstant.COMMAND_PAY)
            .vnp_TmnCode(tmnCode)
            .vnp_Amount(request.getAmount().multiply(new BigDecimal(100)).toBigInteger().toString())
            .vnp_CurrCode(VnpayConstant.CURRENCY_CODE_VND)
            .vnp_TxnRef(request.getTransactionId().toString())
            .vnp_OrderInfo(request.getOrderInfo())
            .vnp_OrderType(VnpayConstant.ORDER_TYPE_OTHER)
            .vnp_Locale(VnpayConstant.LOCALE_VN)
            .vnp_ReturnUrl(request.getReturnUrl())
            .vnp_IpAddr(request.getIpAddr())
            .vnp_CreateDate(formatter.format(cld.getTime()))
            .build();

    String hashData = VnpayUtil.hashAllFields(vnpayRequest, hashSecret);
    vnpayRequest.setVnp_SecureHash(hashData);

    Map<String, String> vnp_Params = vnpayRequest.toMap();
    StringBuilder query = new StringBuilder();
    vnp_Params.forEach(
        (key, value) -> {
          try {
            query.append(URLEncoder.encode(key, StandardCharsets.US_ASCII.toString()));
            query.append('=');
            query.append(URLEncoder.encode(value, StandardCharsets.US_ASCII.toString()));
            query.append('&');
          } catch (UnsupportedEncodingException e) {
            log.error("Lỗi mã hóa tham số URL", e);
          }
        });
    query.setLength(query.length() - 1);

    String paymentUrl = vnpayUrl + "?" + query;
    return CreatePaymentResponse.builder().paymentUrl(paymentUrl).build();
  }

  /** {@inheritDoc} */
  @Override
  public PayoutResponse initiatePayout(Transaction transaction) {
    log.info(LogConstant.VNPAY_PAYOUT_INITIATE, transaction.getId());

    SimpleDateFormat formatter = new SimpleDateFormat(AppConfigConstant.DATE_FORMAT_YYYYMMDDHHMMSS);
    String createDate = formatter.format(new Date());
    String requestId = transaction.getId().toString() + "_" + System.currentTimeMillis();

    VnpayPayoutRequest payoutRequest =
        VnpayPayoutRequest.builder()
            .vnp_RequestId(requestId)
            .vnp_Version(version)
            .vnp_Command(VnpayConstant.COMMAND_PAYOUT)
            .vnp_TmnCode(tmnCode)
            .vnp_TxnRef(transaction.getId().toString())
            .vnp_OrderInfo(
                String.format(
                    TransactionConstant.Description.WITHDRAWAL, transaction.getId().toString()))
            .vnp_Amount(
                transaction.getAmount().multiply(new BigDecimal(100)).toBigInteger().toString())
            .vnp_CreateDate(createDate)
            .vnp_IpAddr(transaction.getIpAddress())
            .vnp_BankCode(transaction.getPaymentMethod().getProvider())
            .vnp_AccountNo(transaction.getPaymentMethod().getAccountNumber())
            .vnp_AccountName(transaction.getPaymentMethod().getAccountName())
            .vnp_PayMethod(VnpayConstant.PAY_METHOD_ACCOUNT)
            .build();

    String hashData = VnpayUtil.hashAllFields(payoutRequest, hashSecret);
    payoutRequest.setVnp_SecureHash(hashData);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    try {
      String requestBody = objectMapper.writeValueAsString(payoutRequest);
      HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

      log.info(LogConstant.VNPAY_PAYOUT_REQUEST_SEND, payoutRequest.toMaskedString());
      ResponseEntity<VnpayApiResponse> responseEntity =
          restTemplate.postForEntity(vnpayPayoutUrl, entity, VnpayApiResponse.class);

      VnpayApiResponse apiResponse = responseEntity.getBody();
      log.info(LogConstant.VNPAY_PAYOUT_RESPONSE_RECEIVE, apiResponse);

      if (apiResponse != null
          && VnpayConstant.API_RESPONSE_SUCCESS_CODE.equals(apiResponse.getResponseCode())) {
        return PayoutResponse.builder()
            .isSuccess(true)
            .message(String.format(MessageConstant.Vnpay.PAYOUT_ACCEPTED, apiResponse.getMessage()))
            .providerTransactionId(apiResponse.getTransactionNo())
            .build();
      } else {
        String errorMessage =
            apiResponse != null ? apiResponse.getMessage() : MessageConstant.Vnpay.UNKNOWN_ERROR;
        return PayoutResponse.builder()
            .isSuccess(false)
            .message(String.format(MessageConstant.Vnpay.PAYOUT_REJECTED, errorMessage))
            .build();
      }

    } catch (Exception e) {
      log.error(LogConstant.VNPAY_PAYOUT_GENERAL_ERROR, e);
      return PayoutResponse.builder()
          .isSuccess(false)
          .message(String.format(MessageConstant.Vnpay.PAYOUT_INTERNAL_ERROR, e.getMessage()))
          .build();
    }
  }

  /** {@inheritDoc} */
  @Override
  public ProcessIpnResponse processIpn(HttpServletRequest request) {
    log.info("Đang xử lý IPN chung từ VNPAY...");
    return vnpayServiceHelper.processGenericIpn(request);
  }
}
