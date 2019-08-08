package com.carpool.tagalong.models.wepay;

import com.google.gson.annotations.SerializedName;

public class ModelAddMerchantBankDetailsRequest {

    @SerializedName("bankName")
    public String bankName;
    @SerializedName("routingNumber")
    public String routingNumber;
    @SerializedName("accountNumber")
    public String accountNumber;
    @SerializedName("accountType")
    public String accountType;

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

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    @Override
    public String toString() {
        return "ModelAddMerchantBankDetailsRequest{" +
                "bankName='" + bankName + '\'' +
                ", routingNumber='" + routingNumber + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", accountType='" + accountType + '\'' +
                '}';
    }
}