package com.bankapp.Backend.model;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    CUSTOMER,
    EMPLOYEE;
    public String getAuthority() {
        // Boys I added "ROLE_" because spring boot security expects to have it in the implementation on UserDetial interface
        return "ROLE_"+ name();
    }
}