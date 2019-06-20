package com.carpool.tagalong.models;

import com.google.gson.annotations.SerializedName;

public class ModelForgotPasswordRequest {


    @SerializedName("mobileNo")
    public String mobileNo;

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    @Override
    public String toString() {
        return "ModelForgotPasswordRequest{" +
                "mobileNo='" + mobileNo + '\'' +
                '}';
    }
}
