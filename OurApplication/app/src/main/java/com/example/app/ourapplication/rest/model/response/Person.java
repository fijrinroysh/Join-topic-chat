package com.example.app.ourapplication.rest.model.response;

import com.example.app.ourapplication.rest.model.Model;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by ROYSH on 6/23/2016.
 */
public class Person extends Model implements Serializable {

    @JsonProperty("type")
    private String mType;
    @JsonProperty("postid")
    private String mPostId;
    @JsonProperty("userid")
    private String mUserId;
    @JsonProperty("name")
    private String mSenderName;
    @JsonProperty("message")
    private String mMessage;
    @JsonProperty("profileimage")
    private String mPhotoId;
    @JsonProperty("image")
    private String mPhotoMsg;
    @JsonProperty("time")
    private String mTimeMsg;

    @JsonCreator
    public Person(@JsonProperty("type") String type,@JsonProperty("postid") String id,@JsonProperty("userid") String userid,@JsonProperty("name") String name,@JsonProperty("message") String msg,@JsonProperty("profileimage") String photoId,@JsonProperty("image") String photoMsg,@JsonProperty("time") String time) {
        this.mType = type;
        this.mPostId = id;
        this.mUserId = userid;
        this.mSenderName = name;
        this.mMessage = msg;
        this.mPhotoId = photoId;
        this.mPhotoMsg = photoMsg;
        this.mTimeMsg = time;
    }



    public String getType() {
        return mType;
    }

    public void setType(String type) {this.mType = type;}

    public String getPostId() {
        return mPostId;
    }

    public void setPostId(String postId) {
        this.mPostId = postId;
    }

    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String userId) {
        this.mUserId = userId;
    }

    public String getSenderName() {
        return mSenderName;
    }

    public void setSenderName(String name) {
        this.mSenderName = name;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        this.mMessage = message;
    }

    public String getPhotoId() {
        return mPhotoId;
    }

    public void setPhotoId(String photoId) {
        this.mPhotoId = photoId;
    }

    public String getPhotoMsg() {
        return mPhotoMsg;
    }

    public void setPhotoMsg(String photoMsg) {
        this.mPhotoMsg = photoMsg;
    }

    public String getTimeMsg() {
        return mTimeMsg;
    }

    public void setTimeMsg(String timeMsg) {
        this.mTimeMsg = timeMsg;
    }
}