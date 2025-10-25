package com.natswarchuan.payment.demo.controller;

import com.natswarchuan.payment.demo.constant.ApiConstant;
import com.natswarchuan.payment.demo.constant.MessageConstant;
import com.natswarchuan.payment.demo.constant.SecurityConstant;
import com.natswarchuan.payment.demo.dto.request.paymentmethod.CreatePaymentMethodRequest;
import com.natswarchuan.payment.demo.dto.request.paymentmethod.LinkEwalletRequest;
import com.natswarchuan.payment.demo.dto.response.HttpResponseApi;
import com.natswarchuan.payment.demo.dto.response.paymentmethod.PaymentMethodSummaryResponse;
import com.natswarchuan.payment.demo.entity.PaymentMethod;
import com.natswarchuan.payment.demo.entity.User;
import com.natswarchuan.payment.demo.interfaces.services.IPaymentMethodService;
import com.natswarchuan.payment.demo.util.DtoMapper;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller chịu trách nhiệm xử lý các yêu cầu liên quan đến phương thức thanh toán của người
 * dùng, bao gồm cả tài khoản ngân hàng và ví điện tử.
 */
@RestController
@RequestMapping(ApiConstant.PAYMENT_METHODS_ENDPOINT)
@RequiredArgsConstructor
public class PaymentMethodController {

  private final IPaymentMethodService paymentMethodService;

  /**
   * Liên kết hoặc cập nhật thông tin tài khoản ngân hàng cho người dùng đang đăng nhập.
   *
   * @param request Dữ liệu để tạo hoặc cập nhật phương thức thanh toán.
   * @return {@code ResponseEntity} chứa thông tin tóm tắt của phương thức vừa xử lý.
   */
  @PostMapping
  @PreAuthorize(
      "isAuthenticated() and hasAuthority('" + SecurityConstant.PERMISSION_FINANCE_TRANSACT + "')")
  public ResponseEntity<HttpResponseApi<PaymentMethodSummaryResponse>> linkOrUpdateBankAccount(
      @Valid @RequestBody CreatePaymentMethodRequest request) {
    PaymentMethod bankAccount = paymentMethodService.linkOrUpdateBankAccount(request);
    PaymentMethodSummaryResponse responseDto = new PaymentMethodSummaryResponse();
    responseDto.fromEntity(bankAccount);

    boolean isUpdate = bankAccount.getCreatedAt().isBefore(bankAccount.getUpdatedAt());
    String message =
        isUpdate
            ? MessageConstant.Payment.BANK_ACCOUNT_UPDATE_SUCCESS
            : MessageConstant.Payment.BANK_ACCOUNT_LINK_SUCCESS;
    HttpStatus status = isUpdate ? HttpStatus.OK : HttpStatus.CREATED;

    return new ResponseEntity<>(new HttpResponseApi<>(status, message, responseDto), status);
  }

  /**
   * Liên kết hoặc cập nhật thông tin ví điện tử với tài khoản của người dùng.
   *
   * @param request Dữ liệu để liên kết hoặc cập nhật ví điện tử.
   * @return {@code ResponseEntity} chứa thông báo thành công và thông tin của ví vừa liên kết/cập
   * nhật.
   */
  @PostMapping(ApiConstant.PAYMENT_METHODS_EWALLET)
  @PreAuthorize(
      "isAuthenticated() and hasAuthority('" + SecurityConstant.PERMISSION_FINANCE_TRANSACT + "')")
  public ResponseEntity<HttpResponseApi<PaymentMethodSummaryResponse>> linkOrUpdateEwallet(
      @Valid @RequestBody LinkEwalletRequest request) {
    PaymentMethod ewallet = paymentMethodService.linkOrUpdateEwallet(request);
    PaymentMethodSummaryResponse responseDto = new PaymentMethodSummaryResponse();
    responseDto.fromEntity(ewallet);

    boolean isUpdate = ewallet.getCreatedAt().isBefore(ewallet.getUpdatedAt());
    String message =
        isUpdate
            ? MessageConstant.Payment.EWALLET_UPDATE_SUCCESS
            : MessageConstant.Payment.EWALLET_LINK_SUCCESS;
    HttpStatus status = isUpdate ? HttpStatus.OK : HttpStatus.CREATED;

    return new ResponseEntity<>(new HttpResponseApi<>(status, message, responseDto), status);
  }

  /**
   * Lấy danh sách tất cả các phương thức thanh toán (ngân hàng, ví điện tử) của người dùng đang
   * đăng nhập.
   *
   * @param currentUser Người dùng đã được xác thực.
   * @return {@code ResponseEntity} chứa danh sách các phương thức thanh toán.
   */
  @GetMapping
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<List<PaymentMethodSummaryResponse>> getUserPaymentMethods(
      @AuthenticationPrincipal User currentUser) {
    List<PaymentMethod> paymentMethods = currentUser.getWallet().getPaymentMethods();
    List<PaymentMethodSummaryResponse> response =
        DtoMapper.toDtoList(paymentMethods, PaymentMethodSummaryResponse::new);
    return ResponseEntity.ok(response);
  }

  /**
   * Hủy liên kết (xóa mềm) một phương thức thanh toán (tài khoản ngân hàng hoặc ví điện tử) dựa
   * trên ID.
   *
   * <p><b>Cải tiến bảo mật:</b> Phương thức này gọi {@code
   * paymentMethodService.deleteForCurrentUser(id)}, một phương thức an toàn sẽ kiểm tra quyền sở
   * hữu trước khi xóa, giúp ngăn chặn lỗ hổng IDOR (Insecure Direct Object Reference).
   *
   * @param id ID của phương thức thanh toán cần xóa.
   * @return {@code ResponseEntity} với trạng thái No Content.
   */
  @DeleteMapping("/{id}")
  @PreAuthorize(
      "isAuthenticated() and hasAuthority('" + SecurityConstant.PERMISSION_FINANCE_TRANSACT + "')")
  public ResponseEntity<Void> deletePaymentMethod(@PathVariable UUID id) {
    paymentMethodService.deleteForCurrentUser(id);
    return ResponseEntity.noContent().build();
  }
}

