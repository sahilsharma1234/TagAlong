package com.carpool.tagalong.models;

import com.google.gson.annotations.SerializedName;

public class ModelUpdateProfileRequest {


    @SerializedName("userName")
    public String userName;
    @SerializedName("mobileNo")
    public String mobileNo;
    @SerializedName("email")
    public String email;
    @SerializedName("address")
    public String address;
    @SerializedName("smoke")
    public boolean smoke;
    @SerializedName("bags")
    public int bags;
    @SerializedName("allowKids")
    public boolean allowKids;
    @SerializedName("vehicle")
    public String vehicle;
    @SerializedName("vehicleNumber")
    public String vehicleNumber;
    @SerializedName("vehicleYear")
    public int vehicleYear;
    @SerializedName("vehicleColor")
    public String vehicleColor;
    @SerializedName("accountNumber")
    public String accountNumber;
    @SerializedName("routingNumber")
    public String routingNumber;
    @SerializedName("shortCode")
    public String shortCode;
    @SerializedName("bankName")
    public String bankName;

    @SerializedName("gender")
    public String gender;

    @SerializedName("genderPrefrance")
    public String genderPrefrance;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isSmoke() {
        return smoke;
    }

    public void setSmoke(boolean smoke) {
        this.smoke = smoke;
    }

    public int getBags() {
        return bags;
    }

    public void setBags(int bags) {
        this.bags = bags;
    }

    public boolean isAllowKids() {
        return allowKids;
    }

    public void setAllowKids(boolean allowKids) {
        this.allowKids = allowKids;
    }

    public String getVehicle() {
        return vehicle;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public int getVehicleYear() {
        return vehicleYear;
    }

    public void setVehicleYear(int vehicleYear) {
        this.vehicleYear = vehicleYear;
    }

    public String getVehicleColor() {
        return vehicleColor;
    }

    public void setVehicleColor(String vehicleColor) {
        this.vehicleColor = vehicleColor;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getRoutingNumber() {
        return routingNumber;
    }

    public void setRoutingNumber(String routingNumber) {
        this.routingNumber = routingNumber;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getGenderPrefrance() {
        return genderPrefrance;
    }

    public void setGenderPrefrance(String genderPrefrance) {
        this.genderPrefrance = genderPrefrance;
    }

    @Override
    public String toString() {
        return "ModelUpdateProfileRequest{" +
                "userName='" + userName + '\'' +
                ", mobileNo='" + mobileNo + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", smoke=" + smoke +
                ", bags=" + bags +
                ", allowKids=" + allowKids +
                ", vehicle='" + vehicle + '\'' +
                ", vehicleNumber='" + vehicleNumber + '\'' +
                ", vehicleYear=" + vehicleYear +
                ", vehicleColor='" + vehicleColor + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", routingNumber='" + routingNumber + '\'' +
                ", shortCode='" + shortCode + '\'' +
                ", bankName='" + bankName + '\'' +
                ", gender='" + gender + '\'' +
                ", genderPrefrance='" + genderPrefrance + '\'' +
                '}';
    }
}
