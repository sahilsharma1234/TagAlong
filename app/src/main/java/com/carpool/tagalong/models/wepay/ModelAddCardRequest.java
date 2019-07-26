package com.carpool.tagalong.models.wepay;

import com.google.gson.annotations.SerializedName;

public class ModelAddCardRequest {

    @SerializedName("number")
    public String number;
    @SerializedName("exp_month")
    public int exp_month;
    @SerializedName("exp_year")
    public int exp_year;
    @SerializedName("cvv")
    public String cvv;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getExp_month() {
        return exp_month;
    }

    public void setExp_month(int exp_month) {
        this.exp_month = exp_month;
    }

    public int getExp_year() {
        return exp_year;
    }

    public void setExp_year(int exp_year) {
        this.exp_year = exp_year;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    @Override
    public String toString() {
        return "ModelAddCardRequest{" +
                "number='" + number + '\'' +
                ", exp_month=" + exp_month +
                ", exp_year=" + exp_year +
                ", cvv='" + cvv + '\'' +
                '}';
    }
}
