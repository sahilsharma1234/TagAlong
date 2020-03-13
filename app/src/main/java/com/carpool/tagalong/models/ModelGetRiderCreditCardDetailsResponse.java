package com.carpool.tagalong.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ModelGetRiderCreditCardDetailsResponse implements Serializable {

    @SerializedName("wepay_card_id")
    public String wepay_card_id;
    @SerializedName("number")
    public String number;
    @SerializedName("exp_month")
    public String exp_month;
    @SerializedName("exp_year")
    public String exp_year;
    @SerializedName("isDefault")
    public boolean isDefault;
    @SerializedName("_id")
    public String _id;

    public String getWepay_card_id() {
        return wepay_card_id;
    }

    public void setWepay_card_id(String wepay_card_id) {
        this.wepay_card_id = wepay_card_id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getExp_month() {
        return exp_month;
    }

    public void setExp_month(String exp_month) {
        this.exp_month = exp_month;
    }

    public String getExp_year() {
        return exp_year;
    }

    public void setExp_year(String exp_year) {
        this.exp_year = exp_year;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
}
