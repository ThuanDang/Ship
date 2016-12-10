package com.example.mrt.ship.models;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mrt on 15/10/2016.
 */

public class Order implements Parcelable {

    private int id;
    private String name;
    private int status;
    private int type;
    private double ship_cost;
    private double price;
    private WareHouse ware_house;
    private Recipient recipient;
    private Commodity commodities;
    private Customer customer;

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public WareHouse getWare_house() {
        return ware_house;
    }

    public void setWare_house(WareHouse ware_house) {
        this.ware_house = ware_house;
    }

    public Recipient getRecipient() {
        return recipient;
    }

    public void setRecipient(Recipient recipient) {
        this.recipient = recipient;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getShip_cost() {
        return ship_cost;
    }

    public void setShip_cost(double ship_cost) {
        this.ship_cost = ship_cost;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Commodity getCommodities() {
        return commodities;
    }

    public void setCommodities(Commodity commodities) {
        this.commodities = commodities;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeInt(this.status);
        dest.writeInt(this.type);
        dest.writeDouble(this.ship_cost);
        dest.writeDouble(this.price);
        dest.writeParcelable(this.ware_house, flags);
        dest.writeParcelable(this.recipient, flags);
        dest.writeParcelable(this.commodities, flags);
        dest.writeParcelable(this.customer, flags);
    }

    public Order() {
    }

    protected Order(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.status = in.readInt();
        this.type = in.readInt();
        this.ship_cost = in.readDouble();
        this.price = in.readDouble();
        this.ware_house = in.readParcelable(WareHouse.class.getClassLoader());
        this.recipient = in.readParcelable(Recipient.class.getClassLoader());
        this.commodities = in.readParcelable(Commodity.class.getClassLoader());
        this.customer = in.readParcelable(Customer.class.getClassLoader());
    }

    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel source) {
            return new Order(source);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };
}



