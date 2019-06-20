package com.carpool.tagalong.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ModelGetAllRidesResponse {

    @SerializedName("message")
    public String message;
    @SerializedName("status")
    public int status;
    @SerializedName("rideData")
    public RideData rideData;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public RideData getRideData() {
        return rideData;
    }

    public void setRideData(RideData rideData) {
        this.rideData = rideData;
    }

    @Override
    public String toString() {
        return "ModelGetAllRidesResponse{" +
                "message='" + message + '\'' +
                ", status=" + status +
                ", rideData=" + rideData +
                '}';
    }

    public static class Rides {
        @SerializedName("_id")
        public String _id;
        @SerializedName("isDrive")
        public boolean isDrive;
        @SerializedName("status")
        public int status;
        @SerializedName("startLocation")
        public String startLocation;
        @SerializedName("endLocation")
        public String endLocation;
        @SerializedName("rideDateTime")
        public String rideDateTime;
        @SerializedName("userId")
        public String userId;
        @SerializedName("rideDate")
        public String rideDate;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public boolean isDrive() {
            return isDrive;
        }

        public void setDrive(boolean drive) {
            isDrive = drive;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
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

        public String getRideDateTime() {
            return rideDateTime;
        }

        public void setRideDateTime(String rideDateTime) {
            this.rideDateTime = rideDateTime;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getRideDate() {
            return rideDate;
        }

        public void setRideDate(String rideDate) {
            this.rideDate = rideDate;
        }

        @Override
        public String toString() {
            return "Rides{" +
                    "_id='" + _id + '\'' +
                    ", isDrive=" + isDrive +
                    ", status=" + status +
                    ", startLocation='" + startLocation + '\'' +
                    ", endLocation='" + endLocation + '\'' +
                    ", rideDateTime='" + rideDateTime + '\'' +
                    ", userId='" + userId + '\'' +
                    ", rideDate='" + rideDate + '\'' +
                    '}';
        }
    }

//    public static class UpcomingRidesArr {
//        @SerializedName("_id")
//        public String _id;
//        @SerializedName("endLocation")
//        public String endLocation;
//        @SerializedName("isDrive")
//        public boolean isDrive;
//        @SerializedName("rideDateTime")
//        public String rideDateTime;
//        @SerializedName("startLocation")
//        public String startLocation;
//        @SerializedName("status")
//        public int status;
//        @SerializedName("userId")
//        public String userId;
//        @SerializedName("rideDate")
//        public String rideDate;
//
//        public String get_id() {
//            return _id;
//        }
//
//        public void set_id(String _id) {
//            this._id = _id;
//        }
//
//        public String getEndLocation() {
//            return endLocation;
//        }
//
//        public void setEndLocation(String endLocation) {
//            this.endLocation = endLocation;
//        }
//
//        public boolean isDrive() {
//            return isDrive;
//        }
//
//        public void setDrive(boolean drive) {
//            isDrive = drive;
//        }
//
//        public String getRideDateTime() {
//            return rideDateTime;
//        }
//
//        public void setRideDateTime(String rideDateTime) {
//            this.rideDateTime = rideDateTime;
//        }
//
//        public String getStartLocation() {
//            return startLocation;
//        }
//
//        public void setStartLocation(String startLocation) {
//            this.startLocation = startLocation;
//        }
//
//        public int getStatus() {
//            return status;
//        }
//
//        public void setStatus(int status) {
//            this.status = status;
//        }
//
//        public String getUserId() {
//            return userId;
//        }
//
//        public void setUserId(String userId) {
//            this.userId = userId;
//        }
//
//        public String getRideDate() {
//            return rideDate;
//        }
//
//        public void setRideDate(String rideDate) {
//            this.rideDate = rideDate;
//        }
//    }

    public static class RideData {
        @SerializedName("currentRidesArr")
        public List<Rides> currentRidesArr;
        @SerializedName("upcomingRidesArr")
        public List<Rides> upcomingRidesArr;

        public List<Rides> getCurrentRidesArr() {
            return currentRidesArr;
        }

        public void setCurrentRidesArr(List<Rides> currentRidesArr) {
            this.currentRidesArr = currentRidesArr;
        }

        public List<Rides> getUpcomingRidesArr() {
            return upcomingRidesArr;
        }

        public void setUpcomingRidesArr(List<Rides> upcomingRidesArr) {
            this.upcomingRidesArr = upcomingRidesArr;
        }

        @Override
        public String toString() {
            return "RideData{" +
                    "currentRidesArr=" + currentRidesArr +
                    ", upcomingRidesArr=" + upcomingRidesArr +
                    '}';
        }
    }
}
