package com.example.mrt.ship.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mrt on 25/11/2016.
 */

public class Commodity implements Parcelable {
    private int id;
    private String name;
    private int count;
    private int price;
    private int weigh;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWeigh() {
        return weigh;
    }

    public void setWeigh(int weigh) {
        this.weigh = weigh;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeInt(this.count);
        dest.writeInt(this.price);
        dest.writeInt(this.weigh);
    }

    public Commodity() {
    }

    protected Commodity(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.count = in.readInt();
        this.price = in.readInt();
        this.weigh = in.readInt();
    }

    public static final Parcelable.Creator<Commodity> CREATOR = new Parcelable.Creator<Commodity>() {
        @Override
        public Commodity createFromParcel(Parcel source) {
            return new Commodity(source);
        }

        @Override
        public Commodity[] newArray(int size) {
            return new Commodity[size];
        }
    };
}
