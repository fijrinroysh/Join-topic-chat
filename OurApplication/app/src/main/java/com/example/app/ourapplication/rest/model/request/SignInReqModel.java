package com.example.app.ourapplication.rest.model.request;

import com.example.app.ourapplication.rest.model.Model;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by sarumugam on 15/01/17.
 */
public class SignInReqModel extends Model implements Serializable {

    @JsonProperty("userid")
    private String mUserId;
    @JsonProperty("password")
    private String mPassword;

    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String mUserId) {
        this.mUserId = mUserId;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String mPassword) {
        this.mPassword = mPassword;
    }
}