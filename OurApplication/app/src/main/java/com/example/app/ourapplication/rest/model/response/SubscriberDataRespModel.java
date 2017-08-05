/*package com.example.app.ourapplication.rest.model.response;

import com.example.app.ourapplication.rest.model.Model;
import com.fasterxml.jackson.annotation.JsonProperty;


public class SubscriberDataRespModel extends Model {

    @JsonProperty("userid")
    private String mUserId;
    @JsonProperty("subscriberid")
    private String mSubscriberId;


    public SubscriberDataRespModel(String userId, String subscriberId) {
        this.mUserId = userId;
        this.mSubscriberId = subscriberId;
    }

    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String mUserId) {
        this.mUserId = mUserId;
    }

    public String getSubscriberId() {
        return mSubscriberId;
    }

    public void setSubscriberId(String subscriberId) {
        this.mSubscriberId = subscriberId;
    }

}*/


package com.example.app.ourapplication.rest.model.response;

import com.example.app.ourapplication.rest.model.Model;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

/**
 * Created by sarumugam on 15/01/17.
 */

public class SubscriberDataRespModel extends Model {


    private boolean mIsSuccess;
    private ArrayList<Subscriber> mData;

    @JsonCreator
    public SubscriberDataRespModel(@JsonProperty("success") Boolean success, @JsonProperty("data") ArrayList<Subscriber> data) {
        this.mIsSuccess = success;
        this.mData = data;

    }

    public boolean isSuccess() {
        return mIsSuccess;
    }

    public ArrayList<Subscriber> getData() {
        return mData;
    }
}
