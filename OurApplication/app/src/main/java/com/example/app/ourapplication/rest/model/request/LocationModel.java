package com.example.app.ourapplication.rest.model.request;

import android.location.Location;

import com.example.app.ourapplication.rest.model.Model;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by sarumugam on 17/01/17.
 */
public class LocationModel extends Model implements Serializable{
    @JsonProperty("longitude")
    private double longitude;
    @JsonProperty("latitude")
    private double latitude;
    @JsonProperty("radius")
    private double radius;

    public LocationModel(){}

    public LocationModel(Location location) {
        this.longitude = location.getLongitude();
        this.latitude = location.getLatitude();
    }

    public LocationModel(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }
}