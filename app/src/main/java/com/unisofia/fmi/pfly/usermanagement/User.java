package com.unisofia.fmi.pfly.usermanagement;

import com.unisofia.fmi.pfly.api.model.Account;

/**
 * Created by martin.ivanov on 2016-10-23.
 */

public class User extends Account {
    private String password;
    private String token;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
