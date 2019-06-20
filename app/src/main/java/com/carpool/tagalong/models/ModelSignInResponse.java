package com.carpool.tagalong.models;

import com.google.gson.annotations.SerializedName;

public class ModelSignInResponse {

    @SerializedName("status")
    public int status;
    @SerializedName("auth")
    public boolean auth;
    @SerializedName("token")
    public String token;
    @SerializedName("message")
    public String message;
    @SerializedName("data")
    public ModelSignInResponseData data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isAuth() {
        return auth;
    }

    public void setAuth(boolean auth) {
        this.auth = auth;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ModelSignInResponseData getData() {
        return data;
    }

    public void setData(ModelSignInResponseData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ModelSignInResponse{" +
                "status=" + status +
                ", auth=" + auth +
                ", token='" + token + '\'' +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
