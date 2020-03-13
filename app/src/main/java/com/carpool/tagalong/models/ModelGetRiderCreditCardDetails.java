package com.carpool.tagalong.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ModelGetRiderCreditCardDetails implements Serializable {


    @SerializedName("status")
    public int status;
    @SerializedName("data")
    public ModelGetRiderCreditCardDetailsResponse data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public ModelGetRiderCreditCardDetailsResponse getData() {
        return data;
    }

    public void setData(ModelGetRiderCreditCardDetailsResponse data) {
        this.data = data;
    }
}
