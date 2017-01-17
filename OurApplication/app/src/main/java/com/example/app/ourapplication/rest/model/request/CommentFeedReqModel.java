package com.example.app.ourapplication.rest.model.request;

import com.example.app.ourapplication.rest.model.Model;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by sarumugam on 15/01/17.
 */
public class CommentFeedReqModel extends Model {

    @JsonProperty("type")
    private String mType;
    @JsonProperty("postid")
    private String mPostId;
    @JsonProperty("latestdate")
    private String mLatestDate;

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        this.mType = type;
    }

    public String getPostId() {
        return mPostId;
    }

    public void setPostId(String postId) {
        this.mPostId = postId;
    }

    public String getLatestDate() {
        return mLatestDate;
    }

    public void setLatestDate(String latestDate) {
        this.mLatestDate = latestDate;
    }
}