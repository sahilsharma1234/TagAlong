package com.carpool.tagalong.models;

import com.google.gson.annotations.SerializedName;

public class ModelRequestRide {

    @SerializedName("rideId")
    public String rideId;
    @SerializedName("driverId")
    public String driverId;
    @SerializedName("startLocation")
    public String startLocation;
    @SerializedName("endLocation")
    public String endLocation;
    @SerializedName("startLat")
    public double startLat;
    @SerializedName("startLong")
    public double startLong;
    @SerializedName("endLat")
    public double endLat;
    @SerializedName("endLong")
    public double endLong;
    @SerializedName("rideDateTime")
    public String rideDateTime;
    @SerializedName("noOfSeats")
    public int noOfSeats;
    @SerializedName("smoke")
    public boolean smoke;
    @SerializedName("allowKids")
    public boolean allowKids;
    @SerializedName("bags")
    public int bags;
    @SerializedName("estimatedFare")
    public double estimatedFare;
    @SerializedName("distBtwSrcDest")
    public double distBtwSrcDest;
    @SerializedName("payMethod")
    public String payMethod;

    public String getPayMethod() {
        return payMethod;
    }

    public void setPayMethod(String payMethod) {
        this.payMethod = payMethod;
    }

    public String getRideId() {
        return rideId;
    }

    public void setRideId(String rideId) {
        this.rideId = rideId;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
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

    public String getRideDateTime() {
        return rideDateTime;
    }

    public void setRideDateTime(String rideDateTime) {
        this.rideDateTime = rideDateTime;
    }

    public int getNoOfSeats() {
        return noOfSeats;
    }

    public void setNoOfSeats(int noOfSeats) {
        this.noOfSeats = noOfSeats;
    }

    public boolean isSmoke() {
        return smoke;
    }

    public void setSmoke(boolean smoke) {
        this.smoke = smoke;
    }

    public boolean isAllowKids() {
        return allowKids;
    }

    public void setAllowKids(boolean allowKids) {
        this.allowKids = allowKids;
    }

    public int getBags() {
        return bags;
    }

    public void setBags(int bags) {
        this.bags = bags;
    }

    public double getEstimatedFare() {
        return estimatedFare;
    }

    public void setEstimatedFare(double estimatedFare) {
        this.estimatedFare = estimatedFare;
    }

    public double getDistBtwSrcDest() {
        return distBtwSrcDest;
    }

    public void setDistBtwSrcDest(double distBtwSrcDest) {
        this.distBtwSrcDest = distBtwSrcDest;
    }

    @Override
    public String toString() {
        return "ModelRequestRide{" +
                "rideId='" + rideId + '\'' +
                ", driverId='" + driverId + '\'' +
                ", startLocation='" + startLocation + '\'' +
                ", endLocation='" + endLocation + '\'' +
                ", startLat=" + startLat +
                ", startLong=" + startLong +
                ", endLat=" + endLat +
                ", endLong=" + endLong +
                ", rideDateTime='" + rideDateTime + '\'' +
                ", noOfSeats=" + noOfSeats +
                ", smoke=" + smoke +
                ", allowKids=" + allowKids +
                ", bags=" + bags +
                ", estimatedFare=" + estimatedFare +
                ", distBtwSrcDest=" + distBtwSrcDest +
                ", payMethod='" + payMethod + '\'' +
                '}';
    }
}
