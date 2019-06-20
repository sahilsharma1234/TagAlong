package com.carpool.tagalong.models;

import com.google.gson.annotations.SerializedName;

public class ModelRidePostResponseData {

    @SerializedName("_id")
    public String _id;
    @SerializedName("__v")
    public int __v;
    @SerializedName("allowKids")
    public boolean allowKids;
    @SerializedName("bags")
    public int bags;
    @SerializedName("createdAt")
    public String createdAt;
    @SerializedName("distBtwSrcDest")
    public double distBtwSrcDest;
    @SerializedName("endLat")
    public double endLat;
    @SerializedName("endLocation")
    public String endLocation;
    @SerializedName("endLong")
    public double endLong;
    @SerializedName("expectedCompletedDate")
    public String expectedCompletedDate;
    @SerializedName("isDrive")
    public boolean isDrive;
    @SerializedName("noOfSeatAvailable")
    public int noOfSeatAvailable;
    @SerializedName("noOfSeats")
    public int noOfSeats;
    @SerializedName("rideDateTime")
    public String rideDateTime;
    @SerializedName("smoke")
    public boolean smoke;
    @SerializedName("startLat")
    public double startLat;
    @SerializedName("startLocation")
    public String startLocation;
    @SerializedName("startLong")
    public double startLong;
    @SerializedName("status")
    public int status;
    @SerializedName("updatedAt")
    public String updatedAt;
    @SerializedName("userId")
    public String userId;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public int get__v() {
        return __v;
    }

    public void set__v(int __v) {
        this.__v = __v;
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

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public double getDistBtwSrcDest() {
        return distBtwSrcDest;
    }

    public void setDistBtwSrcDest(double distBtwSrcDest) {
        this.distBtwSrcDest = distBtwSrcDest;
    }

    public double getEndLat() {
        return endLat;
    }

    public void setEndLat(double endLat) {
        this.endLat = endLat;
    }

    public String getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(String endLocation) {
        this.endLocation = endLocation;
    }

    public double getEndLong() {
        return endLong;
    }

    public void setEndLong(double endLong) {
        this.endLong = endLong;
    }

    public String getExpectedCompletedDate() {
        return expectedCompletedDate;
    }

    public void setExpectedCompletedDate(String expectedCompletedDate) {
        this.expectedCompletedDate = expectedCompletedDate;
    }

    public boolean isDrive() {
        return isDrive;
    }

    public void setDrive(boolean drive) {
        isDrive = drive;
    }

    public int getNoOfSeatAvailable() {
        return noOfSeatAvailable;
    }

    public void setNoOfSeatAvailable(int noOfSeatAvailable) {
        this.noOfSeatAvailable = noOfSeatAvailable;
    }

    public int getNoOfSeats() {
        return noOfSeats;
    }

    public void setNoOfSeats(int noOfSeats) {
        this.noOfSeats = noOfSeats;
    }

    public String getRideDateTime() {
        return rideDateTime;
    }

    public void setRideDateTime(String rideDateTime) {
        this.rideDateTime = rideDateTime;
    }

    public boolean isSmoke() {
        return smoke;
    }

    public void setSmoke(boolean smoke) {
        this.smoke = smoke;
    }

    public double getStartLat() {
        return startLat;
    }

    public void setStartLat(double startLat) {
        this.startLat = startLat;
    }

    public String getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }

    public double getStartLong() {
        return startLong;
    }

    public void setStartLong(double startLong) {
        this.startLong = startLong;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "ModelRidePostResponseData{" +
                "_id='" + _id + '\'' +
                ", __v=" + __v +
                ", allowKids=" + allowKids +
                ", bags=" + bags +
                ", createdAt='" + createdAt + '\'' +
                ", distBtwSrcDest=" + distBtwSrcDest +
                ", endLat=" + endLat +
                ", endLocation='" + endLocation + '\'' +
                ", endLong=" + endLong +
                ", expectedCompletedDate='" + expectedCompletedDate + '\'' +
                ", isDrive=" + isDrive +
                ", noOfSeatAvailable=" + noOfSeatAvailable +
                ", noOfSeats=" + noOfSeats +
                ", rideDateTime='" + rideDateTime + '\'' +
                ", smoke=" + smoke +
                ", startLat=" + startLat +
                ", startLocation='" + startLocation + '\'' +
                ", startLong=" + startLong +
                ", status=" + status +
                ", updatedAt='" + updatedAt + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}
