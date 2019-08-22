package com.carpool.tagalong.models;

import com.google.gson.annotations.SerializedName;

public  class ModelWePayDetails {


    @SerializedName("wePayAccessToken")
    public String wePayAccessToken;
    @SerializedName("wePayAccountId")
    public String wePayAccountId;

    @SerializedName("wePayVerificationStatus")
    public String wePayVerificationStatus;


    public String getWePayAccessToken() {
        return wePayAccessToken;
    }

    public void setWePayAccessToken(String wePayAccessToken) {
        this.wePayAccessToken = wePayAccessToken;
    }

    public String getWePayAccountId() {
        return wePayAccountId;
    }

    public void setWePayAccountId(String wePayAccountId) {
        this.wePayAccountId = wePayAccountId;
    }


    public String getWePayVerificationStatus() {
        return wePayVerificationStatus;
    }

    public void setWePayVerificationStatus(String wePayVerificationStatus) {
        this.wePayVerificationStatus = wePayVerificationStatus;
    }

    @Override
    public String toString() {
        return "ModelWePayDetails{" +
                "wePayAccessToken='" + wePayAccessToken + '\'' +
                ", wePayAccountId='" + wePayAccountId + '\'' +
                ", wePayVerificationStatus='" + wePayVerificationStatus + '\'' +
                '}';
    }
}
