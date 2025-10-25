package com.natswarchuan.payment.demo.dto.request.user;

import lombok.Data;

/**
 * DTO (Data Transfer Object) chứa các tiêu chí để tìm kiếm và lọc người dùng.
 *
 * <p>Đối tượng này cho phép quản trị viên tìm kiếm người dùng dựa trên nhiều thuộc tính khác nhau,
 * bao gồm cả thông tin từ hồ sơ người dùng (UserProfile).
 */
@Data
public class UserSearchRequest {

  /**
   * Một chuỗi tìm kiếm chung.
   *
   * <p>Hệ thống sẽ tìm kiếm chuỗi này trong các trường email, số điện thoại, tên đầy đủ, và biệt
   * danh (nickname) của người dùng.
   */
  private String keyword;

  /** Tên vai trò (role) mà người dùng phải có (ví dụ: "ADMIN", "USER"). */
  private String role;

  /** Trạng thái eKYC của người dùng. */
  private Integer ekycStatus;
}
