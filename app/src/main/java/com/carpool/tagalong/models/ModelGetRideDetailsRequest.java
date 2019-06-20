package com.carpool.tagalong.models;

import com.google.gson.annotations.SerializedName;

public class ModelGetRideDetailsRequest {

    @SerializedName("rideId")
    public String rideId;

    public String getRideId() {
        return rideId;
    }

    public void setRideId(String rideId) {
        this.rideId = rideId;
    }

    @Override
    public String toString() {
        return "ModelGetRideDetailsRequest{" +
                "rideId='" + rideId + '\'' +
                '}';
    }
}
