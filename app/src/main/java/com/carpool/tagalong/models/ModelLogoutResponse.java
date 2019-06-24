package com.carpool.tagalong.models;

import com.google.gson.annotations.SerializedName;

public class ModelLogoutResponse {


    @SerializedName("status")
    public int status;
    @SerializedName("message")
    public String message;

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
}