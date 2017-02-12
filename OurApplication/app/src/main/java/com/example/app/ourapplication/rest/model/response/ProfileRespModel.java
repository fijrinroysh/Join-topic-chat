package com.example.app.ourapplication.rest.model.response;

import com.example.app.ourapplication.rest.model.Model;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by sarumugam on 17/01/17.
 */
public class ProfileRespModel extends Model {


    private boolean mIsSuccess;


    @JsonCreator
    public ProfileRespModel(@JsonProperty("success") Boolean success) {
        this.mIsSuccess = success;
    }

    public boolean isSuccess() {
        return mIsSuccess;
    }

    public void setIsSuccess(boolean isSuccess) {
        this.mIsSuccess = isSuccess;
    }

}
