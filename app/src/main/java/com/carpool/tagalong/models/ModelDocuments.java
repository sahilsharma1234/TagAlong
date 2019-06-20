package com.carpool.tagalong.models;

import com.google.gson.annotations.SerializedName;

public class ModelDocuments {

    @SerializedName("_id")
    public String _id;
    @SerializedName("url")
    public String url;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "ModelDocuments{" +
                "_id='" + _id + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
