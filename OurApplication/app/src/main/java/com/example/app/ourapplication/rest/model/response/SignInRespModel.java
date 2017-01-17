package com.example.app.ourapplication.rest.model.response;

import com.example.app.ourapplication.rest.model.Model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by sarumugam on 15/01/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SignInRespModel extends Model implements Serializable {

    @JsonProperty("success")
    private boolean mIsSuccess;
    @JsonProperty("users")
    private String mToken;
    @JsonProperty("message")
    private String mMessage;
    @JsonProperty("users")
    private ArrayList<UserModel> mUsers;

    public ArrayList<UserModel> getUsers() {
        return mUsers;
    }

    public void setUsers(ArrayList<UserModel> users) {
        this.mUsers = users;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        this.mMessage = message;
    }

    public boolean isSuccess() {
        return mIsSuccess;
    }

    public void setIsSuccess(boolean isSuccess) {
        this.mIsSuccess = isSuccess;
    }

    public String getToken() {
        return mToken;
    }

    public void setToken(String token) {
        this.mToken = token;
    }
}