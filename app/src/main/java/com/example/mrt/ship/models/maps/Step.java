package com.example.mrt.ship.models.maps;


/**
 * Created by mrt on 03/12/2016.
 */
public class Step {
    private TextValue distance;
    private TextValue duration;
    private Location end_location;
    private String html_instructions;
    private OverViewPolyLine polyline;
    private Location start_location;
    private String travel_mode;

    public TextValue getDistance() {
        return distance;
    }

    public void setDistance(TextValue distance) {
        this.distance = distance;
    }

    public TextValue getDuration() {
        return duration;
    }

    public void setDuration(TextValue duration) {
        this.duration = duration;
    }

    public Location getEnd_location() {
        return end_location;
    }

    public void setEnd_location(Location end_location) {
        this.end_location = end_location;
    }

    public String getHtml_instructions() {
        return html_instructions;
    }

    public void setHtml_instructions(String html_instructions) {
        this.html_instructions = html_instructions;
    }

    public OverViewPolyLine getPolyline() {
        return polyline;
    }

    public void setPolyline(OverViewPolyLine polyline) {
        this.polyline = polyline;
    }

    public Location getStart_location() {
        return start_location;
    }

    public void setStart_location(Location start_location) {
        this.start_location = start_location;
    }

    public String getTravel_mode() {
        return travel_mode;
    }

    public void setTravel_mode(String travel_mode) {
        this.travel_mode = travel_mode;
    }
}
