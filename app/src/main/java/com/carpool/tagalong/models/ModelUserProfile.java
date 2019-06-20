package com.carpool.tagalong.models;

import com.google.gson.annotations.SerializedName;

public class ModelUserProfile {

    @SerializedName("status")
    public int status;
    @SerializedName("message")
    public String message;
    @SerializedName("data")
    public ModelUserProfileData data;

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

    public ModelUserProfileData getData() {
        return data;
    }

    public void setData(ModelUserProfileData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ModelUserProfile{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
