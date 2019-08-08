package com.carpool.tagalong.models.wepay;

import com.google.gson.annotations.SerializedName;

public class ModelRegisterMerchantWePayRequest {


    @SerializedName("original_ip")
    public String original_ip;
    @SerializedName("original_device")
    public String original_device;
    @SerializedName("last_name")
    public String last_name;

    public String getOriginal_ip() {
        return original_ip;
    }

    public void setOriginal_ip(String original_ip) {
        this.original_ip = original_ip;
    }

    public String getOriginal_device() {
        return original_device;
    }

    public void setOriginal_device(String original_device) {
        this.original_device = original_device;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    @Override
    public String toString() {
        return "ModelRegisterMerchantWePayRequest{" +
                "original_ip='" + original_ip + '\'' +
                ", original_device='" + original_device + '\'' +
                ", last_name='" + last_name + '\'' +
                '}';
    }
}
