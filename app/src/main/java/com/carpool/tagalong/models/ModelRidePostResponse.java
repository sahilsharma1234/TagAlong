package com.carpool.tagalong.models;

import com.google.gson.annotations.SerializedName;

public class ModelRidePostResponse {

    @SerializedName("message")
    public String message;
    @SerializedName("status")
    public int status;
    @SerializedName("data")
    public ModelRidePostResponseData data;

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

    public ModelRidePostResponseData getData() {
        return data;
    }

    public void setData(ModelRidePostResponseData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ModelRidePostResponse{" +
                "message='" + message + '\'' +
                ", status=" + status +
                ", data=" + data +
                '}';
    }
}
