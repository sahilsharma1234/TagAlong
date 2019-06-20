package com.carpool.tagalong.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ModelGetCarBrandModelResponse {

    @SerializedName("status")
    public int status;
    @SerializedName("message")
    public String message;
    @SerializedName("data")
    public List<ModelGetCarBrandModelResponseData> data;

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

    public List<ModelGetCarBrandModelResponseData> getData() {
        return data;
    }

    public void setData(List<ModelGetCarBrandModelResponseData> data) {
        this.data = data;
    }

    public class Models {
        @SerializedName("_id")
        public String _id;
        @SerializedName("name")
        public String name;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "Models{" +
                    "_id='" + _id + '\'' +
                    ", name='" + name + '\'' +
                    '}';
        }
    }

    public class ModelGetCarBrandModelResponseData {
        @SerializedName("_id")
        public String _id;
        @SerializedName("carBrand")
        public String carBrand;
        @SerializedName("models")
        public List<Models> models;
        @SerializedName("createdAt")
        public String createdAt;
        @SerializedName("updatedAt")
        public String updatedAt;
        @SerializedName("__v")
        public int __v;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getCarBrand() {
            return carBrand;
        }

        public void setCarBrand(String carBrand) {
            this.carBrand = carBrand;
        }

        public List<Models> getModels() {
            return models;
        }

        public void setModels(List<Models> models) {
            this.models = models;
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

        public int get__v() {
            return __v;
        }

        public void set__v(int __v) {
            this.__v = __v;
        }

        @Override
        public String toString() {
            return "ModelGetCarBrandModelResponseData{" +
                    "_id='" + _id + '\'' +
                    ", carBrand='" + carBrand + '\'' +
                    ", models=" + models +
                    ", createdAt='" + createdAt + '\'' +
                    ", updatedAt='" + updatedAt + '\'' +
                    ", __v=" + __v +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "ModelGetCarBrandModelResponse{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
