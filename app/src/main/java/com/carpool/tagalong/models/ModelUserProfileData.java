package com.carpool.tagalong.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ModelUserProfileData {

    @SerializedName("userName")
    public String userName;
    @SerializedName("email")
    public String email;
    @SerializedName("mobileNo")
    public String mobileNo;
    @SerializedName("address")
    public String address;
    @SerializedName("drove")
    public String drove;
    @SerializedName("rating")
    public String rating;
    @SerializedName("trips")
    public String trips;
    @SerializedName("profile_pic")
    public String profile_pic;
    @SerializedName("driverDetails")
    public ModelUserDriverDetails driverDetails;
    @SerializedName("paymentDetails")
    public ModelPaymentDetails paymentDetails;
    @SerializedName("documents")
    public List<ModelDocuments> documents;
    @SerializedName("gender")
    public String gender;

    @SerializedName("genderPrefrance")
    public String genderPrefrance;

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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDrove() {
        return drove;
    }

    public void setDrove(String drove) {
        this.drove = drove;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getTrips() {
        return trips;
    }

    public void setTrips(String trips) {
        this.trips = trips;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }

    public ModelUserDriverDetails getDriverDetails() {
        return driverDetails;
    }

    public void setDriverDetails(ModelUserDriverDetails driverDetails) {
        this.driverDetails = driverDetails;
    }

    public ModelPaymentDetails getPaymentDetails() {
        return paymentDetails;
    }

    public void setPaymentDetails(ModelPaymentDetails paymentDetails) {
        this.paymentDetails = paymentDetails;
    }

    public List<ModelDocuments> getDocuments() {
        return documents;
    }

    public void setDocuments(List<ModelDocuments> documents) {
        this.documents = documents;
    }

    @Override
    public String toString() {
        return "ModelUserProfileData{" +
                "userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", mobileNo='" + mobileNo + '\'' +
                ", address='" + address + '\'' +
                ", drove='" + drove + '\'' +
                ", rating='" + rating + '\'' +
                ", trips='" + trips + '\'' +
                ", profile_pic='" + profile_pic + '\'' +
                ", driverDetails=" + driverDetails +
                ", paymentDetails=" + paymentDetails +
                ", documents=" + documents +
                ", gender='" + gender + '\'' +
                ", genderPrefrance='" + genderPrefrance + '\'' +
                '}';
    }
}
