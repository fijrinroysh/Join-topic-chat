package com.example.app.ourapplication.rest.model.response;

import com.example.app.ourapplication.Keys;
import com.example.app.ourapplication.rest.model.Model;
import com.example.app.ourapplication.rest.model.request.CommentFeedReqModel;
import com.example.app.ourapplication.rest.model.request.LocationModel;
import com.example.app.ourapplication.util.Helper;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sarumugam on 17/01/17.
 */
public class CompleteFeedModel extends Model {

    @JsonProperty("type")
    private String mType;
    @JsonProperty("token")
    private String mToken;
    @JsonProperty("message")
    private String mMessage;
    @JsonProperty("longitude")
    private double mLongitude;
    @JsonProperty("latitude")
    private double mLatitude;
    @JsonProperty("image")
    private String mPhotoMsg;
    @JsonProperty("time")
    private String mTimeMsg;

    public CompleteFeedModel(String type, String token, String message, LocationModel locationModel, String bitmap) {
        this.mType = type;
        this.mToken = token;
        this.mMessage = message;
        this.mLongitude = locationModel.getLongitude();
        this.mLatitude = locationModel.getLatitude();
        this.mPhotoMsg = bitmap;
        this.mTimeMsg = Helper.getCurrentTimeStamp();
    }

    public String getType() {
        return mType;
    }

    public String getToken() {
        return mToken;
    }

    public String getMessage() {
        return mMessage;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public String getPhotoMsg() {
        return mPhotoMsg;
    }

    public String getTimeMsg() {
        return mTimeMsg;
    }
}