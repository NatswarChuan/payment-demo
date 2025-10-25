package com.natswarchuan.payment.demo.entity;

import com.natswarchuan.payment.demo.constant.EntityConstant;
import com.natswarchuan.payment.demo.constant.UserConstant;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Lớp Entity đại diện cho một người dùng trong hệ thống.
 *
 * <p>Lớp này triển khai giao diện {@link UserDetails} của Spring Security để tích hợp với cơ chế
 * xác thực.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = EntityConstant.TABLE_USERS)
@SQLDelete(sql = "UPDATE users SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class User implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, length = 100)
  private String email;

  @Column(unique = true, length = 20)
  private String phoneNumber;

  @Column(nullable = false)
  @ColumnDefault("0")
  @Builder.Default
  private Integer ekycStatus = UserConstant.EkycStatus.NOT_VERIFIED;

  private Instant createdAt;

  private Instant updatedAt;

  private Instant lastLoginAt;

  private Instant deletedAt;

  @OneToOne(
      mappedBy = "user",
      cascade = CascadeType.ALL,
      fetch = FetchType.LAZY,
      orphanRemoval = true)
  private UserProfile userProfile;

  @OneToOne(
      mappedBy = EntityConstant.WALLET_FIELD_USER,
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY)
  private Wallet wallet;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = EntityConstant.TABLE_USERS_ROLES,
      joinColumns = @JoinColumn(name = EntityConstant.COLUMN_USER_ID),
      inverseJoinColumns = @JoinColumn(name = EntityConstant.COLUMN_ROLE_ID))
  @Builder.Default
  private Set<Role> roles = new HashSet<>();

  /**
   * Trường này không được lưu vào cơ sở dữ liệu (transient) và được dùng để lưu trữ các quyền hạn
   * đã được tính toán.
   *
   * <p><b>Tối ưu hóa N+1 Query:</b> Thay vì tính toán quyền hạn một cách đệ quy tại đây (dễ gây ra
   * N+1 query), trường này sẽ được điền bởi một lớp dịch vụ như {@code UnifiedUserDetailsService},
   * nơi logic lấy dữ liệu được tối ưu hóa bằng các truy vấn hiệu quả hơn.
   */
  @Transient private transient Set<GrantedAuthority> authorities;

  /**
   * Trả về tập hợp các quyền hạn (authorities) đã được cấp cho người dùng.
   *
   * @return một tập hợp các {@code GrantedAuthority}.
   */
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return this.authorities;
  }

  @Override
  public String getPassword() {
    return null;
  }

  @Override
  public String getUsername() {
    return this.email != null ? this.email : this.phoneNumber;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return this.deletedAt == null;
  }
}

