package com.example.mrt.ship.models.maps;

/**
 * Created by mrt on 03/12/2016.
 */
public class GeocodedWayPoint {
    private String geocoder_status;
    private String place_id;
    private String[] types;

    public String getGeocoder_status() {
        return geocoder_status;
    }

    public void setGeocoder_status(String geocoder_status) {
        this.geocoder_status = geocoder_status;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public String[] getTypes() {
        return types;
    }

    public void setTypes(String[] types) {
        this.types = types;
    }
}
