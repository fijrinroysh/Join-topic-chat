package com.example.app.ourapplication.rest.model.response;

import com.example.app.ourapplication.rest.model.Model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by sarumugam on 15/01/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserModel extends Model {

    @JsonProperty("username")
    private String mUserName;
    @JsonProperty("phonenumber")
    private String mPhoneNumber;
    @JsonProperty("profileimage")
    private String mProfPicUrl;

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String userName) {
        this.mUserName = userName;
    }

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.mPhoneNumber = phoneNumber;
    }

    public String getProfPicUrl() {
        return mProfPicUrl;
    }

    public void setProfPicUrl(String profPicUrl) {
        this.mProfPicUrl = profPicUrl;
    }
}