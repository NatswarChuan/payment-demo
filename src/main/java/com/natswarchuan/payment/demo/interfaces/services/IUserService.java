package com.natswarchuan.payment.demo.interfaces.services;

import com.natswarchuan.payment.demo.dto.request.auth.VerifyRegistrationRequest;
import com.natswarchuan.payment.demo.dto.request.user.UserSearchRequest;
import com.natswarchuan.payment.demo.dto.response.user.UserDetailResponse;
import com.natswarchuan.payment.demo.dto.response.user.UserSummaryResponse;
import com.natswarchuan.payment.demo.entity.User;
import com.natswarchuan.payment.demo.interfaces.IService;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface IUserService extends IService<User, Long>, UserDetailsService {

  User createUser(VerifyRegistrationRequest request);

  Optional<User> findByIdentifier(String identifier);

  /**
   * Tìm người dùng theo định danh, bao gồm cả những người dùng đã bị xóa mềm.
   *
   * @param identifier Email hoặc số điện thoại.
   * @return {@code Optional<User>} chứa người dùng nếu tìm thấy.
   */
  Optional<User> findByIdentifierWithSoftDeleted(String identifier);

  UserDetailResponse getUserDetails(Long userId);

  /**
   * Hoàn tất quy trình eKYC cho một người dùng.
   *
   * @param userId ID của người dùng đã hoàn thành eKYC.
   */
  void completeEkyc(Long userId);

  /**
   * Tìm kiếm, lọc và phân trang danh sách người dùng dựa trên các tiêu chí động.
   *
   * @param searchRequest đối tượng chứa các tiêu chí tìm kiếm.
   * @param pageable thông tin phân trang và sắp xếp.
   * @return một trang (Page) chứa thông tin tóm tắt của những người dùng phù hợp.
   */
  Page<UserSummaryResponse> searchUsers(UserSearchRequest searchRequest, Pageable pageable);
}
