package com.carpool.tagalong.models;

import com.carpool.tagalong.models.wepay.CreditCards;
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
    @SerializedName("zipcode")
    public String zipcode;
    @SerializedName("driverDetails")
    public ModelUserDriverDetails driverDetails;
    @SerializedName("paymentDetails")
    public ModelPaymentDetails paymentDetails;

    @SerializedName("wepayDetails")
    public ModelWePayDetails wepayDetails;

    @SerializedName("documents")
    public List<ModelDocuments> documents;
    @SerializedName("gender")
    public String gender;
    @SerializedName("checkrStatus")
    public String checkrStatus;
    @SerializedName("card")
    public List<CreditCards> card;
    @SerializedName("genderPrefrance")
    public String genderPrefrance;
    @SerializedName("_id")
    public String _id;
    @SerializedName("dob")
    public String dob;
    @SerializedName("ssn")
    public String ssn;
    @SerializedName("last_name")
    public String last_name;

    public ModelWePayDetails getWepayDetails() {
        return wepayDetails;
    }

    public void setWepayDetails(ModelWePayDetails wepayDetails) {
        this.wepayDetails = wepayDetails;
    }

    public String getCheckrStatus() {
        return checkrStatus;
    }

    public void setCheckrStatus(String checkrStatus) {
        this.checkrStatus = checkrStatus;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
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

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public List<CreditCards> getCard() {
        return card;
    }

    public void setCard(List<CreditCards> card) {
        this.card = card;
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
                ", zipcode='" + zipcode + '\'' +
                ", driverDetails=" + driverDetails +
                ", paymentDetails=" + paymentDetails +
                ", wepayDetails=" + wepayDetails +
                ", documents=" + documents +
                ", gender='" + gender + '\'' +
                ", checkrStatus='" + checkrStatus + '\'' +
                ", card=" + card +
                ", genderPrefrance='" + genderPrefrance + '\'' +
                ", _id='" + _id + '\'' +
                ", dob='" + dob + '\'' +
                ", ssn='" + ssn + '\'' +
                ", last_name='" + last_name + '\'' +
                '}';
    }

    public static class Card {
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
    }

    public static class DriverDetails {
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
        public String vehicleYear;
        @SerializedName("vehicleColor")
        public String vehicleColor;
    }

    public static class PaymentDetails {
        @SerializedName("accountNumber")
        public String accountNumber;
        @SerializedName("shortCode")
        public String shortCode;
        @SerializedName("bankName")
        public String bankName;
        @SerializedName("routingNumber")
        public String routingNumber;
    }

    public static class Documents {
    }
}
