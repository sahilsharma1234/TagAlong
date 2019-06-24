package com.carpool.tagalong.models;

import com.google.gson.annotations.SerializedName;

public class ModelForgotPasswordResponse {


    @SerializedName("message")
    public String message;
    @SerializedName("status")
    public int status;

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

    @Override
    public String toString() {
        return "ModelForgotPasswordResponse{" +
                "message='" + message + '\'' +
                ", status=" + status +
                '}';
    }
}