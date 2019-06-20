package com.carpool.tagalong.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ModelGetTimelineResponse {


    @SerializedName("status")
    public int status;
    @SerializedName("message")
    public String message;
    @SerializedName("data")
    public List<ModelGetCurrentRideResponse.Timeline> data;

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

    public List<ModelGetCurrentRideResponse.Timeline> getData() {
        return data;
    }

    public void setData(List<ModelGetCurrentRideResponse.Timeline> data) {
        this.data = data;
    }
}
