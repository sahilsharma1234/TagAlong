package com.carpool.tagalong.models.wepay;

import com.google.gson.annotations.SerializedName;

public class ModelGetWePayIframeRequest {

    @SerializedName("account_id")
    public long account_id;
    @SerializedName("mode")
    public String mode;

    public long getAccount_id() {
        return account_id;
    }

    public void setAccount_id(long account_id) {
        this.account_id = account_id;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    @Override
    public String toString() {
        return "ModelGetWePayIframeRequest{" +
                "account_id=" + account_id +
                ", mode='" + mode + '\'' +
                '}';
    }
}
