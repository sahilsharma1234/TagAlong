package com.carpool.tagalong.models;

import com.google.gson.annotations.SerializedName;

public class ModelSignUpResponse  {

    @SerializedName("message")
    public String message;
    @SerializedName("status")
    public int status;
    @SerializedName("_id")
    public String _id;
    @SerializedName("otp")
    public String otp;
    @SerializedName("token")
    public String token;

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

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "ModelSignUpResponse{" +
                "message='" + message + '\'' +
                ", status=" + status +
                ", _id='" + _id + '\'' +
                ", otp='" + otp + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
