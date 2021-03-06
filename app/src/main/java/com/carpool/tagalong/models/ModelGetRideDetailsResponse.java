package com.carpool.tagalong.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ModelGetRideDetailsResponse implements Serializable {

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

    public static class JoinRequest implements Serializable {

        @SerializedName("_id")
        public String _id;
        @SerializedName("bags")
        public int bags;
        @SerializedName("allowKids")
        public boolean allowKids;
        @SerializedName("estimatedFare")
        public double estimatedFare;
        @SerializedName("status")
        public int status;
        @SerializedName("startLocation")
        public String startLocation;
        @SerializedName("endLocation")
        public String endLocation;
        @SerializedName("rideDateTime")
        public String rideDateTime;
        @SerializedName("noOfSeats")
        public int noOfSeats;
        @SerializedName("createdAt")
        public String createdAt;
        @SerializedName("userId")
        public String userId;
        @SerializedName("address")
        public String address;
        @SerializedName("userName")
        public String userName;
        @SerializedName("profile_pic")
        public String profile_pic;
        @SerializedName("rating")
        public double rating;
        @SerializedName("payStatus")
        public boolean payStatus;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public int getBags() {
            return bags;
        }

        public void setBags(int bags) {
            this.bags = bags;
        }

        public boolean isAllowKids() {
            return allowKids;
        }

        public void setAllowKids(boolean allowKids) {
            this.allowKids = allowKids;
        }

        public double getEstimatedFare() {
            return estimatedFare;
        }

        public void setEstimatedFare(double estimatedFare) {
            this.estimatedFare = estimatedFare;
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

        public int getNoOfSeats() {
            return noOfSeats;
        }

        public void setNoOfSeats(int noOfSeats) {
            this.noOfSeats = noOfSeats;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
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

        public double getRating() {
            return rating;
        }

        public void setRating(double rating) {
            this.rating = rating;
        }

        public boolean isPayStatus() {
            return payStatus;
        }

        public void setPayStatus(boolean payStatus) {
            this.payStatus = payStatus;
        }
    }

    public static class OnBoard implements Serializable {

        @SerializedName("_id")
        public String _id;
        @SerializedName("bags")
        public int bags;
        @SerializedName("allowKids")
        public boolean allowKids;
        @SerializedName("estimatedFare")
        public double estimatedFare;
        @SerializedName("status")
        public int status;
        @SerializedName("startLocation")
        public String startLocation;
        @SerializedName("endLocation")
        public String endLocation;
        @SerializedName("rideDateTime")
        public String rideDateTime;
        @SerializedName("noOfSeats")
        public int noOfSeats;
        @SerializedName("createdAt")
        public String createdAt;
        @SerializedName("userId")
        public String userId;
        @SerializedName("address")
        public String address;
        @SerializedName("userName")
        public String userName;
        @SerializedName("profile_pic")
        public String profile_pic;
        @SerializedName("rating")
        public double rating;
        @SerializedName("payStatus")
        public boolean payStatus;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public int getBags() {
            return bags;
        }

        public void setBags(int bags) {
            this.bags = bags;
        }

        public boolean isAllowKids() {
            return allowKids;
        }

        public void setAllowKids(boolean allowKids) {
            this.allowKids = allowKids;
        }

        public double getEstimatedFare() {
            return estimatedFare;
        }

        public void setEstimatedFare(double estimatedFare) {
            this.estimatedFare = estimatedFare;
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

        public int getNoOfSeats() {
            return noOfSeats;
        }

        public void setNoOfSeats(int noOfSeats) {
            this.noOfSeats = noOfSeats;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
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

        public double getRating() {
            return rating;
        }

        public void setRating(double rating) {
            this.rating = rating;
        }

        public boolean isPayStatus() {
            return payStatus;
        }

        public void setPayStatus(boolean payStatus) {
            this.payStatus = payStatus;
        }
    }

    public static class RidersList implements Serializable {

    }

    public static class Timeline implements Serializable {

        @SerializedName("_id")
        public String _id;
        @SerializedName("type")
        public int type;
        @SerializedName("rideId")
        public String rideId;
        @SerializedName("postUrl")
        public String postUrl;
        @SerializedName("createdAt")
        public String createdAt;
        @SerializedName("userName")
        public String userName;
        @SerializedName("profile_pic")
        public String profile_pic;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getRideId() {
            return rideId;
        }

        public void setRideId(String rideId) {
            this.rideId = rideId;
        }

        public String getPostUrl() {
            return postUrl;
        }

        public void setPostUrl(String postUrl) {
            this.postUrl = postUrl;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
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
    }

    public static class RideData implements Serializable {

        @SerializedName("_id")
        public String _id;
        @SerializedName("createdAt")
        public String createdAt;
        @SerializedName("endLocation")
        public String endLocation;
        @SerializedName("isDrive")
        public boolean isDrive;
        @SerializedName("rideDateTime")
        public String rideDateTime;
        @SerializedName("startLocation")
        public String startLocation;
        @SerializedName("status")
        public int status;
        @SerializedName("userId")
        public String userId;
        @SerializedName("profile_pic")
        public String profile_pic;
        @SerializedName("userName")
        public String userName;
        @SerializedName("joinRequest")
        public List<JoinRequest> joinRequest;
        @SerializedName("onBoard")
        public List<OnBoard> onBoard;
        @SerializedName("ridersList")
        public List<RidersList> ridersList;
        @SerializedName("timeline")
        public List<Timeline> timeline;
        @SerializedName("driverDetails")
        public DriverDetails driverDetails;

        @SerializedName("estimatedFare")
        public double estimatedFare;

        @SerializedName("payStatus")
        public boolean payStatus;

        public double getEstimatedFare() {
            return estimatedFare;
        }

        public void setEstimatedFare(double estimatedFare) {
            this.estimatedFare = estimatedFare;
        }

        public boolean isPayStatus() {
            return payStatus;
        }

        public void setPayStatus(boolean payStatus) {
            this.payStatus = payStatus;
        }

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getEndLocation() {
            return endLocation;
        }

        public void setEndLocation(String endLocation) {
            this.endLocation = endLocation;
        }

        public boolean isDrive() {
            return isDrive;
        }

        public void setDrive(boolean drive) {
            isDrive = drive;
        }

        public String getRideDateTime() {
            return rideDateTime;
        }

        public void setRideDateTime(String rideDateTime) {
            this.rideDateTime = rideDateTime;
        }

        public String getStartLocation() {
            return startLocation;
        }

        public void setStartLocation(String startLocation) {
            this.startLocation = startLocation;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getProfile_pic() {
            return profile_pic;
        }

        public void setProfile_pic(String profile_pic) {
            this.profile_pic = profile_pic;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public List<JoinRequest> getJoinRequest() {
            return joinRequest;
        }

        public void setJoinRequest(List<JoinRequest> joinRequest) {
            this.joinRequest = joinRequest;
        }

        public List<OnBoard> getOnBoard() {
            return onBoard;
        }

        public void setOnBoard(List<OnBoard> onBoard) {
            this.onBoard = onBoard;
        }

        public List<RidersList> getRidersList() {
            return ridersList;
        }

        public void setRidersList(List<RidersList> ridersList) {
            this.ridersList = ridersList;
        }

        public List<Timeline> getTimeline() {
            return timeline;
        }

        public void setTimeline(List<Timeline> timeline) {
            this.timeline = timeline;
        }

        public DriverDetails getDriverDetails() {
            return driverDetails;
        }

        public void setDriverDetails(DriverDetails driverDetails) {
            this.driverDetails = driverDetails;
        }

        @Override
        public String toString() {
            return "RideData{" +
                    "_id='" + _id + '\'' +
                    ", createdAt='" + createdAt + '\'' +
                    ", endLocation='" + endLocation + '\'' +
                    ", isDrive=" + isDrive +
                    ", rideDateTime='" + rideDateTime + '\'' +
                    ", startLocation='" + startLocation + '\'' +
                    ", status=" + status +
                    ", userId='" + userId + '\'' +
                    ", profile_pic='" + profile_pic + '\'' +
                    ", userName='" + userName + '\'' +
                    ", joinRequest=" + joinRequest +
                    ", onBoard=" + onBoard +
                    ", ridersList=" + ridersList +
                    ", timeline=" + timeline +
                    ", driverDetails=" + driverDetails +
                    ", estimatedFare=" + estimatedFare +
                    ", payStatus=" + payStatus +
                    '}';
        }
    }

    public class DriverDetails implements Serializable {

        @SerializedName("_id")
        public String _id;
        @SerializedName("userId")
        public String userId;
        @SerializedName("userName")
        public String userName;
        @SerializedName("vehicle")
        public String vehicle;
        @SerializedName("vehicleNumber")
        public String vehicleNumber;
        @SerializedName("startLocation")
        public String startLocation;
        @SerializedName("endLocation")
        public String endLocation;
        @SerializedName("rideDateTime")
        public String rideDateTime;
        @SerializedName("profile_pic")
        public String profile_pic;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getVehicle() {
            return vehicle;
        }

        public void setVehicle(String vehicle) {
            this.vehicle = vehicle;
        }

        public String getVehicleNumber() {
            return vehicleNumber;
        }

        public void setVehicleNumber(String vehicleNumber) {
            this.vehicleNumber = vehicleNumber;
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

        public String getProfile_pic() {
            return profile_pic;
        }

        public void setProfile_pic(String profile_pic) {
            this.profile_pic = profile_pic;
        }
    }
}
