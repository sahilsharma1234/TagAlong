package com.carpool.tagalong.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CountryData implements Serializable {

    @SerializedName("_id")
    public String _id;
    @SerializedName("code")
    public String code;
    @SerializedName("name")
    public String name;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "CountryData{" +
                "_id='" + _id + '\'' +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
