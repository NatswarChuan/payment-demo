package com.natswarchuan.payment.demo.entity;

import com.fasterxml.uuid.Generators;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import java.time.Instant;
import java.util.Random;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * Lớp cơ sở trừu tượng cho tất cả các thực thể (entity) trong hệ thống.
 *
 * <p>Cung cấp các trường chung như ID, thời gian tạo, thời gian cập nhật và một trường {@code
 * deletedAt} để hỗ trợ cơ chế xóa mềm (soft delete).
 */
@Getter
@Setter
@MappedSuperclass
public abstract class BaseEntity {

  @Id
  @Column(updatable = false, nullable = false, columnDefinition = "BINARY(16)")
  private UUID id;

  @CreationTimestamp
  @Column(updatable = false, nullable = false)
  private Instant createdAt;

  @UpdateTimestamp
  @Column(nullable = false)
  private Instant updatedAt;

  /**
   * Dấu thời gian ghi nhận thời điểm một thực thể bị xóa mềm.
   *
   * <p>Giá trị `null` cho biết thực thể chưa bị xóa. Khi thực thể bị xóa, trường này sẽ được cập
   * nhật với thời gian hiện tại.
   */
  @Column private Instant deletedAt;

  /**
   * Hàm callback được gọi trước khi một thực thể được lưu lần đầu tiên.
   *
   * <p>Tự động tạo một UUID v7 (time-ordered) nếu ID chưa được cung cấp.
   */
  @PrePersist
  protected void onCreate() {
    if (this.id == null) {
      this.id = Generators.timeBasedEpochGenerator(new Random()).generate();
    }
  }
}
