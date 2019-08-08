package com.carpool.tagalong.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ModelGetNearbyDriversResponse {


    @SerializedName("status")
    public int status;
    @SerializedName("message")
    public String message;
    @SerializedName("data")
    public List<Data> data;

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

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ModelGetNearbyDriversResponse{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }

    public static class Location {
        @SerializedName("type")
        public String type;
        @SerializedName("coordinates")
        public List<Double> coordinates;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public List<Double> getCoordinates() {
            return coordinates;
        }

        public void setCoordinates(List<Double> coordinates) {
            this.coordinates = coordinates;
        }

        @Override
        public String toString() {
            return "Location{" +
                    "type='" + type + '\'' +
                    ", coordinates=" + coordinates +
                    '}';
        }
    }

    public static class Data {
        @SerializedName("location")
        public Location location;
        @SerializedName("_id")
        public String _id;

        public Location getLocation() {
            return location;
        }

        public void setLocation(Location location) {
            this.location = location;
        }

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "location=" + location +
                    ", _id='" + _id + '\'' +
                    '}';
        }
    }
}
