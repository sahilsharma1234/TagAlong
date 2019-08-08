package com.carpool.tagalong.models;

import com.google.gson.annotations.SerializedName;

public class ModelUserDriverDetails {

    @SerializedName("smoke")
    public boolean smoke;
    @SerializedName("bags")
    public int bags;
    @SerializedName("allowKids")
    public boolean allowKids;
    @SerializedName("vehicleBrand")
    public String vehicleBrand;
    @SerializedName("vehicleModel")
    public String vehicleModel;
    @SerializedName("vehicleNumber")
    public String vehicleNumber;
    @SerializedName("vehicleYear")
    public int vehicleYear;
    @SerializedName("vehicleColor")
    public String vehicleColor;

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

    public String getVehicleBrand() {
        return vehicleBrand;
    }

    public void setVehicleBrand(String vehicleBrand) {
        this.vehicleBrand = vehicleBrand;
    }

    public String getVehicleModel() {
        return vehicleModel;
    }

    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
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

    @Override
    public String toString() {
        return "ModelUserDriverDetails{" +
                "smoke=" + smoke +
                ", bags=" + bags +
                ", allowKids=" + allowKids +
                ", vehicleBrand='" + vehicleBrand + '\'' +
                ", vehicleModel='" + vehicleModel + '\'' +
                ", vehicleNumber='" + vehicleNumber + '\'' +
                ", vehicleYear=" + vehicleYear +
                ", vehicleColor='" + vehicleColor + '\'' +
                '}';
    }
}
