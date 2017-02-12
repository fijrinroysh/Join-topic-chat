package com.example.app.ourapplication.rest.model.response;

import com.example.app.ourapplication.rest.model.Model;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by sarumugam on 15/01/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SignInRespModel extends Model implements Serializable {


    private boolean mIsSuccess;
    private String mToken;
    private String mMessage;



    @JsonCreator
    public SignInRespModel(@JsonProperty("success") Boolean success,@JsonProperty("token") String token,@JsonProperty("message") String message) {
        this.mIsSuccess = success;
        this.mToken = token;
        this.mMessage = message;


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