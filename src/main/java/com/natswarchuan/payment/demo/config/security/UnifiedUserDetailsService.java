package com.natswarchuan.payment.demo.config.security;

import com.natswarchuan.payment.demo.constant.MessageConstant;
import com.natswarchuan.payment.demo.constant.SecurityConstant;
import com.natswarchuan.payment.demo.entity.User;
import com.natswarchuan.payment.demo.interfaces.services.IUserService;
import com.natswarchuan.payment.demo.repository.RoleRepository;
import com.natswarchuan.payment.demo.repository.RoleRepository.AuthorityProjection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Lớp dịch vụ hợp nhất, chịu trách nhiệm tải thông tin chi tiết của người dùng cho Spring
 * Security.
 *
 * <p>Lớp này được đánh dấu là {@code @Primary} để được ưu tiên sử dụng thay vì các triển khai {@code
 * UserDetailsService} khác. Nó tối ưu hóa việc tải dữ liệu bằng cách lấy tất cả các vai trò và
 * quyền hạn của người dùng trong một truy vấn duy nhất.
 */
@Primary
@Service
@RequiredArgsConstructor
public class UnifiedUserDetailsService implements UserDetailsService {

  private final IUserService userService;
  private final RoleRepository roleRepository;

  /**
   * Tải thông tin người dùng (bao gồm cả các quyền hạn) dựa trên định danh (email hoặc số điện
   * thoại).
   *
   * <p>Phương thức này được tối ưu hóa để thực hiện một truy vấn duy nhất tới cơ sở dữ liệu để lấy
   * toàn bộ các vai trò và quyền trong hệ thống phân cấp của người dùng, giúp cải thiện hiệu năng
   * đáng kể.
   *
   * @param username Định danh của người dùng (email hoặc số điện thoại).
   * @return một đối tượng {@code UserDetails} chứa đầy đủ thông tin xác thực.
   * @throws UsernameNotFoundException nếu không tìm thấy người dùng.
   */
  @Override
  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user =
        userService
            .findByIdentifier(username)
            .orElseThrow(
                () ->
                    new UsernameNotFoundException(
                        String.format(MessageConstant.User.NOT_FOUND_BY_IDENTIFIER, username)));

    Set<GrantedAuthority> authorities = new HashSet<>();
    List<AuthorityProjection> results =
        roleRepository.findAuthoritiesInHierarchyByUserId(user.getId());

    for (AuthorityProjection result : results) {
      String authority = result.getAuthority();
      String type = result.getType();
      if ("ROLE".equals(type)) {
        authorities.add(new SimpleGrantedAuthority(SecurityConstant.ROLE_PREFIX + authority));
      } else if ("PERMISSION".equals(type)) {
        authorities.add(new SimpleGrantedAuthority(authority));
      }
    }

    user.setAuthorities(authorities);

    return user;
  }
}

