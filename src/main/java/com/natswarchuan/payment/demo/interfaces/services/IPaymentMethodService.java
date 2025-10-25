package com.natswarchuan.payment.demo.interfaces.services;

import com.natswarchuan.payment.demo.dto.request.paymentmethod.CreatePaymentMethodRequest;
import com.natswarchuan.payment.demo.dto.request.paymentmethod.LinkEwalletRequest;
import com.natswarchuan.payment.demo.entity.PaymentMethod;
import com.natswarchuan.payment.demo.interfaces.IService;
import java.util.UUID;

/**
 * Interface định nghĩa các hợp đồng cho dịch vụ quản lý phương thức thanh toán của người dùng.
 *
 * <p>Bao gồm các nghiệp vụ như liên kết, cập nhật và xóa các phương thức thanh toán (tài khoản ngân
 * hàng, ví điện tử).
 */
public interface IPaymentMethodService extends IService<PaymentMethod, UUID> {

  /**
   * Xóa một phương thức thanh toán, đồng thời đảm bảo rằng chỉ chủ sở hữu mới có thể thực hiện hành
   * động này.
   *
   * <p>Phương thức này được thiết kế để ngăn chặn lỗ hổng IDOR (Insecure Direct Object Reference)
   * bằng cách kiểm tra quyền sở hữu trước khi xóa.
   *
   * @param id ID của phương thức thanh toán cần xóa.
   * @throws com.natswarchuan.payment.demo.exception.custom.ForbiddenException nếu người dùng hiện
   *     tại không phải là chủ sở hữu.
   */
  void deleteForCurrentUser(UUID id);

  /**
   * Liên kết hoặc cập nhật thông tin ví điện tử cho người dùng đang đăng nhập.
   *
   * <p>Nếu một ví điện tử với cùng nhà cung cấp và số tài khoản đã tồn tại và thuộc về người dùng,
   * thông tin sẽ được cập nhật. Nếu không, một liên kết mới sẽ được tạo.
   *
   * @param request dữ liệu yêu cầu chứa thông tin về ví điện tử.
   * @return thực thể {@link PaymentMethod} đã được tạo hoặc cập nhật.
   */
  PaymentMethod linkOrUpdateEwallet(LinkEwalletRequest request);

  /**
   * Liên kết hoặc cập nhật thông tin tài khoản ngân hàng cho người dùng đang đăng nhập.
   *
   * <p>Tương tự như liên kết ví điện tử, phương thức này sẽ cập nhật nếu tài khoản đã tồn tại và
   * thuộc về người dùng, ngược lại sẽ tạo mới.
   *
   * @param request dữ liệu yêu cầu chứa thông tin về tài khoản ngân hàng.
   * @return thực thể {@link PaymentMethod} đã được tạo hoặc cập nhật.
   */
  PaymentMethod linkOrUpdateBankAccount(CreatePaymentMethodRequest request);
}
