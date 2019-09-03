package com.carpool.tagalong.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ModelQuickRideBookResponse {

    @SerializedName("message")
    public String message;
    @SerializedName("status")
    public int status;
    @SerializedName("driverDetails")
    public DriverDetails driverDetails;

    @SerializedName("requestId")
    public String requestId;

    @SerializedName("pickupVerificationCode")
    public String pickupVerificationCode;

    @SerializedName("passengersPickupComingUp")
    public List<PassengersPickupComingUp> passengersPickupComingUp;

    public List<PassengersPickupComingUp> getPassengersPickupComingUp() {
        return passengersPickupComingUp;
    }

    public void setPassengersPickupComingUp(List<PassengersPickupComingUp> passengersPickupComingUp) {
        this.passengersPickupComingUp = passengersPickupComingUp;
    }

    public String getPickupVerificationCode() {
        return pickupVerificationCode;
    }

    public void setPickupVerificationCode(String pickupVerificationCode) {
        this.pickupVerificationCode = pickupVerificationCode;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    @Override
    public String toString() {
        return "ModelQuickRideBookResponse{" +
                "message='" + message + '\'' +
                ", status=" + status +
                ", driverDetails=" + driverDetails +
                ", requestId='" + requestId + '\'' +
                ", pickupVerificationCode='" + pickupVerificationCode + '\'' +
                ", passengersPickupComingUp=" + passengersPickupComingUp +
                '}';
    }

    public static class DriverDetails {

        @SerializedName("_id")
        public String _id;
        @SerializedName("userName")
        public String userName;
        @SerializedName("mobileNo")
        public String mobileNo;
        @SerializedName("rating")
        public double rating;
        @SerializedName("profile_pic")
        public String profile_pic;
        @SerializedName("vehicle")
        public String vehicle;
        @SerializedName("vehicleNumber")
        public String vehicleNumber;

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

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getMobileNo() {
            return mobileNo;
        }

        public void setMobileNo(String mobileNo) {
            this.mobileNo = mobileNo;
        }

        public double getRating() {
            return rating;
        }

        public void setRating(double rating) {
            this.rating = rating;
        }

        public String getProfile_pic() {
            return profile_pic;
        }

        public void setProfile_pic(String profile_pic) {
            this.profile_pic = profile_pic;
        }
    }

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

    public DriverDetails getDriverDetails() {
        return driverDetails;
    }

    public void setDriverDetails(DriverDetails driverDetails) {
        this.driverDetails = driverDetails;
    }

    public static class PassengersPickupComingUp {
        @SerializedName("_id")
        public String _id;
        @SerializedName("startLat")
        public double startLat;
        @SerializedName("startLong")
        public double startLong;
    }
}
