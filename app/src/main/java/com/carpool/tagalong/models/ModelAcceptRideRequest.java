package com.carpool.tagalong.models;

import com.google.gson.annotations.SerializedName;

public class ModelAcceptRideRequest {


    @SerializedName("requestId")
    public String requestId;
    @SerializedName("rideId")
    public String rideId;
    @SerializedName("isAccepted")
    public boolean isAccepted;

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

    public boolean isAccepted() {
        return isAccepted;
    }

    public void setAccepted(boolean accepted) {
        isAccepted = accepted;
    }

    @Override
    public String toString() {
        return "ModelAcceptRideRequest{" +
                "requestId='" + requestId + '\'' +
                ", rideId='" + rideId + '\'' +
                ", isAccepted=" + isAccepted +
                '}';
    }
}
