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
    @SerializedName("vehicleBrand")
    public String vehicleBrand;
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
    @SerializedName("last_name")
    public String last_name;
    @SerializedName("dob")
    public String dob;
    @SerializedName("ssn")
    public String ssn;
    @SerializedName("zipcode")
    public int zipcode;
    @SerializedName("gender")
    public String gender;
    @SerializedName("genderPrefrance")
    public String genderPrefrance;
    @SerializedName("checkrStatus")
    public String   checkrStatus;
    @SerializedName("city")
    public String city;
    @SerializedName("region")
    public String region;
    @SerializedName("original_ip")
    public String original_ip;
    @SerializedName("original_device")
    public String original_device;

    @SerializedName("driver_license_number")
    public String driver_license_number;

    @SerializedName("driver_license_state")
    public String driver_license_state;

    @SerializedName("payStack")
    public ModelPaystackDetails payStack;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getOriginal_ip() {
        return original_ip;
    }

    public void setOriginal_ip(String original_ip) {
        this.original_ip = original_ip;
    }

    public String getOriginal_device() {
        return original_device;
    }

    public void setOriginal_device(String original_device) {
        this.original_device = original_device;
    }

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

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
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

    public int getZipcode() {
        return zipcode;
    }

    public void setZipcode(int zipcode) {
        this.zipcode = zipcode;
    }

    public String getCheckrStatus() {
        return checkrStatus;
    }

    public void setCheckrStatus(String checkrStatus) {
        this.checkrStatus = checkrStatus;
    }

    public String getVehicleBrand() {
        return vehicleBrand;
    }

    public void setVehicleBrand(String vehicleBrand) {
        this.vehicleBrand = vehicleBrand;
    }

    public String getVehicle() {
        return vehicle;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }

    public String getDriver_license_number() {
        return driver_license_number;
    }

    public void setDriver_license_number(String driver_license_number) {
        this.driver_license_number = driver_license_number;
    }

    public String getDriver_license_state() {
        return driver_license_state;
    }

    public void setDriver_license_state(String driver_license_state) {
        this.driver_license_state = driver_license_state;
    }

    public ModelPaystackDetails getPayStack() {
        return payStack;
    }

    public void setPayStack(ModelPaystackDetails payStack) {
        this.payStack = payStack;
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
                ", vehicleBrand='" + vehicleBrand + '\'' +
                ", vehicle='" + vehicle + '\'' +
                ", vehicleNumber='" + vehicleNumber + '\'' +
                ", vehicleYear=" + vehicleYear +
                ", vehicleColor='" + vehicleColor + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", routingNumber='" + routingNumber + '\'' +
                ", shortCode='" + shortCode + '\'' +
                ", bankName='" + bankName + '\'' +
                ", last_name='" + last_name + '\'' +
                ", dob='" + dob + '\'' +
                ", ssn='" + ssn + '\'' +
                ", zipcode=" + zipcode +
                ", gender='" + gender + '\'' +
                ", genderPrefrance='" + genderPrefrance + '\'' +
                ", checkrStatus='" + checkrStatus + '\'' +
                ", city='" + city + '\'' +
                ", region='" + region + '\'' +
                ", original_ip='" + original_ip + '\'' +
                ", original_device='" + original_device + '\'' +
                ", driver_license_number='" + driver_license_number + '\'' +
                ", driver_license_state='" + driver_license_state + '\'' +
                ", payStack=" + payStack +
                '}';
    }
}
