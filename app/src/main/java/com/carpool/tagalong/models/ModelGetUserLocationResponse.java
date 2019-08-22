package com.carpool.tagalong.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ModelGetUserLocationResponse {


    @SerializedName("status")
    public int status;
    @SerializedName("message")
    public String message;
    @SerializedName("coordinates")
    public List<Double> coordinates;

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

    public List<Double> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<Double> coordinates) {
        this.coordinates = coordinates;
    }

    @Override
    public String toString() {
        return "ModelGetUserLocationResponse{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", coordinates=" + coordinates +
                '}';
    }
}
