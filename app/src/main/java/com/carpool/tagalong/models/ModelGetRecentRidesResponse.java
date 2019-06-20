package com.carpool.tagalong.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ModelGetRecentRidesResponse {


    @SerializedName("message")
    public String message;
    @SerializedName("status")
    public int status;
    @SerializedName("rideData")
    public List<RideData> rideData;

    public class RidersList {
        @SerializedName("userName")
        public String userName;
        @SerializedName("profile_pic")
        public String profile_pic;

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

        @Override
        public String toString() {
            return "RidersList{" +
                    "userName='" + userName + '\'' +
                    ", profile_pic='" + profile_pic + '\'' +
                    '}';
        }
    }

    public class TimelineData {
        @SerializedName("_id")
        public String _id;
        @SerializedName("type")
        public int type;
        @SerializedName("rideId")
        public String rideId;
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

        @Override
        public String toString() {
            return "TimelineData{" +
                    "_id='" + _id + '\'' +
                    ", type=" + type +
                    ", rideId='" + rideId + '\'' +
                    ", postUrl='" + postUrl + '\'' +
                    '}';
        }
    }

    public class RideData {
        @SerializedName("_id")
        public String _id;
        @SerializedName("userId")
        public String userId;
        @SerializedName("status")
        public int status;
        @SerializedName("isDrive")
        public boolean isDrive;
        @SerializedName("heading")
        public String heading;
        @SerializedName("rideDate")
        public String rideDate;
        @SerializedName("distBtwSrcDest")
        public String distBtwSrcDest;
        @SerializedName("driverPic")
        public String driverPic;
        @SerializedName("driverName")
        public String driverName;
        @SerializedName("ridersList")
        public List<RidersList> ridersList;
        @SerializedName("timelineData")
        public List<TimelineData> timelineData;

        @SerializedName("startLocation")
        public String startLocation;
        @SerializedName("endLocation")
        public String endLocation;

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

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public boolean isDrive() {
            return isDrive;
        }

        public void setDrive(boolean drive) {
            isDrive = drive;
        }

        public String getHeading() {
            return heading;
        }

        public void setHeading(String heading) {
            this.heading = heading;
        }

        public String getRideDate() {
            return rideDate;
        }

        public void setRideDate(String rideDate) {
            this.rideDate = rideDate;
        }

        public String getDistBtwSrcDest() {
            return distBtwSrcDest;
        }

        public void setDistBtwSrcDest(String distBtwSrcDest) {
            this.distBtwSrcDest = distBtwSrcDest;
        }

        public String getDriverPic() {
            return driverPic;
        }

        public void setDriverPic(String driverPic) {
            this.driverPic = driverPic;
        }

        public String getDriverName() {
            return driverName;
        }

        public void setDriverName(String driverName) {
            this.driverName = driverName;
        }

        public List<RidersList> getRidersList() {
            return ridersList;
        }

        public void setRidersList(List<RidersList> ridersList) {
            this.ridersList = ridersList;
        }

        public List<TimelineData> getTimelineData() {
            return timelineData;
        }

        public void setTimelineData(List<TimelineData> timelineData) {
            this.timelineData = timelineData;
        }

        @Override
        public String toString() {
            return "RideData{" +
                    "_id='" + _id + '\'' +
                    ", userId='" + userId + '\'' +
                    ", status=" + status +
                    ", isDrive=" + isDrive +
                    ", heading='" + heading + '\'' +
                    ", rideDate='" + rideDate + '\'' +
                    ", distBtwSrcDest='" + distBtwSrcDest + '\'' +
                    ", driverPic='" + driverPic + '\'' +
                    ", driverName='" + driverName + '\'' +
                    ", ridersList=" + ridersList +
                    ", timelineData=" + timelineData +
                    ", startLocation='" + startLocation + '\'' +
                    ", endLocation='" + endLocation + '\'' +
                    '}';
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

    public List<RideData> getRideData() {
        return rideData;
    }

    public void setRideData(List<RideData> rideData) {
        this.rideData = rideData;
    }

    @Override
    public String toString() {
        return "ModelGetRecentRidesResponse{" +
                "message='" + message + '\'' +
                ", status=" + status +
                ", rideData=" + rideData +
                '}';
    }
}
