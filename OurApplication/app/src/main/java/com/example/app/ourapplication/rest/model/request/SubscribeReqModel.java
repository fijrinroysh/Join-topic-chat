package com.example.app.ourapplication.rest.model.request;

import com.example.app.ourapplication.rest.model.Model;
import com.example.app.ourapplication.util.Helper;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Sudha on 09-07-2017.
 */
public class SubscribeReqModel extends Model {


    @JsonProperty("postid")
    private String mPostId;
    @JsonProperty("userid")
    private String mUserId;
    @JsonProperty("subscriptionflag")
    private String mSubscriptionFlag;

    public SubscribeReqModel(String postid, String userid, String subscriptionflag) {
        this.mPostId = postid;
        this.mUserId = userid;
        this.mSubscriptionFlag=subscriptionflag;
           }

    public String getPostId() {
        return mPostId;
    }

    public void setPostId(String postId) {
        this.mPostId = postId;
    }

    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String mUserId) {
        this.mUserId = mUserId;
    }


    public String getSubscriptionFlag() {
        return mSubscriptionFlag;
    }

    public void setSubscriptionFlag(String subscriptionflag) {
        this.mSubscriptionFlag = subscriptionflag;
    }
}