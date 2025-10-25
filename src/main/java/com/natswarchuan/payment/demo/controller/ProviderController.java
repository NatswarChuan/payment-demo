package com.natswarchuan.payment.demo.controller;

import com.natswarchuan.payment.demo.constant.ApiConstant;
import com.natswarchuan.payment.demo.dto.response.provider.ProviderResponse;
import com.natswarchuan.payment.demo.interfaces.services.IProviderService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller chịu trách nhiệm xử lý các yêu cầu liên quan đến nhà cung cấp dịch vụ thanh toán.
 */
@RestController
@RequestMapping(ApiConstant.PROVIDERS_ENDPOINT)
@RequiredArgsConstructor
public class ProviderController {

  private final IProviderService providerService;

  /**
   * API để lấy danh sách tất cả các nhà cung cấp cổng thanh toán được hệ thống hỗ trợ.
   *
   * @return ResponseEntity chứa danh sách các nhà cung cấp.
   */
  @GetMapping
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<List<ProviderResponse>> getProviders() {
    List<ProviderResponse> providers = providerService.getAvailableProviders();
    return ResponseEntity.ok(providers);
  }
}

