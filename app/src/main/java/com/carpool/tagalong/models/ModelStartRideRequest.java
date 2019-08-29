package com.carpool.tagalong.models;

import com.google.gson.annotations.SerializedName;

public class ModelStartRideRequest {


    @SerializedName("rideId")
    public String rideId;

    @SerializedName("startedDate")
    public String startedDate;

    public String getRideId() {
        return rideId;
    }

    public void setRideId(String rideId) {
        this.rideId = rideId;
    }

    public String getStartedDate() {
        return startedDate;
    }

    public void setStartedDate(String startedDate) {
        this.startedDate = startedDate;
    }

    @Override
    public String toString() {
        return "ModelStartRideRequest{" +
                "rideId='" + rideId + '\'' +
                ", startedDate='" + startedDate + '\'' +
                '}';
    }
}
