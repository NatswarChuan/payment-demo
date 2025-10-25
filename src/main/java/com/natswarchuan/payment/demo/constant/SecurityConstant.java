package com.natswarchuan.payment.demo.constant;

public final class SecurityConstant {
  private SecurityConstant() {}

  // JWT
  public static final String JWT_CLAIM_USER_ID = "userId";
  public static final String JWT_CLAIM_ROLES = "roles";
  public static final String JWT_BEARER_PREFIX = "Bearer ";
  public static final int JWT_BEARER_PREFIX_LENGTH = 7;

  // HTTP Headers
  public static final String HEADER_AUTHORIZATION = "Authorization";
  public static final String HEADER_USER_AGENT = "User-Agent";
  public static final String HEADER_X_FORWARDED_FOR = "X-Forwarded-For";

  // Spring Security
  public static final String ANONYMOUS_USER = "anonymousUser";
  public static final String ROLE_PREFIX = "ROLE_";

  // Permissions
  public static final String PERMISSION_USER_READ = "user:read";
  public static final String PERMISSION_USER_WRITE = "user:write";
  public static final String PERMISSION_TRANSACTION_READ = "transaction:read";
  public static final String PERMISSION_TRANSACTION_CREATE = "transaction:create";
  public static final String PERMISSION_ADMIN_DASHBOARD_READ = "admin:dashboard:read";
  public static final String PERMISSION_ROLE_MANAGE = "role:manage";
  public static final String PERMISSION_FINANCE_TRANSACT = "finance:transact";

  // Roles
  public static final String ROLE_SUPER_ADMIN = "SUPER_ADMIN";
  public static final String ROLE_ADMIN = "ADMIN";
  public static final String ROLE_USER = "USER";
  public static final String ROLE_VERIFIED_USER = "VERIFIED_USER";
}
