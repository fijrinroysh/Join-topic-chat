package com.example.app.ourapplication.rest.model.response;

import com.example.app.ourapplication.rest.model.Model;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by sarumugam on 17/01/17.
 */
public class ProfileRespModel extends Model {

    @JsonProperty("success")
    private boolean mIsSuccess;

    public boolean isSuccess() {
        return mIsSuccess;
    }

    public void setIsSuccess(boolean isSuccess) {
        this.mIsSuccess = isSuccess;
    }

}
