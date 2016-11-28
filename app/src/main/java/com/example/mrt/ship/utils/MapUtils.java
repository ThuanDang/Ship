package com.example.mrt.ship.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.animation.BounceInterpolator;

import com.example.mrt.ship.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by mrt on 24/11/2016.
 */

public class MapUtils {
    private static AlertDialog dialog;

    /**
     * Check if GPS is off, require user turn on GPS to pass
     * @param context
     */
    public static void checkGPS(final Context context){
        LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);

        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) { }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {

            // notify user
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setCancelable(false);
            builder.setTitle(context.getResources().getString(R.string.gps_network_not_enabled));
            builder.setMessage("Bật GPS cho phép bạn tìm thấy những đơn hàng xung quanh vị trí của mình.");
            builder.setPositiveButton(context.getResources().getString(R.string.open_location_settings)
                    , new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            // TODO Auto-generated method stub
                            Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            context.startActivity(myIntent);
                            //get gps
                        }
                    });
            builder.setNegativeButton(context.getString(R.string.Cancel),
                    new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    paramDialogInterface.cancel();
                }
            });
            dialog = builder.create();
            dialog.show();
        }

    }


    public static void cancelDialog(){
        if(dialog != null){
            dialog.cancel();
        }
    }

    /**
     * Bounce animation of marker when appear on map
     * @param marker
     */
    public static void dropPinEffect(final Marker marker) {

        // Handler allows us to repeat a code block after a specified delay
        final android.os.Handler handler = new android.os.Handler();
        final long start = SystemClock.uptimeMillis();
        final long duration = 1500;

        // Use the bounce interpolator
        final android.view.animation.Interpolator interpolator = new BounceInterpolator();

        // Animate marker with a bounce updating its position every 15ms
        handler.post(new Runnable() {

            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;

                // Calculate t for bounce based on elapsed time
                float t = Math.max(1 - interpolator.getInterpolation((float)elapsed/duration)
                        , 0);

                // Set the anchor
                marker.setAnchor(0.5f, 1.0f + 14 * t);

                if (t > 0.0) {
                    // Post this event again 15ms from now.
                    handler.postDelayed(this, 15);
                } else {
                    // done elapsing, show window
                    marker.showInfoWindow();
                }
            }
        });

    }

    public static String getAddressFromLocation(Context context, Location location){
        List<Address> addresses = null;
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(location.getLatitude(),
                    location.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(addresses != null){
            // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            String address = addresses.get(0).getAddressLine(0);
            // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            /*String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL*/
            return address + "," + city;
        }

        return "";
    }

    public static boolean GPSisON(Context context){
        LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}
        return gps_enabled;
    }
}
