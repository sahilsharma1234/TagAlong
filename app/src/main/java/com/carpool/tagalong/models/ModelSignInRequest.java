package com.carpool.tagalong.models;

import com.google.gson.annotations.SerializedName;

public class ModelSignInRequest {

    @SerializedName("mobileNo")
    public String mobileNo;

    @SerializedName("password")
    public String password;
    @SerializedName("deviceToken")
    public String deviceToken;
    @SerializedName("deviceType")
    public int deviceType;

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "ModelSignInRequest{" +
                "mobileNo='" + mobileNo + '\'' +
                ", password='" + password + '\'' +
                ", deviceToken='" + deviceToken + '\'' +
                ", deviceType=" + deviceType +
                '}';
    }
}