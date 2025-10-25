package com.natswarchuan.payment.demo.controller;

import com.natswarchuan.payment.demo.constant.ApiConstant;
import com.natswarchuan.payment.demo.constant.SecurityConstant;
import com.natswarchuan.payment.demo.dto.request.wallet.SetPinRequest;
import com.natswarchuan.payment.demo.dto.response.wallet.WalletDetailResponse;
import com.natswarchuan.payment.demo.entity.User;
import com.natswarchuan.payment.demo.interfaces.services.IWalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiConstant.WALLETS_ENDPOINT)
@RequiredArgsConstructor
public class WalletController {

  private final IWalletService walletService;

  @GetMapping
  @PreAuthorize("hasAuthority('" + SecurityConstant.PERMISSION_USER_READ + "')")
  public ResponseEntity<WalletDetailResponse> getWalletById(
      @AuthenticationPrincipal User currentUser) {
    WalletDetailResponse walletDetail = walletService.findWalletForUser(currentUser.getId());
    return ResponseEntity.ok(walletDetail);
  }

  @PostMapping("/set-pin")
  @PreAuthorize(
      "isAuthenticated() and hasAuthority('" + SecurityConstant.PERMISSION_FINANCE_TRANSACT + "')")
  public ResponseEntity<Void> setPin(
      @AuthenticationPrincipal User currentUser, @Valid @RequestBody SetPinRequest request) {
    walletService.setPin(currentUser.getId(), request.getPin());
    return ResponseEntity.ok().build();
  }
}
