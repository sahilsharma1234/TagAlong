package com.carpool.tagalong.models.emergencysos;

import com.google.gson.annotations.SerializedName;

public class ModelUpdateCoordinates {


    @SerializedName("latitude")
    public double latitude;
    @SerializedName("longitude")
    public double longitude;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "ModelUpdateCoordinates{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
