package com.natswarchuan.payment.demo.controller;

import com.natswarchuan.payment.demo.constant.ApiConstant;
import com.natswarchuan.payment.demo.constant.LogConstant;
import com.natswarchuan.payment.demo.constant.MessageConstant;
import com.natswarchuan.payment.demo.constant.SecurityConstant;
import com.natswarchuan.payment.demo.dto.request.user.UserSearchRequest;
import com.natswarchuan.payment.demo.dto.response.HttpResponseApi;
import com.natswarchuan.payment.demo.dto.response.user.UserDetailResponse;
import com.natswarchuan.payment.demo.dto.response.user.UserSummaryResponse;
import com.natswarchuan.payment.demo.entity.User;
import com.natswarchuan.payment.demo.interfaces.services.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Controller chịu trách nhiệm xử lý các yêu cầu liên quan đến người dùng. */
@RestController
@RequestMapping(ApiConstant.USERS_ENDPOINT)
@RequiredArgsConstructor
@Slf4j
public class UserController {

  private final IUserService userService;

  /**
   * API dành cho quản trị viên để tìm kiếm, lọc và phân trang người dùng.
   *
   * @param searchRequest các tiêu chí tìm kiếm (keyword, role, ekycStatus).
   * @param pageable thông tin phân trang (page, size, sort).
   * @return một trang (Page) chứa thông tin tóm tắt của người dùng.
   */
  @GetMapping
  @PreAuthorize("hasRole('" + SecurityConstant.ROLE_ADMIN + "')")
  public ResponseEntity<Page<UserSummaryResponse>> searchUsers(
      final UserSearchRequest searchRequest, final Pageable pageable) {
    final Page<UserSummaryResponse> userPage = userService.searchUsers(searchRequest, pageable);
    return ResponseEntity.ok(userPage);
  }

  /**
   * Lấy thông tin chi tiết của người dùng đang đăng nhập.
   *
   * @param currentUser người dùng đã được xác thực, được inject tự động.
   * @return ResponseEntity chứa thông tin chi tiết của người dùng.
   */
  @GetMapping(ApiConstant.USERS_ME)
  @PreAuthorize("isAuthenticated() and hasAuthority('" + SecurityConstant.PERMISSION_USER_READ + "')")
  public ResponseEntity<UserDetailResponse> getCurrentUser(
      @AuthenticationPrincipal final User currentUser) {
    log.info(LogConstant.FETCHING_USER_DETAILS, currentUser.getId());
    final UserDetailResponse userDetail = userService.getUserDetails(currentUser.getId());
    return ResponseEntity.ok(userDetail);
  }

  /**
   * Endpoint để client gọi sau khi người dùng hoàn tất quy trình eKYC trên một hệ thống bên ngoài.
   *
   * <p>API này sẽ cập nhật trạng thái eKYC của người dùng và gán vai trò tương ứng để cho phép họ
   * thực hiện các giao dịch tài chính.
   *
   * @param currentUser người dùng đã được xác thực.
   * @return ResponseEntity chứa thông báo thành công.
   */
  @PostMapping(ApiConstant.USERS_ME_EKYC_COMPLETE)
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<HttpResponseApi<Void>> completeEkyc(
      @AuthenticationPrincipal final User currentUser) {
    userService.completeEkyc(currentUser.getId());
    return new HttpResponseApi.Ok<>(MessageConstant.User.EKYC_COMPLETED_SUCCESS);
  }
}
