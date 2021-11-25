package com.muhammad.warehouse_management.model;

import static com.muhammad.warehouse_management.config.constant.Authority.*;

public enum Role {
    ROLE_WORKER_USER(WORKER_USER_AUTHORITIES),

    ROLE_ADMIN(ADMIN_AUTHORITIES);


    private String[] authorities;

    Role(String... authorities) {
        this.authorities = authorities;
    }

    public String[] getAuthorities() {
        return authorities;
    }
}
