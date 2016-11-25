package com.example.mrt.ship.models;

import java.util.List;

/**
 * Created by mrt on 15/10/2016.
 */

public class Order {
    private int id;
    private String name;
    private Location from_address;
    private Location to_address;
    private String description;
    private double price;
    private double ship_cost;
    private String status;
    private List<Type> types;
    private Customer owner;


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

    public Location getFrom_address() {
        return from_address;
    }

    public void setFrom_address(Location from_address) {
        this.from_address = from_address;
    }

    public Location getTo_address() {
        return to_address;
    }

    public void setTo_address(Location to_address) {
        this.to_address = to_address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Type> getTypes() {
        return types;
    }

    public void setTypes(List<Type> types) {
        this.types = types;
    }

    public Customer getOwner() {
        return owner;
    }

    public void setOwner(Customer owner) {
        this.owner = owner;
    }
}
