package com.carpool.tagalong.models;

import com.google.gson.annotations.SerializedName;

public class ModelVerifyOtp {


    @SerializedName("mobileNo")
    public String mobileNo;
    @SerializedName("otp")
    public int otp;

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public int getOtp() {
        return otp;
    }

    public void setOtp(int otp) {
        this.otp = otp;
    }

    @Override
    public String toString() {
        return "ModelVerifyOtp{" +
                "mobileNo='" + mobileNo + '\'' +
                ", otp=" + otp +
                '}';
    }
}
