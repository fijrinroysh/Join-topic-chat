package com.example.app.ourapplication.rest.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by sarumugam on 15/01/17.
 */
public class SignUpReqModel extends SignInReqModel {

    @JsonProperty("userid")
    private String mUserId;
    @JsonProperty("name")
    private String mName;
    @JsonProperty("password")
    private String mPassWord;

    public void setName(String name) {
        this.mName = name;
    }

    public String getName() {
        return mName;
    }

    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String userId) {
        this.mUserId = userId;
    }

    public String getPassWord() {
        return mPassWord;
    }

    public void setPassWord(String passWord) {
        this.mPassWord = passWord;
    }
}