package com.natswarchuan.payment.demo.service;

import com.natswarchuan.payment.demo.constant.MessageConstant;
import com.natswarchuan.payment.demo.exception.custom.BadRequestException;
import com.natswarchuan.payment.demo.interfaces.services.IPaymentGatewayService;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * Lớp factory chịu trách nhiệm cung cấp các thể hiện (instance) của dịch vụ cổng thanh toán.
 *
 * <p>Lớp này áp dụng mẫu thiết kế Factory để quản lý và truy xuất các dịch vụ thanh toán khác nhau
 * (ví dụ: VNPAY, Momo). Nó tự động phát hiện tất cả các bean triển khai {@link
 * IPaymentGatewayService} và ánh xạ chúng với tên nhà cung cấp tương ứng. Điều này giúp hệ thống dễ
 * dàng mở rộng bằng cách thêm các nhà cung cấp mới mà không cần thay đổi logic nghiệp vụ cốt lõi.
 */
@Component
public class PaymentGatewayFactory {

  /**
   * Một Map lưu trữ các dịch vụ cổng thanh toán, với key là tên nhà cung cấp (viết hoa) và value là
   * thể hiện của dịch vụ đó.
   */
  private final Map<String, IPaymentGatewayService> gatewayServiceMap;

  /**
   * Khởi tạo factory và xây dựng map các dịch vụ cổng thanh toán.
   *
   * <p>Spring Boot sẽ tự động inject một danh sách tất cả các bean có triển khai interface {@code
   * IPaymentGatewayService}. Constructor này sẽ duyệt qua danh sách, lấy tên nhà cung cấp từ mỗi
   * dịch vụ, và đưa vào map để truy xuất nhanh chóng.
   *
   * @param gatewayServices Một danh sách các thể hiện của {@code IPaymentGatewayService} được
   *     inject tự động bởi Spring.
   */
  public PaymentGatewayFactory(List<IPaymentGatewayService> gatewayServices) {
    this.gatewayServiceMap =
        gatewayServices.stream()
            .collect(Collectors.toMap(s -> s.getProviderName().toUpperCase(), Function.identity()));
  }

  /**
   * Lấy ra dịch vụ cổng thanh toán tương ứng với tên nhà cung cấp được chỉ định.
   *
   * <p>Phương thức này không phân biệt chữ hoa chữ thường của tên nhà cung cấp.
   *
   * @param providerName Tên của nhà cung cấp cổng thanh toán (ví dụ: "VNPAY").
   * @return Thể hiện của {@code IPaymentGatewayService} tương ứng với nhà cung cấp.
   * @throws BadRequestException nếu không tìm thấy dịch vụ nào cho nhà cung cấp được yêu cầu.
   */
  public IPaymentGatewayService getGateway(String providerName) {
    IPaymentGatewayService service = gatewayServiceMap.get(providerName.toUpperCase());
    if (service == null) {
      throw new BadRequestException(MessageConstant.Payment.GATEWAY_NOT_SUPPORTED, providerName);
    }
    return service;
  }
}
