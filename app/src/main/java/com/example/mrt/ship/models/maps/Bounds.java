package com.example.mrt.ship.models.maps;

/**
 * Created by mrt on 03/12/2016.
 */
public class Bounds {
    private Location northeast;
    private Location southwest;

    public Location getNortheast() {
        return northeast;
    }

    public void setNortheast(Location northeast) {
        this.northeast = northeast;
    }

    public Location getSouthwest() {
        return southwest;
    }

    public void setSouthwest(Location southwest) {
        this.southwest = southwest;
    }
}
