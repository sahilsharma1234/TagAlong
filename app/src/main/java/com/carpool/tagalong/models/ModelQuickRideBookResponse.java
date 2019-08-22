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

    @SerializedName("passengersPickupComingUp")
    public List<PassengersPickupComingUp> passengersPickupComingUp;

    public List<PassengersPickupComingUp> getPassengersPickupComingUp() {
        return passengersPickupComingUp;
    }

    public void setPassengersPickupComingUp(List<PassengersPickupComingUp> passengersPickupComingUp) {
        this.passengersPickupComingUp = passengersPickupComingUp;
    }

    @Override
    public String toString() {
        return "ModelQuickRideBookResponse{" +
                "message='" + message + '\'' +
                ", status=" + status +
                ", driverDetails=" + driverDetails +
                ", passengersPickupComingUp=" + passengersPickupComingUp +
                '}';
    }

    public static class DriverDetails {
        @SerializedName("_id")
        public String _id;
        @SerializedName("name")
        public String name;
        @SerializedName("mobileNo")
        public String mobileNo;
        @SerializedName("rating")
        public int rating;
        @SerializedName("profile_pic")
        public String profile_pic;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMobileNo() {
            return mobileNo;
        }

        public void setMobileNo(String mobileNo) {
            this.mobileNo = mobileNo;
        }

        public int getRating() {
            return rating;
        }

        public void setRating(int rating) {
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
