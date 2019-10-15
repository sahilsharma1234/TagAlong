package com.carpool.tagalong.models;

import com.google.gson.annotations.SerializedName;

public class ModelSendReportRequest {


    @SerializedName("rideId")
    public String rideId;
    @SerializedName("reportTitle")
    public String reportTitle;
    @SerializedName("reportDescription")
    public String reportDescription;

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
