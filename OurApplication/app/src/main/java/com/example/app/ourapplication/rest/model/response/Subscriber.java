package com.example.app.ourapplication.rest.model.response;

import com.example.app.ourapplication.rest.model.Model;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
/**
 * Created by Sudha on 22-07-2017.
 */
public class Subscriber extends Model implements Serializable {

    private String mUserId;
    private String mSubscriberId;

    @JsonCreator
    public Subscriber(@JsonProperty("userid") String userid,@JsonProperty("subscriberid") String subscriberid){
        this.mUserId = userid;
        this.mSubscriberId = subscriberid;
    }



    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String userId) {
        this.mUserId = userId;
    }

    public String getSubscriberId() {
        return mSubscriberId;
    }

    public void setSubscriberId(String subscriberid) {
        this.mSubscriberId = subscriberid;
    }


}