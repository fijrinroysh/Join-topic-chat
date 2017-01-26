package com.example.app.ourapplication.rest.model.request;

import com.example.app.ourapplication.rest.model.Model;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by sarumugam on 15/01/17.
 */
public class ProfileFeedReqModel extends Model {

    @JsonProperty("userid")
    private String mUserId;
    @JsonProperty("oldestdate")
    private String mOldestDate;


    public ProfileFeedReqModel(String userid, String oldestDate) {
        this.mUserId = userid;
        this.mOldestDate = oldestDate;

    }

    public String getType() {
        return mUserId;
    }

    public void setType(String userid) {
        this.mUserId = userid;
    }


    public String getOldestDate() {
        return mOldestDate;
    }

    public void setOldestDate(String oldestDate) {
        this.mOldestDate = oldestDate;
    }



}


