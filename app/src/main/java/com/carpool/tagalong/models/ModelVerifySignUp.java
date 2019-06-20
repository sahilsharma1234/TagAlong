package com.carpool.tagalong.models;

import com.google.gson.annotations.SerializedName;

public class ModelVerifySignUp {

    @SerializedName("otp")
    public String otp;

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    @Override
    public String toString() {
        return "ModelVerifySignUp{" +
                "otp='" + otp + '\'' +
                '}';
    }
}