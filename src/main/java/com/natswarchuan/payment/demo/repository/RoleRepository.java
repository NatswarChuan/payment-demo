package com.natswarchuan.payment.demo.repository;

import com.natswarchuan.payment.demo.entity.Role;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Lớp repository để truy cập dữ liệu của thực thể {@link Role}.
 *
 * <p>Cung cấp các phương thức CRUD cơ bản và các truy vấn tùy chỉnh, bao gồm cả truy vấn đệ quy
 * hiệu quả để lấy toàn bộ vai trò và quyền hạn của người dùng trong một lần gọi.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

  /**
   * Interface Projection để ánh xạ kết quả từ truy vấn native một cách tường minh.
   *
   * <p>Việc sử dụng projection giúp tăng cường type-safety so với việc dùng {@code Object[]}.
   */
  interface AuthorityProjection {
    String getAuthority();

    String getType();
  }

  /**
   * Tìm một vai trò dựa trên tên của nó.
   *
   * @param name Tên của vai trò cần tìm.
   * @return một {@code Optional<Role>} chứa vai trò nếu tìm thấy.
   */
  Optional<Role> findByName(String name);

  /**
   * Tìm tất cả các quyền hạn (vai trò và quyền) trong hệ thống phân cấp cho một người dùng cụ thể
   * bằng một truy vấn đệ quy duy nhất.
   *
   * <p><b>Tối ưu hóa hiệu năng:</b> Truy vấn này sử dụng Common Table Expression (CTE) đệ quy
   * ({@code WITH RECURSIVE}) để duyệt qua cây phân cấp vai trò, từ vai trò được gán trực tiếp cho
   * người dùng lên đến các vai trò cha. Sau đó, nó sử dụng {@code UNION ALL} để kết hợp kết quả tên
   * vai trò và tên quyền, giúp lấy tất cả thông tin cần thiết trong một lần truy vấn duy nhất,
   * tránh được vấn đề N+1 query.
   *
   * @param userId ID của người dùng.
   * @return Một danh sách các đối tượng {@link AuthorityProjection}, mỗi đối tượng chứa tên và loại
   * quyền hạn.
   */
  @Query(
      value =
          """
              WITH RECURSIVE role_hierarchy (id) AS (
                SELECT r.id FROM roles r JOIN users_roles ur ON r.id = ur.role_id WHERE ur.user_id = :userId
                UNION DISTINCT
                SELECT r.parent_id FROM roles r JOIN role_hierarchy rh ON r.id = rh.id WHERE r.parent_id IS NOT NULL
              )
              SELECT r.name as authority, 'ROLE' as type FROM roles r WHERE r.id IN (SELECT id FROM role_hierarchy)
              UNION ALL
              SELECT DISTINCT p.name as authority, 'PERMISSION' as type FROM permissions p JOIN roles_permissions rp ON p.id = rp.permission_id WHERE rp.role_id IN (SELECT id FROM role_hierarchy)
          """,
      nativeQuery = true)
  List<AuthorityProjection> findAuthoritiesInHierarchyByUserId(Long userId);
}

