package com.natswarchuan.payment.demo.controller;

import com.natswarchuan.payment.demo.constant.ApiConstant;
import com.natswarchuan.payment.demo.constant.SecurityConstant;
import com.natswarchuan.payment.demo.dto.request.transaction.TransactionSearchRequest;
import com.natswarchuan.payment.demo.dto.request.transaction.TransferRequest;
import com.natswarchuan.payment.demo.dto.response.transaction.TransactionSummaryResponse;
import com.natswarchuan.payment.demo.interfaces.services.ITransactionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller chịu trách nhiệm xử lý các yêu cầu liên quan đến giao dịch nội bộ.
 *
 * <p>Bao gồm các chức năng như chuyển tiền giữa các ví và truy vấn lịch sử giao dịch.
 */
@RestController
@RequestMapping(ApiConstant.TRANSACTIONS_ENDPOINT)
@RequiredArgsConstructor
public class TransactionController {

  private final ITransactionService transactionService;

  /**
   * Endpoint để thực hiện chuyển tiền giữa hai ví trong hệ thống.
   *
   * @param transferRequest thông tin chi tiết của yêu cầu chuyển tiền.
   * @return {@code ResponseEntity} với trạng thái OK nếu yêu cầu hợp lệ.
   */
  @PostMapping(ApiConstant.TRANSACTIONS_TRANSFER)
  @PreAuthorize(
      "isAuthenticated() and hasAuthority('" + SecurityConstant.PERMISSION_FINANCE_TRANSACT + "')")
  public ResponseEntity<Void> transferFunds(
      @Valid @RequestBody final TransferRequest transferRequest) {
    transactionService.initiateTransfer(transferRequest);
    return ResponseEntity.ok().build();
  }

  /**
   * Endpoint để tạo một khóa bí mật dùng một lần cho giao dịch chuyển tiền.
   *
   * @param request đối tượng HttpServletRequest để lấy địa chỉ IP của client.
   * @return {@code ResponseEntity} chứa chuỗi khóa bí mật.
   */
  @GetMapping(ApiConstant.TRANSACTIONS_TRANSFER_SECRET_KEY)
  @PreAuthorize(
      "isAuthenticated() and hasAuthority('" + SecurityConstant.PERMISSION_FINANCE_TRANSACT + "')")
  public ResponseEntity<String> getSecretKey(final HttpServletRequest request) {
    return ResponseEntity.ok(transactionService.createTransferSecretKey(request));
  }

  /**
   * Endpoint để tìm kiếm và phân trang lịch sử giao dịch của người dùng hiện tại.
   *
   * @param searchRequest các tiêu chí tìm kiếm.
   * @param pageable thông tin phân trang và sắp xếp.
   * @return một trang (Page) chứa danh sách các giao dịch phù hợp.
   */
  @GetMapping
  @PreAuthorize(
      "isAuthenticated() and hasAuthority('" + SecurityConstant.PERMISSION_TRANSACTION_READ + "')")
  public ResponseEntity<Page<TransactionSummaryResponse>> searchTransactions(
      @Valid final TransactionSearchRequest searchRequest, final Pageable pageable) {
    final Page<TransactionSummaryResponse> results =
        transactionService.searchTransactionsForCurrentUser(searchRequest, pageable);
    return ResponseEntity.ok(results);
  }
}
