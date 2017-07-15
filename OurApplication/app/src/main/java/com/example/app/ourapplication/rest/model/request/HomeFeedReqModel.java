package com.example.app.ourapplication.rest.model.request;

import com.example.app.ourapplication.rest.model.Model;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by sarumugam on 15/01/17.
 */
public class HomeFeedReqModel extends Model {

    @JsonProperty("type")
    private String mType;
    @JsonProperty("range")
    private String mRange;
    @JsonProperty("longitude")
    private double mLongitude;
    @JsonProperty("latitude")
    private double mLatitude;
    @JsonProperty("latestdate")
    private String mLatestDate;
    @JsonProperty("userid")
    private String mUserid;

    public HomeFeedReqModel(String type, String range, double longitude, double latitude, String latestDate,String userid) {
        this.mType = type;
        this.mRange = range;
        this.mLongitude = longitude;
        this.mLatitude = latitude;
        this.mLatestDate = latestDate;
        this.mUserid = userid;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        this.mType = type;
    }

    public String getRange() {
        return mRange;
    }

    public void setRange(String range) {
        this.mRange = range;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double longitude) {
        this.mLongitude = longitude;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double latitude) {
        this.mLatitude = latitude;
    }

    public String getLatestDate() {
        return mLatestDate;
    }

    public void setLatestDate(String latestDate) {
        this.mLatestDate = latestDate;
    }

    public String getUserId() {
        return mUserid;
    }

    public void setUserId(String userid) {
        this.mUserid = userid;
    }

}