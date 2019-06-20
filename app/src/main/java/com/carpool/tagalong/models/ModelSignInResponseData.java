package com.carpool.tagalong.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ModelSignInResponseData {


    @SerializedName("_id")
    public String _id;
    @SerializedName("profile_pic")
    public String profile_pic;
    @SerializedName("latitude")
    public int latitude;
    @SerializedName("longitude")
    public int longitude;
    @SerializedName("deviceToken")
    public String deviceToken;
    @SerializedName("verifyMobile")
    public boolean verifyMobile;
    @SerializedName("verifyEmail")
    public boolean verifyEmail;
    @SerializedName("smoke")
    public String smoke;
    @SerializedName("bags")
    public String bags;
    @SerializedName("allowKids")
    public boolean allowKids;
    @SerializedName("vehicle")
    public String vehicle;
    @SerializedName("vehicleNumber")
    public String vehicleNumber;
    @SerializedName("accountNumber")
    public String accountNumber;
    @SerializedName("shortCode")
    public String shortCode;
    @SerializedName("bankName")
    public String bankName;
    @SerializedName("status")
    public int status;
    @SerializedName("userName")
    public String userName;
    @SerializedName("address")
    public String address;
    @SerializedName("mobileNo")
    public String mobileNo;
    @SerializedName("email")
    public String email;
    @SerializedName("documents")
    public List<ModelDocuments> documents;
    @SerializedName("createdAt")
    public String createdAt;
    @SerializedName("updatedAt")
    public String updatedAt;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }

    public int getLatitude() {
        return latitude;
    }

    public void setLatitude(int latitude) {
        this.latitude = latitude;
    }

    public int getLongitude() {
        return longitude;
    }

    public void setLongitude(int longitude) {
        this.longitude = longitude;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public boolean isVerifyMobile() {
        return verifyMobile;
    }

    public void setVerifyMobile(boolean verifyMobile) {
        this.verifyMobile = verifyMobile;
    }

    public boolean isVerifyEmail() {
        return verifyEmail;
    }

    public void setVerifyEmail(boolean verifyEmail) {
        this.verifyEmail = verifyEmail;
    }

    public String getSmoke() {
        return smoke;
    }

    public void setSmoke(String smoke) {
        this.smoke = smoke;
    }

    public String getBags() {
        return bags;
    }

    public void setBags(String bags) {
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public List<ModelDocuments> getDocuments() {
        return documents;
    }

    public void setDocuments(List<ModelDocuments> documents) {
        this.documents = documents;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "ModelSignInResponseData{" +
                "_id='" + _id + '\'' +
                ", profile_pic='" + profile_pic + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", deviceToken='" + deviceToken + '\'' +
                ", verifyMobile=" + verifyMobile +
                ", verifyEmail=" + verifyEmail +
                ", smoke='" + smoke + '\'' +
                ", bags='" + bags + '\'' +
                ", allowKids=" + allowKids +
                ", vehicle='" + vehicle + '\'' +
                ", vehicleNumber='" + vehicleNumber + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", shortCode='" + shortCode + '\'' +
                ", bankName='" + bankName + '\'' +
                ", status=" + status +
                ", userName='" + userName + '\'' +
                ", address='" + address + '\'' +
                ", mobileNo='" + mobileNo + '\'' +
                ", email='" + email + '\'' +
                ", documents=" + documents +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }
}
