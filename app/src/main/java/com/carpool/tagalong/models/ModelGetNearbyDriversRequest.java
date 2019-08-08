package com.carpool.tagalong.models;

import com.google.gson.annotations.SerializedName;

public class ModelGetNearbyDriversRequest {

    @SerializedName("currentLat")
    public double currentLat;
    @SerializedName("currentLong")
    public double currentLong;

    public double getCurrentLat() {
        return currentLat;
    }

    public void setCurrentLat(double currentLat) {
        this.currentLat = currentLat;
    }

    public double getCurrentLong() {
        return currentLong;
    }

    public void setCurrentLong(double currentLong) {
        this.currentLong = currentLong;
    }

    @Override
    public String toString() {
        return "ModelGetNearbyDriversRequest{" +
                "currentLat=" + currentLat +
                ", currentLong=" + currentLong +
                '}';
    }
}
