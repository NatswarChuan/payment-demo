package com.natswarchuan.payment.demo.config;

import com.natswarchuan.payment.demo.constant.LogConstant;
import com.natswarchuan.payment.demo.constant.SecurityConstant;
import com.natswarchuan.payment.demo.entity.Permission;
import com.natswarchuan.payment.demo.entity.Role;
import com.natswarchuan.payment.demo.repository.PermissionRepository;
import com.natswarchuan.payment.demo.repository.RoleRepository;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

  private final RoleRepository roleRepository;
  private final PermissionRepository permissionRepository;

  @Override
  @Transactional
  public void run(String... args) throws Exception {
    log.info(LogConstant.DATA_INIT_START);

    Permission readUser = createPermissionIfNotFound(SecurityConstant.PERMISSION_USER_READ);
    Permission writeUser = createPermissionIfNotFound(SecurityConstant.PERMISSION_USER_WRITE);
    Permission readTransaction =
        createPermissionIfNotFound(SecurityConstant.PERMISSION_TRANSACTION_READ);
    Permission createTransaction =
        createPermissionIfNotFound(SecurityConstant.PERMISSION_TRANSACTION_CREATE);
    Permission readAdminDashboard =
        createPermissionIfNotFound(SecurityConstant.PERMISSION_ADMIN_DASHBOARD_READ);
    Permission manageRoles = createPermissionIfNotFound(SecurityConstant.PERMISSION_ROLE_MANAGE);
    Permission transactFinance =
        createPermissionIfNotFound(SecurityConstant.PERMISSION_FINANCE_TRANSACT);

    
    Role superAdminRole =
        createRoleIfNotFound(
            SecurityConstant.ROLE_SUPER_ADMIN, new HashSet<>(Arrays.asList(manageRoles)));

    Role adminRole =
        createRoleIfNotFound(
            SecurityConstant.ROLE_ADMIN,
            new HashSet<>(Arrays.asList(writeUser, readAdminDashboard)));
    adminRole.setParent(superAdminRole);
    roleRepository.save(adminRole);

    Role userRole =
        createRoleIfNotFound(
            SecurityConstant.ROLE_USER,
            new HashSet<>(Arrays.asList(readUser, readTransaction, createTransaction)));
    userRole.setParent(adminRole);
    roleRepository.save(userRole);

    
    Role verifiedUserRole =
        createRoleIfNotFound(
            SecurityConstant.ROLE_VERIFIED_USER, new HashSet<>(Arrays.asList(transactFinance)));
    verifiedUserRole.setParent(userRole);
    roleRepository.save(verifiedUserRole);

    log.info(LogConstant.DATA_INIT_FINISH);
  }

  private Permission createPermissionIfNotFound(String name) {
    return permissionRepository
        .findByName(name)
        .orElseGet(
            () -> {
              log.info(LogConstant.CREATING_PERMISSION, name);
              return permissionRepository.save(new Permission(name));
            });
  }

  private Role createRoleIfNotFound(String name, Set<Permission> permissions) {
    return roleRepository
        .findByName(name)
        .orElseGet(
            () -> {
              log.info(LogConstant.CREATING_ROLE, name);
              Role role = new Role(name);
              role.setPermissions(permissions);
              roleRepository.save(role);
              return role;
            });
  }
}
