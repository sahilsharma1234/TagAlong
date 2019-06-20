package com.carpool.tagalong.models;

import com.google.gson.annotations.SerializedName;

public class ModelStartRideRequest {


    @SerializedName("rideId")
    public String rideId;

    public String getRideId() {
        return rideId;
    }

    public void setRideId(String rideId) {
        this.rideId = rideId;
    }
}
