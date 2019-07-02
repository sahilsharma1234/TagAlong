package com.carpool.tagalong.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ModelTagUsers {

    @SerializedName("requestId")
    public String requestId;
    @SerializedName("userContacts")
    public List<String> userContacts;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public List<String> getUserContacts() {
        return userContacts;
    }

    public void setUserContacts(List<String> userContacts) {
        this.userContacts = userContacts;
    }

    @Override
    public String toString() {
        return "ModelTagUsers{" +
                "requestId='" + requestId + '\'' +
                ", userContacts=" + userContacts +
                '}';
    }
}
