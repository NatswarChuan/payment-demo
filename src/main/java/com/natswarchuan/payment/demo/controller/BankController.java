package com.natswarchuan.payment.demo.controller;

import com.natswarchuan.payment.demo.constant.ApiConstant;
import com.natswarchuan.payment.demo.dto.Bank;
import com.natswarchuan.payment.demo.interfaces.services.IBankService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller cung cấp các API liên quan đến thông tin ngân hàng.
 */
@RestController
@RequestMapping(ApiConstant.BANKS_ENDPOINT)
@RequiredArgsConstructor
public class BankController {

  private final IBankService bankService;

  /**
   * Lấy danh sách các ngân hàng được hệ thống hỗ trợ cho việc rút tiền.
   *
   * @return Danh sách thông tin ngân hàng.
   */
  @GetMapping
  public ResponseEntity<List<Bank>> getSupportedBanks() {
    return ResponseEntity.ok(bankService.getSupportedBanks());
  }
}
