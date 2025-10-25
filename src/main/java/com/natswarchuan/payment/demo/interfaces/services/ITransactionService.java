package com.natswarchuan.payment.demo.interfaces.services;

import com.natswarchuan.payment.demo.dto.request.transaction.DepositRequest;
import com.natswarchuan.payment.demo.dto.request.transaction.TransactionSearchRequest;
import com.natswarchuan.payment.demo.dto.request.transaction.TransferRequest;
import com.natswarchuan.payment.demo.dto.request.transaction.WithdrawRequest;
import com.natswarchuan.payment.demo.dto.response.gateway.CreatePaymentResponse;
import com.natswarchuan.payment.demo.dto.response.gateway.ProcessIpnResponse;
import com.natswarchuan.payment.demo.dto.response.transaction.TransactionSummaryResponse;
import com.natswarchuan.payment.demo.entity.Transaction;
import com.natswarchuan.payment.demo.interfaces.IService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/** Interface cho dịch vụ quản lý các nghiệp vụ liên quan đến {@link Transaction}. */
public interface ITransactionService extends IService<Transaction, UUID> {

  /**
   * Khởi tạo một giao dịch nạp tiền.
   *
   * @param request Dữ liệu yêu cầu nạp tiền.
   * @param gatewayProvider Tên nhà cung cấp cổng thanh toán.
   * @param httpServletRequest Đối tượng request để lấy thông tin client.
   * @return Phản hồi chứa thông tin để chuyển hướng đến cổng thanh toán.
   */
  CreatePaymentResponse initiateDeposit(
      DepositRequest request, String gatewayProvider, HttpServletRequest httpServletRequest);

  /**
   * Hoàn tất một giao dịch nạp tiền sau khi nhận được thông báo IPN.
   *
   * @param ipnResponse Dữ liệu đã được xử lý từ IPN.
   */
  void completeDeposit(ProcessIpnResponse ipnResponse);

  /**
   * Hoàn tất một giao dịch rút tiền sau khi nhận được thông báo IPN.
   *
   * @param ipnResponse Dữ liệu đã được xử lý từ IPN.
   */
  void completeWithdrawal(ProcessIpnResponse ipnResponse);

  /**
   * Khởi tạo một giao dịch rút tiền.
   *
   * @param request Dữ liệu yêu cầu rút tiền.
   * @param provider Tên nhà cung cấp dịch vụ rút tiền.
   * @param httpServletRequest Đối tượng request để lấy thông tin client.
   */
  void initiateWithdrawal(
      WithdrawRequest request, String provider, HttpServletRequest httpServletRequest);

  /**
   * Thực hiện chuyển tiền giữa hai ví.
   *
   * @param request Dữ liệu yêu cầu chuyển tiền.
   */
  void initiateTransfer(TransferRequest request);

  /**
   * Tạo một khóa bí mật dùng một lần cho giao dịch chuyển tiền.
   *
   * @param request Đối tượng HttpServletRequest để lấy địa chỉ IP.
   * @return Chuỗi khóa bí mật.
   */
  String createTransferSecretKey(HttpServletRequest request);

  /**
   * Tìm kiếm và phân trang lịch sử giao dịch của người dùng hiện tại dựa trên các tiêu chí lọc.
   *
   * @param searchRequest đối tượng chứa các tiêu chí tìm kiếm.
   * @param pageable thông tin phân trang và sắp xếp.
   * @return một trang (Page) chứa các giao dịch phù hợp.
   */
  Page<TransactionSummaryResponse> searchTransactionsForCurrentUser(
      TransactionSearchRequest searchRequest, Pageable pageable);
}
