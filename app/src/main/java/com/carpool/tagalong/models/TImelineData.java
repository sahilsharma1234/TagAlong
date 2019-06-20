package com.carpool.tagalong.models;

import com.google.gson.annotations.SerializedName;

public class TImelineData {

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
    @SerializedName("profile_pic")
    public String profile_pic;
    @SerializedName("userName")
    public String userName;

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
}
