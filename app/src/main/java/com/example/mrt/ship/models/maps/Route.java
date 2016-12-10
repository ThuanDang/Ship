package com.example.mrt.ship.models.maps;


import java.util.ArrayList;

/**
 * Created by mrt on 03/12/2016.
 */
public class Route {
    private Bounds bounds;
    private String copyrights;
    private ArrayList<Leg> legs;
    private String summary;
    private String[] warnings;
    private int[] waypoint_order;
    private OverViewPolyLine overview_polyline;

    public OverViewPolyLine getOverview_polyline() {
        return overview_polyline;
    }

    public void setOverview_polyline(OverViewPolyLine overview_polyline) {
        this.overview_polyline = overview_polyline;
    }

    public int[] getWaypoint_order() {
        return waypoint_order;
    }

    public void setWaypoint_order(int[] waypoint_order) {
        this.waypoint_order = waypoint_order;
    }

    public String[] getWarnings() {
        return warnings;
    }

    public void setWarnings(String[] warnings) {
        this.warnings = warnings;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Bounds getBounds() {
        return bounds;
    }

    public void setBounds(Bounds bounds) {
        this.bounds = bounds;
    }

    public String getCopyrights() {
        return copyrights;
    }

    public void setCopyrights(String copyrights) {
        this.copyrights = copyrights;
    }

    public ArrayList<Leg> getLegs() {
        return legs;
    }

    public void setLegs(ArrayList<Leg> legs) {
        this.legs = legs;
    }
}
