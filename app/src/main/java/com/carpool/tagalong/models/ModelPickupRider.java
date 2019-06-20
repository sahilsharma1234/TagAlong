package com.carpool.tagalong.models;

import com.google.gson.annotations.SerializedName;

public class ModelPickupRider {


    @SerializedName("requestId")
    public String requestId;
    @SerializedName("rideId")
    public String rideId;
    @SerializedName("pickupLat")
    public double pickupLat;
    @SerializedName("pickupLong")
    public double pickupLong;

    @SerializedName("dropLat")
    public double dropLat;
    @SerializedName("dropLong")
    public double dropLong;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getRideId() {
        return rideId;
    }

    public void setRideId(String rideId) {
        this.rideId = rideId;
    }

    public double getPickupLat() {
        return pickupLat;
    }

    public void setPickupLat(double pickupLat) {
        this.pickupLat = pickupLat;
    }

    public double getPickupLong() {
        return pickupLong;
    }

    public void setPickupLong(double pickupLong) {
        this.pickupLong = pickupLong;
    }

    public double getDropLat() {
        return dropLat;
    }

    public void setDropLat(double dropLat) {
        this.dropLat = dropLat;
    }

    public double getDropLong() {
        return dropLong;
    }

    public void setDropLong(double dropLong) {
        this.dropLong = dropLong;
    }

    @Override
    public String toString() {
        return "ModelPickupRider{" +
                "requestId='" + requestId + '\'' +
                ", rideId='" + rideId + '\'' +
                ", pickupLat=" + pickupLat +
                ", pickupLong=" + pickupLong +
                ", dropLat=" + dropLat +
                ", dropLong=" + dropLong +
                '}';
    }
}
