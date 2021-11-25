package com.muhammad.warehouse_management.config.constant;

public class Authority {
    public static final String[] WORKER_USER_AUTHORITIES = { "user:read", "user:create" };
    public static final String[] ADMIN_AUTHORITIES = { "user:read", "user:create", "user:update", "user:delete" };
}
