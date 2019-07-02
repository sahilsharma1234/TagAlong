package com.carpool.tagalong.models.emergencysos;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ModelGetEmergencyRidesResponse  implements Serializable {

    @SerializedName("status")
    public int status;
    @SerializedName("message")
    public String message;
    @SerializedName("emergencyRides")
    public List<EmergencyRides> emergencyRides;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<EmergencyRides> getEmergencyRides() {
        return emergencyRides;
    }

    public void setEmergencyRides(List<EmergencyRides> emergencyRides) {
        this.emergencyRides = emergencyRides;
    }

    public static class Location implements Serializable {

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

    public static class DriverDetail implements Serializable {

        @SerializedName("_id")
        public String _id;
        @SerializedName("location")
        public Location location;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public Location getLocation() {
            return location;
        }

        public void setLocation(Location location) {
            this.location = location;
        }
    }

    public static class RiderList implements Serializable {

        @SerializedName("_id")
        public String _id;
        @SerializedName("userName")
        public String userName;
        @SerializedName("mobileNo")
        public String mobileNo;
        @SerializedName("profile_pic")
        public String profile_pic;

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

        public String getProfile_pic() {
            return profile_pic;
        }

        public void setProfile_pic(String profile_pic) {
            this.profile_pic = profile_pic;
        }
    }

    public static class EmergencyRides implements Serializable {

        @SerializedName("_id")
        public String _id;
        @SerializedName("endLocation")
        public String endLocation;
        @SerializedName("rideDateTime")
        public String rideDateTime;
        @SerializedName("startLocation")
        public String startLocation;
        @SerializedName("driverDetail")
        public DriverDetail driverDetail;
        @SerializedName("riderList")
        public List<RiderList> riderList;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
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

        public String getStartLocation() {
            return startLocation;
        }

        public void setStartLocation(String startLocation) {
            this.startLocation = startLocation;
        }

        public DriverDetail getDriverDetail() {
            return driverDetail;
        }

        public void setDriverDetail(DriverDetail driverDetail) {
            this.driverDetail = driverDetail;
        }

        public List<RiderList> getRiderList() {
            return riderList;
        }

        public void setRiderList(List<RiderList> riderList) {
            this.riderList = riderList;
        }
    }
}