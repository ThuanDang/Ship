package com.example.mrt.ship.models.maps;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by mrt on 06/12/2016.
 */

public class Path {
    private List<List<LatLng>> list;
    private double duration;
    private double distance;

    public List<List<LatLng>> getList() {
        return list;
    }

    public void setList(List<List<LatLng>> list) {
        this.list = list;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
