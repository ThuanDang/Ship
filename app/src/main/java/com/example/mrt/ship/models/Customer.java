package com.example.mrt.ship.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mrt on 24/10/2016.
 */
public class Customer implements Parcelable {
    private int id;
    private User users;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUsers() {
        return users;
    }

    public void setUsers(User users) {
        this.users = users;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeParcelable(this.users, flags);
    }

    public Customer() {
    }

    protected Customer(Parcel in) {
        this.id = in.readInt();
        this.users = in.readParcelable(User.class.getClassLoader());
    }

    public static final Parcelable.Creator<Customer> CREATOR = new Parcelable.Creator<Customer>() {
        @Override
        public Customer createFromParcel(Parcel source) {
            return new Customer(source);
        }

        @Override
        public Customer[] newArray(int size) {
            return new Customer[size];
        }
    };
}
