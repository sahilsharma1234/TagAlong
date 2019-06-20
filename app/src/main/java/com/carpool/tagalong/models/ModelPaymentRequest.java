package com.carpool.tagalong.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ModelPaymentRequest implements Serializable {


    @SerializedName("driverId")
    public String driverId;
    @SerializedName("rideId")
    public String rideId;
    @SerializedName("stripeToken")
    public String stripeToken;
    @SerializedName("amount")
    public double amount;

    @SerializedName("requestId")
    public String requestId;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getRideId() {
        return rideId;
    }

    public void setRideId(String rideId) {
        this.rideId = rideId;
    }

    public String getStripeToken() {
        return stripeToken;
    }

    public void setStripeToken(String stripeToken) {
        this.stripeToken = stripeToken;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "ModelPaymentRequest{" +
                "driverId='" + driverId + '\'' +
                ", rideId='" + rideId + '\'' +
                ", stripeToken='" + stripeToken + '\'' +
                ", amount=" + amount +
                ", requestId='" + requestId + '\'' +
                '}';
    }
}
