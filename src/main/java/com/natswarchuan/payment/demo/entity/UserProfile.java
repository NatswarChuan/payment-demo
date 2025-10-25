package com.natswarchuan.payment.demo.entity;

import com.natswarchuan.payment.demo.constant.EntityConstant;
import com.natswarchuan.payment.demo.constant.UserConstant;
import jakarta.persistence.*;
import java.time.Instant;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * Lớp Entity chứa thông tin hồ sơ chi tiết của người dùng.
 *
 * <p><b>Xóa mềm (Soft Delete):</b> Thực thể này sử dụng cơ chế xóa mềm, liên kết với việc xóa mềm
 * của thực thể {@link User}.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
    name = EntityConstant.TABLE_USER_PROFILES,
    indexes = {@Index(name = "idx_userprofile_nickname", columnList = "nickName")})
@SQLDelete(sql = "UPDATE user_profiles SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class UserProfile {

  @Id private Long id;

  @OneToOne(fetch = FetchType.LAZY, optional = false)
  @MapsId
  @JoinColumn(name = "id")
  private User user;

  @Column(unique = true, nullable = false, length = 100)
  private String nickName;

  @Column(nullable = false, length = 100)
  private String fullName;

  @Builder.Default
  @Column(nullable = false)
  @ColumnDefault("2")
  private int gender = UserConstant.Gender.OTHER;

  @Column(length = 2048)
  private String avatar;

  @Column(columnDefinition = "TEXT")
  private String bio;

  @CreationTimestamp
  @Column(updatable = false, nullable = false)
  private Instant createdAt;

  @UpdateTimestamp
  @Column(nullable = false)
  private Instant updatedAt;

  /** Thời điểm hồ sơ bị xóa mềm. `null` nếu chưa bị xóa. */
  private Instant deletedAt;
}
