package com.carpool.tagalong.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ModelGetAccessTokenResponse implements Serializable {

    @SerializedName("status")
    public int status;
    @SerializedName("message")
    public String message;
    @SerializedName("data")
    public ModelGetAccessTokenResponseData data;

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

    public ModelGetAccessTokenResponseData getData() {
        return data;
    }

    public void setData(ModelGetAccessTokenResponseData data) {
        this.data = data;
    }
}
