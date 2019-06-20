package com.carpool.tagalong.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ModelSearchRideResponse {


    @SerializedName("message")
    public String message;
    @SerializedName("status")
    public int status;
    @SerializedName("data")
    public List<ModelSearchRideResponseData> data;

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

    public List<ModelSearchRideResponseData> getData() {
        return data;
    }

    public void setData(List<ModelSearchRideResponseData> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ModelSearchRideResponse{" +
                "message='" + message + '\'' +
                ", status=" + status +
                ", data=" + data +
                '}';
    }
}