package com.example.mrt.ship.models;

/**
 * Created by mrt on 28/10/2016.
 */

public class Type {
    private int id;
    private String name;
    private int code;
    private double cost;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }
}
