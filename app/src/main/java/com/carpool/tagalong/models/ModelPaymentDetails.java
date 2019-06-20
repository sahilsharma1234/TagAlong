package com.carpool.tagalong.models;

import com.google.gson.annotations.SerializedName;

public class ModelPaymentDetails {
    @SerializedName("accountNumber")
    public String accountNumber;
    @SerializedName("shortCode")
    public String shortCode;
    @SerializedName("bankName")
    public String bankName;
    @SerializedName("routingNumber")
    public String routingNumber;

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getRoutingNumber() {
        return routingNumber;
    }

    public void setRoutingNumber(String routingNumber) {
        this.routingNumber = routingNumber;
    }

    @Override
    public String toString() {
        return "ModelPaymentDetails{" +
                "accountNumber='" + accountNumber + '\'' +
                ", shortCode='" + shortCode + '\'' +
                ", bankName='" + bankName + '\'' +
                ", routingNumber='" + routingNumber + '\'' +
                '}';
    }
}
