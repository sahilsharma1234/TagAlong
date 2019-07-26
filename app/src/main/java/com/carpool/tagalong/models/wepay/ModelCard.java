package com.carpool.tagalong.models.wepay;

import com.google.gson.annotations.SerializedName;

public class ModelCard {

    @SerializedName("wepay_card_id")
    public String wepay_card_id;

    public String getWepay_card_id() {
        return wepay_card_id;
    }

    public void setWepay_card_id(String wepay_card_id) {
        this.wepay_card_id = wepay_card_id;
    }


    @Override
    public String toString() {
        return "ModelCard{" +
                "wepay_card_id='" + wepay_card_id + '\'' +
                '}';
    }
}
