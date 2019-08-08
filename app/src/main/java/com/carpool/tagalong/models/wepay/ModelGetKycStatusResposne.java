package com.carpool.tagalong.models.wepay;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ModelGetKycStatusResposne {

    @SerializedName("account_id")
    public int account_id;
    @SerializedName("state")
    public String state;
    @SerializedName("supporting_documents")
    public List<String> supporting_documents;
    @SerializedName("legal_name")
    public Legal_name legal_name;

    public static class Supporting_documents {
    }

    public static class Legal_name {
        @SerializedName("first")
        public String first;
        @SerializedName("last")
        public String last;

        public String getFirst() {
            return first;
        }

        public void setFirst(String first) {
            this.first = first;
        }

        public String getLast() {
            return last;
        }

        public void setLast(String last) {
            this.last = last;
        }

        @Override
        public String toString() {
            return "Legal_name{" +
                    "first='" + first + '\'' +
                    ", last='" + last + '\'' +
                    '}';
        }
    }

    public int getAccount_id() {
        return account_id;
    }

    public void setAccount_id(int account_id) {
        this.account_id = account_id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<String> getSupporting_documents() {
        return supporting_documents;
    }

    public void setSupporting_documents(List<String> supporting_documents) {
        this.supporting_documents = supporting_documents;
    }

    public Legal_name getLegal_name() {
        return legal_name;
    }

    public void setLegal_name(Legal_name legal_name) {
        this.legal_name = legal_name;
    }

    @Override
    public String toString() {
        return "ModelGetKycStatusResposne{" +
                "account_id=" + account_id +
                ", state='" + state + '\'' +
                ", supporting_documents=" + supporting_documents +
                ", legal_name=" + legal_name +
                '}';
    }
}
