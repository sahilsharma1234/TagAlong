package com.carpool.tagalong.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ModelGetDriverProfileResponse {


    @SerializedName("status")
    public int status;
    @SerializedName("message")
    public String message;
    @SerializedName("data")
    public Data data;

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

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class TimelineData {

        @SerializedName("_id")
        public String _id;
        @SerializedName("type")
        public int type;
        @SerializedName("postUrl")
        public String postUrl;

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

        public String getPostUrl() {
            return postUrl;
        }

        public void setPostUrl(String postUrl) {
            this.postUrl = postUrl;
        }
    }

    public class Rides {

        @SerializedName("_id")
        public String _id;
        @SerializedName("userId")
        public String userId;
        @SerializedName("heading")
        public String heading;
        @SerializedName("postUrl")
        public String postUrl;
        @SerializedName("timelineData")
        public List<TimelineData> timelineData;

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

        public String getHeading() {
            return heading;
        }

        public void setHeading(String heading) {
            this.heading = heading;
        }

        public String getPostUrl() {
            return postUrl;
        }

        public void setPostUrl(String postUrl) {
            this.postUrl = postUrl;
        }

        public List<TimelineData> getTimelineData() {
            return timelineData;
        }

        public void setTimelineData(List<TimelineData> timelineData) {
            this.timelineData = timelineData;
        }
    }

    public class Data {

        @SerializedName("_id")
        public String _id;
        @SerializedName("profile_pic")
        public String profile_pic;
        @SerializedName("userName")
        public String userName;
        @SerializedName("address")
        public String address;
        @SerializedName("drove")
        public String drove;
        @SerializedName("trips")
        public int trips;
        @SerializedName("rating")
        public double rating;
        @SerializedName("rides")
        public List<Rides> rides;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
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

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getDrove() {
            return drove;
        }

        public void setDrove(String drove) {
            this.drove = drove;
        }

        public int getTrips() {
            return trips;
        }

        public void setTrips(int trips) {
            this.trips = trips;
        }

        public double getRating() {
            return rating;
        }

        public void setRating(double rating) {
            this.rating = rating;
        }

        public List<Rides> getRides() {
            return rides;
        }

        public void setRides(List<Rides> rides) {
            this.rides = rides;
        }
    }
}