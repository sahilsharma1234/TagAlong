package com.carpool.tagalong.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ModelGetCurrentRideResponse implements Serializable {

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

    public class JoinRequest implements Serializable {

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

        public double getEstimatedFare() {
            return estimatedFare;
        }

        public void setEstimatedFare(double estimatedFare) {
            this.estimatedFare = estimatedFare;
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

    public class OnBoard implements Serializable {

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

        @SerializedName("mobileNo")
        public String mobileNo;

        public String getMobileNo() {
            return mobileNo;
        }

        public void setMobileNo(String mobileNo) {
            this.mobileNo = mobileNo;
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

        public double getEstimatedFare() {
            return estimatedFare;
        }

        public void setEstimatedFare(double estimatedFare) {
            this.estimatedFare = estimatedFare;
        }

        public double getRating() {
            return rating;
        }

        public void setRating(double rating) {
            this.rating = rating;
        }

    }

    public class Timeline implements Serializable {

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

    public class RideData implements Serializable {

        @SerializedName("joinRequest")
        public List<JoinRequest> joinRequest;
        @SerializedName("onBoard")
        public List<OnBoard> onBoard;
        @SerializedName("timeline")
        public List<Timeline> timeline;
        @SerializedName("driverDetails")
        public DriverDetails driverDetails;
        @SerializedName("_id")
        public String _id;
        @SerializedName("isDrive")
        public boolean isDrive;
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
        @SerializedName("userId")
        public String userId;
        @SerializedName("createdAt")
        public String createdAt;
        @SerializedName("profile_pic")
        public String profile_pic;
        @SerializedName("userName")
        public String userName;
        @SerializedName("payStatus")
        public boolean payStatus;

        @SerializedName("isRideShort")
        public boolean isRideShort;

        @SerializedName("pickupVerificationCode")
        public String pickupVerificationCode;

        @SerializedName("driverETA")
        public String driverETA;

        @SerializedName("startLat")
        public double startLat;

        @SerializedName("startLong")
        public double startLong;

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

        public String getDriverETA() {
            return driverETA;
        }

        public void setDriverETA(String driverETA) {
            this.driverETA = driverETA;
        }

        public boolean isRideShort() {
            return isRideShort;
        }

        public void setRideShort(boolean rideShort) {
            isRideShort = rideShort;
        }

        public boolean isPayStatus() {
            return payStatus;
        }

        public void setPayStatus(boolean payStatus) {
            this.payStatus = payStatus;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
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

        public double getEstimatedFare() {
            return estimatedFare;
        }

        public void setEstimatedFare(double estimatedFare) {
            this.estimatedFare = estimatedFare;
        }

        public String getPickupVerificationCode() {
            return pickupVerificationCode;
        }

        public void setPickupVerificationCode(String pickupVerificationCode) {
            this.pickupVerificationCode = pickupVerificationCode;
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

        @SerializedName("status")
        public int status;

        @SerializedName("location")
        public LocationDriver location;
        @SerializedName("rating")
        public double rating;
        @SerializedName("mobileNo")
        public String mobileNo;

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

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public LocationDriver getLocation() {
            return location;
        }

        public void setLocation(LocationDriver location) {
            this.location = location;
        }

        public double getRating() {
            return rating;
        }

        public void setRating(double rating) {
            this.rating = rating;
        }

        public String getMobileNo() {
            return mobileNo;
        }

        public void setMobileNo(String mobileNo) {
            this.mobileNo = mobileNo;
        }

        class LocationDriver implements Serializable {

            @SerializedName("type")
            public String type;
            @SerializedName("coordinates")
            public List<Double> coordinates;

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public List<Double> getCoordinates() {
                return coordinates;
            }

            public void setCoordinates(List<Double> coordinates) {
                this.coordinates = coordinates;
            }
        }
    }
}