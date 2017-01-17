package com.example.app.ourapplication.rest.model.request;

import com.example.app.ourapplication.rest.model.Model;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by sarumugam on 15/01/17.
 */
public class ProfileUpdateModel extends Model {

    @JsonProperty("userid")
    private String mUserId;
    @JsonProperty("columnname")
    private String mColumnName;
    @JsonProperty("columndata")
    private String mColumnData;

    public ProfileUpdateModel(String userId, String columnName, String columnData) {
        this.mUserId = userId;
        this.mColumnName = columnName;
        this.mColumnData = columnData;
    }

    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String mUserId) {
        this.mUserId = mUserId;
    }

    public String getColumnName() {
        return mColumnName;
    }

    public void setColumnName(String name) {
        this.mColumnName = name;
    }

    public String getColumnData() {
        return mColumnData;
    }

    public void setColumnData(String data) {
        this.mColumnData = data;
    }
}