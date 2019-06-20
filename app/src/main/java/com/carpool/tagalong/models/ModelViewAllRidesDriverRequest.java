package com.carpool.tagalong.models;

import com.google.gson.annotations.SerializedName;

public class ModelViewAllRidesDriverRequest {

    @SerializedName("userId")
    public String userId;
    @SerializedName("page")
    public int page;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    @Override
    public String toString() {
        return "ModelViewAllRidesDriverRequest{" +
                "userId='" + userId + '\'' +
                ", page=" + page +
                '}';
    }
}
