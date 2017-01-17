package com.example.app.ourapplication.rest.model.response;

import com.example.app.ourapplication.rest.model.Model;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

/**
 * Created by sarumugam on 15/01/17.
 */
public class FeedRespModel extends Model {

    @JsonProperty("success")
    private boolean mIsSuccess;
    @JsonProperty("data")
    private ArrayList<Person> mData;

    public boolean isSuccess() {
        return mIsSuccess;
    }

    public ArrayList<Person> getData() {
        return mData;
    }
}