package com.natswarchuan.payment.demo.service;

import com.natswarchuan.payment.demo.dto.response.provider.ProviderResponse;
import com.natswarchuan.payment.demo.interfaces.services.IPaymentGatewayService;
import com.natswarchuan.payment.demo.interfaces.services.IProviderService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Lớp dịch vụ triển khai các nghiệp vụ liên quan đến nhà cung cấp cổng thanh toán.
 *
 * <p>Lớp này tự động phát hiện tất cả các bean triển khai {@link IPaymentGatewayService} để xây
 * dựng danh sách các nhà cung cấp có sẵn.
 */
@Service
@RequiredArgsConstructor
public class ProviderService implements IProviderService {

  private final List<IPaymentGatewayService> gatewayServices;

  /** {@inheritDoc} */
  @Override
  public List<ProviderResponse> getAvailableProviders() {
    return gatewayServices.stream()
        .map(service -> new ProviderResponse(service.getProviderName()))
        .collect(Collectors.toList());
  }
}
