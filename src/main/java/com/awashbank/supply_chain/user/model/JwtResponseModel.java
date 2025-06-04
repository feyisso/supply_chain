package com.awashbank.supply_chain.user.model;

import java.io.Serializable;

public class JwtResponseModel implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private final String token;
    private final boolean status;

    public JwtResponseModel(boolean status, String token) {

        this.status = status;
        this.token = token;
    }

    public boolean getStatus() {
        return status;
    }

    public String getToken() {
        return token;
    }
}
