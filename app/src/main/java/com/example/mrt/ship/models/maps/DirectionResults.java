package com.example.mrt.ship.models.maps;


import java.util.ArrayList;


/**
 * Created by mrt on 03/12/2016.
 */
public class DirectionResults {
    private ArrayList<GeocodedWayPoint> geocoded_waypoints;
    private ArrayList<Route> routes;
    private String status;

    public ArrayList<GeocodedWayPoint> getGeocoded_waypoints() {
        return geocoded_waypoints;
    }

    public void setGeocoded_waypoints(ArrayList<GeocodedWayPoint> geocoded_waypoints) {
        this.geocoded_waypoints = geocoded_waypoints;
    }

    public ArrayList<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(ArrayList<Route> routes) {
        this.routes = routes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
