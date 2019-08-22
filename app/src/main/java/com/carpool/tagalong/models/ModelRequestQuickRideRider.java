package com.carpool.tagalong.models;

import com.google.gson.annotations.SerializedName;

public class ModelRequestQuickRideRider {


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
    @SerializedName("noOfSeats")
    public int noOfSeats;
    @SerializedName("smoke")
    public boolean smoke;
    @SerializedName("allowKids")
    public boolean allowKids;
    @SerializedName("bags")
    public int bags;
    @SerializedName("estimatedFare")
    public Double estimatedFare;

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

    public Double getEstimatedFare() {
        return estimatedFare;
    }

    public void setEstimatedFare(Double estimatedFare) {
        this.estimatedFare = estimatedFare;
    }

    @Override
    public String toString() {
        return "ModelRequestQuickRideRider{" +
                "startLocation='" + startLocation + '\'' +
                ", endLocation='" + endLocation + '\'' +
                ", startLat=" + startLat +
                ", startLong=" + startLong +
                ", endLat=" + endLat +
                ", endLong=" + endLong +
                ", noOfSeats=" + noOfSeats +
                ", smoke=" + smoke +
                ", allowKids=" + allowKids +
                ", bags=" + bags +
                ", estimatedFare=" + estimatedFare +
                '}';
    }
}
