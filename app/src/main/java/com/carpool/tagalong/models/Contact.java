package com.carpool.tagalong.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sahilsharma on 31/1/18.
 */

public class Contact implements Parcelable {

    public String id,name,phone,label,email;
    public boolean isSelected  = false;

    public Contact(String id, String name, String phone, String label, String email){
        this.id    = id;
        this.name  = name;
        this.phone = phone;
        this.label = label;
        this.email = email;
    }

    protected Contact(Parcel in) {
        isSelected = in.readByte() != 0x00;
        id    = in.readString();
        name  = in.readString();
        phone = in.readString();
        label = in.readString();
        email = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (isSelected ? 0x01 : 0x00));
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(phone);
        dest.writeString(label);
        dest.writeString(email);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Contact> CREATOR = new Parcelable.Creator<Contact>() {

        @Override
        public Contact createFromParcel(Parcel in) {
            return new Contact(in);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}