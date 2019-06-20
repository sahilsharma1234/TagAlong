package com.carpool.tagalong.models;

import com.google.gson.annotations.SerializedName;

public class ModelRateRiderequest {

    @SerializedName("rateTo")
    public String rateTo;
    @SerializedName("rideId")
    public String rideId;
    @SerializedName("rating")
    public double rating;
    @SerializedName("review")
    public String review;

    public String getRateTo() {
        return rateTo;
    }

    public void setRateTo(String rateTo) {
        this.rateTo = rateTo;
    }

    public String getRideId() {
        return rideId;
    }

    public void setRideId(String rideId) {
        this.rideId = rideId;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    @Override
    public String toString() {
        return "ModelRateRiderequest{" +
                "rateTo='" + rateTo + '\'' +
                ", rideId='" + rideId + '\'' +
                ", rating=" + rating +
                ", review='" + review + '\'' +
                '}';
    }
}