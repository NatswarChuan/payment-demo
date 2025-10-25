package com.natswarchuan.payment.demo.entity;

import com.natswarchuan.payment.demo.constant.EntityConstant;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = EntityConstant.TABLE_ROLES)
public class Role {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false, length = 50)
  private String name;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = EntityConstant.COLUMN_PARENT_ID)
  private Role parent;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = EntityConstant.TABLE_ROLES_PERMISSIONS,
      joinColumns = @JoinColumn(name = EntityConstant.COLUMN_ROLE_ID),
      inverseJoinColumns = @JoinColumn(name = EntityConstant.COLUMN_PERMISSION_ID))
  private Set<Permission> permissions = new HashSet<>();

  public Role(String name) {
    this.name = name;
  }
}
