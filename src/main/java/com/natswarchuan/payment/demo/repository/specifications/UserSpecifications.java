package com.natswarchuan.payment.demo.repository.specifications;

import com.natswarchuan.payment.demo.constant.EntityConstant;
import com.natswarchuan.payment.demo.dto.request.user.UserSearchRequest;
import com.natswarchuan.payment.demo.entity.Role;
import com.natswarchuan.payment.demo.entity.User;
import com.natswarchuan.payment.demo.entity.UserProfile;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Lớp chứa các {@link Specification} để xây dựng các truy vấn động, có khả năng tái sử dụng cho
 * thực thể {@link User}.
 */
@Component
public class UserSpecifications {

  /**
   * Xây dựng một đối tượng {@link Specification} hoàn chỉnh từ các tiêu chí tìm kiếm người dùng.
   *
   * @param searchRequest đối tượng chứa các tiêu chí tìm kiếm.
   * @return một {@code Specification<User>} tổng hợp tất cả các điều kiện lọc.
   */
  public Specification<User> fromSearchRequest(final UserSearchRequest searchRequest) {
    Specification<User> spec = Specification.where(null);

    if (StringUtils.hasText(searchRequest.getKeyword())) {
      spec = spec.and(keywordContains(searchRequest.getKeyword()));
    }
    if (StringUtils.hasText(searchRequest.getRole())) {
      spec = spec.and(hasRole(searchRequest.getRole()));
    }
    if (searchRequest.getEkycStatus() != null) {
      spec = spec.and(hasEkycStatus(searchRequest.getEkycStatus()));
    }

    return spec;
  }

  /**
   * Tạo một Specification để tìm kiếm keyword trong nhiều trường.
   *
   * @param keyword từ khóa tìm kiếm.
   * @return một {@code Specification} để tìm kiếm từ khóa.
   */
  private Specification<User> keywordContains(final String keyword) {
    return (root, query, criteriaBuilder) -> {
      final Join<User, UserProfile> profileJoin = root.join("userProfile", JoinType.LEFT);
      final String pattern = "%" + keyword.toLowerCase() + "%";

      return criteriaBuilder.or(
          criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), pattern),
          criteriaBuilder.like(root.get("phoneNumber"), pattern),
          criteriaBuilder.like(criteriaBuilder.lower(profileJoin.get("fullName")), pattern),
          criteriaBuilder.like(criteriaBuilder.lower(profileJoin.get("nickName")), pattern));
    };
  }

  /**
   * Tạo một Specification để lọc người dùng theo vai trò.
   *
   * @param roleName tên của vai trò.
   * @return một {@code Specification} để lọc theo vai trò.
   */
  private Specification<User> hasRole(final String roleName) {
    return (root, query, criteriaBuilder) -> {
      final Join<User, Role> roleJoin = root.join(EntityConstant.USER_FIELD_ROLES);
      return criteriaBuilder.equal(roleJoin.get(EntityConstant.FIELD_NAME), roleName);
    };
  }

  /**
   * Tạo một Specification để lọc người dùng theo trạng thái eKYC.
   *
   * @param status trạng thái eKYC.
   * @return một {@code Specification} để lọc theo trạng thái eKYC.
   */
  private Specification<User> hasEkycStatus(final Integer status) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("ekycStatus"), status);
  }
}
