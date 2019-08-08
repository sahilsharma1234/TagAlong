package com.carpool.tagalong.models.wepay;

import com.google.gson.annotations.SerializedName;

public class ModelIframeResponse {


    @SerializedName("account_id")
    public int account_id;
    @SerializedName("uri")
    public String uri;

    public int getAccount_id() {
        return account_id;
    }

    public void setAccount_id(int account_id) {
        this.account_id = account_id;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    @Override
    public String toString() {
        return "ModelIframeResponse{" +
                "account_id=" + account_id +
                ", uri='" + uri + '\'' +
                '}';
    }
}
