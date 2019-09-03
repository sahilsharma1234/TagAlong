package com.carpool.tagalong.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ModelSearchRideResponseData implements Serializable {
    @SerializedName("userId")
    public String userId;
    @SerializedName("rideId")
    public String rideId;
    @SerializedName("startLocation")
    public String startLocation;
    @SerializedName("endLocation")
    public String endLocation;
    @SerializedName("totalDistanceFromSrcDest")
    public double totalDistanceFromSrcDest;
    @SerializedName("estimatedFare")
    public double estimatedFare;
    @SerializedName("userName")
    public String userName;
    @SerializedName("profile_pic")
    public String profile_pic;
    @SerializedName("smoke")
    public boolean smoke;
    @SerializedName("rideDateTime")
    public String rideDateTime;
    @SerializedName("rating")
    public double rating;
    @SerializedName("createdAt")
    public String createdAt;

    public double getTotalDistanceFromSrcDest() {
        return totalDistanceFromSrcDest;
    }

    public void setTotalDistanceFromSrcDest(double totalDistanceFromSrcDest) {
        this.totalDistanceFromSrcDest = totalDistanceFromSrcDest;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRideId() {
        return rideId;
    }

    public void setRideId(String rideId) {
        this.rideId = rideId;
    }

    public String getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }

    public String getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(String endLocation) {
        this.endLocation = endLocation;
    }

    public void setTotalDistanceFromSrcDest(int totalDistanceFromSrcDest) {
        this.totalDistanceFromSrcDest = totalDistanceFromSrcDest;
    }

    public double getEstimatedFare() {
        return estimatedFare;
    }

    public void setEstimatedFare(double estimatedFare) {
        this.estimatedFare = estimatedFare;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }

    public boolean isSmoke() {
        return smoke;
    }

    public void setSmoke(boolean smoke) {
        this.smoke = smoke;
    }

    public String getRideDateTime() {
        return rideDateTime;
    }

    public void setRideDateTime(String rideDateTime) {
        this.rideDateTime = rideDateTime;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "ModelSearchRideResponseData{" +
                "userId='" + userId + '\'' +
                ", rideId='" + rideId + '\'' +
                ", startLocation='" + startLocation + '\'' +
                ", endLocation='" + endLocation + '\'' +
                ", totalDistanceFromSrcDest=" + totalDistanceFromSrcDest +
                ", estimatedFare=" + estimatedFare +
                ", userName='" + userName + '\'' +
                ", profile_pic='" + profile_pic + '\'' +
                ", smoke=" + smoke +
                ", rideDateTime='" + rideDateTime + '\'' +
                ", rating=" + rating +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}
