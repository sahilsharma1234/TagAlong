package com.carpool.tagalong.models.chat;

import com.google.gson.annotations.SerializedName;

public class ModelGetChatRequest {


    @SerializedName("receiver")
    public String receiver;

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }
}
