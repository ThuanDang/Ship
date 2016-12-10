package com.example.mrt.ship.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mrt on 28/11/2016.
 */

public class WareHouse implements Parcelable {
    private int id;
    private String name;
    private String phone;
    private String address;
    private double latitude;
    private double longtitude;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLongitude() {
        return longtitude;
    }

    public void setLongitude(double longitude) {
        this.longtitude = longitude;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.address);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longtitude);
    }

    public WareHouse() {
    }

    protected WareHouse(Parcel in) {
        this.address = in.readString();
        this.latitude = in.readDouble();
        this.longtitude = in.readDouble();
    }

    public static final Parcelable.Creator<WareHouse> CREATOR = new Parcelable.Creator<WareHouse>() {
        @Override
        public WareHouse createFromParcel(Parcel source) {
            return new WareHouse(source);
        }

        @Override
        public WareHouse[] newArray(int size) {
            return new WareHouse[size];
        }
    };
}
