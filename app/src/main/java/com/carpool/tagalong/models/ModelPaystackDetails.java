package com.carpool.tagalong.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ModelPaystackDetails implements Serializable {


    @SerializedName("businessName")
    public String businessName;
    @SerializedName("bankName")
    public String bankName;
    @SerializedName("accountNumber")
    public String accountNumber;
    @SerializedName("subAccountId")
    public String subAccountId;

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getSubAccountId() {
        return subAccountId;
    }

    public void setSubAccountId(String subAccountId) {
        this.subAccountId = subAccountId;
    }


    @Override
    public String toString() {
        return "ModelPaystackDetails{" +
                "businessName='" + businessName + '\'' +
                ", bankName='" + bankName + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", subAccountId='" + subAccountId + '\'' +
                '}';
    }
}
