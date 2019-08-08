package com.carpool.tagalong.models;

import com.google.gson.annotations.SerializedName;

public  class ModelWePayDetails {


    @SerializedName("wePayAccessToken")
    public String wePayAccessToken;
    @SerializedName("wePayAccountId")
    public String wePayAccountId;


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


    @Override
    public String toString() {
        return "ModelWePayDetails{" +
                "wePayAccessToken='" + wePayAccessToken + '\'' +
                ", wePayAccountId='" + wePayAccountId + '\'' +
                '}';
    }
}
