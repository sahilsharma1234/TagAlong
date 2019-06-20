package com.carpool.tagalong.models;

import com.google.gson.annotations.SerializedName;

public class ModelCancelOwnRideRequest {


    @SerializedName("requestId")
    public String requestId;

    @SerializedName("rideId")
    public String rideId;

    @SerializedName("cancelReason")
    public String cancelReason;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public String getRideId() {
        return rideId;
    }

    public void setRideId(String rideId) {
        this.rideId = rideId;
    }

    @Override
    public String toString() {
        return "ModelCancelOwnRideRequest{" +
                "requestId='" + requestId + '\'' +
                ", rideId='" + rideId + '\'' +
                ", cancelReason='" + cancelReason + '\'' +
                '}';
    }
}
