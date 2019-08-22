package com.carpool.tagalong.models;

import com.google.gson.annotations.SerializedName;

public class ModelGetEstimatedFareResponse {


    @SerializedName("status")
    public int status;
    @SerializedName("message")
    public String message;
    @SerializedName("data")
    public Data data;

    public static class Data {
        @SerializedName("estimatedFare")
        public double estimatedFare;
        @SerializedName("estimatedTimeToReachDestination")
        public String estimatedTimeToReachDestination;

        public double getEstimatedFare() {
            return estimatedFare;
        }

        public void setEstimatedFare(double estimatedFare) {
            this.estimatedFare = estimatedFare;
        }

        public String getEstimatedTimeToReachDestination() {
            return estimatedTimeToReachDestination;
        }

        public void setEstimatedTimeToReachDestination(String estimatedTimeToReachDestination) {
            this.estimatedTimeToReachDestination = estimatedTimeToReachDestination;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "estimatedFare=" + estimatedFare +
                    ", estimatedTimeToReachDestination='" + estimatedTimeToReachDestination + '\'' +
                    '}';
        }
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ModelGetEstimatedFareResponse{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
