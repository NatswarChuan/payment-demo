package com.natswarchuan.payment.demo.constant;

public final class EntityConstant {
  private EntityConstant() {}

  // Table Names
  public static final String TABLE_USERS = "users";
  public static final String TABLE_USER_PROFILES = "user_profiles";
  public static final String TABLE_WALLETS = "wallets";
  public static final String TABLE_TRANSACTIONS = "transactions";
  public static final String TABLE_PAYMENT_METHODS = "payment_methods";
  public static final String TABLE_ROLES = "roles";
  public static final String TABLE_PERMISSIONS = "permissions";
  public static final String TABLE_USERS_ROLES = "users_roles";
  public static final String TABLE_ROLES_PERMISSIONS = "roles_permissions";

  // General Column Names
  public static final String FIELD_ID = "id";
  public static final String FIELD_CREATED_AT = "createdAt";
  public static final String FIELD_NAME = "name";

  // Foreign Key Column Names
  public static final String COLUMN_USER_ID = "user_id";
  public static final String COLUMN_WALLET_ID = "wallet_id";
  public static final String COLUMN_PAYMENT_METHOD_ID = "payment_method_id";
  public static final String COLUMN_RELATED_TRANSACTION_ID = "related_transaction_id";
  public static final String COLUMN_ROLE_ID = "role_id";
  public static final String COLUMN_PERMISSION_ID = "permission_id";
  public static final String COLUMN_PARENT_ID = "parent_id";

  // Index Names
  public static final String INDEX_USER_NICKNAME = "idx_user_nickname";
  public static final String INDEX_TXN_WALLET_CREATED = "idx_transaction_wallet_created";
  public static final String INDEX_TXN_STATUS_TYPE = "idx_transaction_status_type";
  public static final String INDEX_PM_WALLET = "idx_paymentmethod_wallet";
  public static final String INDEX_TXN_PROVIDER_ID = "idx_txn_provider_id";

  // Wallet Entity
  public static final String WALLET_FIELD_SELF = "wallet";
  public static final String WALLET_FIELD_USER = "user";
  public static final String WALLET_FIELD_STATUS = "status";
  public static final String WALLET_FIELD_CURRENCY = "currency";
  public static final String WALLET_FIELD_BALANCE = "balance";
  public static final String WALLET_FIELD_PAYMENT_METHODS = "paymentMethods";
  public static final String WALLET_FIELD_TRANSACTIONS = "transactions";

  // User Entity
  public static final String USER_FIELD_NICKNAME = "nickName";
  public static final String USER_FIELD_FULLNAME = "fullName";
  public static final String USER_FIELD_EMAIL = "email";
  public static final String USER_FIELD_PHONE_NUMBER = "phoneNumber";
  public static final String USER_FIELD_ROLE = "role";
  public static final String USER_FIELD_WALLET = "wallet";
  public static final String USER_FIELD_ROLES = "roles";

  // PaymentMethod Entity
  public static final String PM_FIELD_WALLET = "wallet";
  public static final String PM_FIELD_TYPE = "type";
  public static final String PM_FIELD_PROVIDER = "provider";
  public static final String PM_FIELD_IS_DEFAULT = "isDefault";

  // Transaction Entity
  public static final String TXN_FIELD_WALLET = "wallet";
  public static final String TXN_FIELD_TYPE = "type";
  public static final String TXN_FIELD_STATUS = "status";
  public static final String TXN_FIELD_AMOUNT = "amount";
  public static final String TXN_FIELD_CREATED_AT = "createdAt";

  // Role Entity
  public static final String ROLE_FIELD_PARENT = "parent";
  public static final String ROLE_FIELD_PERMISSIONS = "permissions";
}
