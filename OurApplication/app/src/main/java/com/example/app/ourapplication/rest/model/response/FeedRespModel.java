package com.example.app.ourapplication.rest.model.response;

import com.example.app.ourapplication.rest.model.Model;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

/**
 * Created by sarumugam on 15/01/17.
 */

public class FeedRespModel extends Model {


    private boolean mIsSuccess;
    private ArrayList<Person> mData;

    @JsonCreator
    public FeedRespModel(@JsonProperty("success") Boolean success,@JsonProperty("data") ArrayList<Person> data ) {
        this.mIsSuccess = success;
        this.mData = data;

    }

    public boolean isSuccess() {
        return mIsSuccess;
    }

    public ArrayList<Person> getData() {
        return mData;
    }
}