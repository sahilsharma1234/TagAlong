package com.carpool.tagalong.models.chat;

import com.google.gson.annotations.SerializedName;

public class ChatSendMessageRequest {


    @SerializedName("receiver")
    public String receiver;
    @SerializedName("msg")
    public String msg;

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "ChatSendMessageRequest{" +
                "receiver='" + receiver + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }
}
