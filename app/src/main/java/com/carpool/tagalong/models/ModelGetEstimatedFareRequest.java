package com.carpool.tagalong.models;

import com.google.gson.annotations.SerializedName;

public class ModelGetEstimatedFareRequest {

    @SerializedName("startLat")
    public double startLat;
    @SerializedName("startLong")
    public double startLong;
    @SerializedName("endLat")
    public double endLat;
    @SerializedName("endLong")
    public double endLong;

    public double getStartLat() {
        return startLat;
    }

    public void setStartLat(double startLat) {
        this.startLat = startLat;
    }

    public double getStartLong() {
        return startLong;
    }

    public void setStartLong(double startLong) {
        this.startLong = startLong;
    }

    public double getEndLat() {
        return endLat;
    }

    public void setEndLat(double endLat) {
        this.endLat = endLat;
    }

    public double getEndLong() {
        return endLong;
    }

    public void setEndLong(double endLong) {
        this.endLong = endLong;
    }

    @Override
    public String toString() {
        return "ModelGetEstimatedFareRequest{" +
                "startLat=" + startLat +
                ", startLong=" + startLong +
                ", endLat=" + endLat +
                ", endLong=" + endLong +
                '}';
    }
}
