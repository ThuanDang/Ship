package com.example.mrt.ship.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.mrt.ship.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by mrt on 24/11/2016.
 */

public class CustomWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private LayoutInflater inflater;

    public CustomWindowAdapter(LayoutInflater inflater){
        this.inflater = inflater;
    }


    // This defines the contents within the info window based on the marker
    @Override
    public View getInfoContents(Marker marker) {

        // Getting view from the layout file
        View v = inflater.inflate(R.layout.custom_marker_info, null);

        // Populate fields
        TextView title = (TextView) v.findViewById(R.id.title);
        title.setText(marker.getTitle());

        TextView description = (TextView) v.findViewById(R.id.description);
        description.setText(marker.getSnippet());


        return v;
    }


    // This changes the frame of the info window; returning null uses the default frame.
    // This is just the border and arrow surrounding the contents specified above
    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

}