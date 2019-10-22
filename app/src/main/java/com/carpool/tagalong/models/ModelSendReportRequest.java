package com.carpool.tagalong.models;

import com.google.gson.annotations.SerializedName;

public class ModelSendReportRequest {


    @SerializedName("rideId")
    public String rideId;
    @SerializedName("reportTitle")
    public String reportTitle;
    @SerializedName("reportDescription")
    public String reportDescription;
    @SerializedName("startLocation")
    public String startLocation;
    @SerializedName("endLocation")
    public String endLocation;

    public String getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }

    public String getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(String endLocation) {
        this.endLocation = endLocation;
    }

    public String getRideId() {
        return rideId;
    }

    public void setRideId(String rideId) {
        this.rideId = rideId;
    }

    public String getReportTitle() {
        return reportTitle;
    }

    public void setReportTitle(String reportTitle) {
        this.reportTitle = reportTitle;
    }

    public String getReportDescription() {
        return reportDescription;
    }

    public void setReportDescription(String reportDescription) {
        this.reportDescription = reportDescription;
    }
}
