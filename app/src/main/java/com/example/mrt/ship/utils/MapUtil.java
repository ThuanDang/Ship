package com.example.mrt.ship.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.example.mrt.ship.R;
import com.example.mrt.ship.models.Order;
import com.example.mrt.ship.models.maps.DirectionResults;
import com.example.mrt.ship.models.maps.Leg;
import com.example.mrt.ship.models.maps.Path;
import com.example.mrt.ship.models.maps.Route;
import com.example.mrt.ship.models.maps.RouteDecode;
import com.example.mrt.ship.models.maps.Step;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import com.example.mrt.ship.models.maps.Location;

/**
 * Created by mrt on 24/11/2016.
 */

public class MapUtil {
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
            builder.setTitle(context.getResources().getString(com.example.mrt.ship.R.string.gps_network_not_enabled));
            builder.setMessage("Bật GPS cho phép bạn tìm thấy những đơn hàng xung quanh vị trí của mình.");
            builder.setPositiveButton(context.getResources().getString(com.example.mrt.ship.R.string.open_location_settings)
                    , new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            // TODO Auto-generated method stub
                            Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            context.startActivity(myIntent);
                            //get gps
                        }
                    });
            builder.setNegativeButton(context.getString(com.example.mrt.ship.R.string.Cancel),
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

    public static boolean GPSisON(Context context){
        LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}
        return gps_enabled;
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
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final long duration = 1500;

        // Use the bounce interpolator
        final Interpolator interpolator = new BounceInterpolator();

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


    public static void addMarkerPickupAndDelivery(GoogleMap map, List<String> H, String origins,
                                                  List<Order> data){
        final String[] locations = origins.split("\\|");

        for(int i = 1; i < H.size(); i++){
            String[] latLngString = locations[Integer.valueOf(H.get(i).substring(1))].split(",");
            LatLng latlng = new LatLng(
                    Double.valueOf(latLngString[0]),
                    Double.valueOf(latLngString[1])
            );
            int index = Integer.valueOf(H.get(i).substring(1));
            String type = H.get(i).substring(0,1);
            if(type.equals("p")){

                Marker pMarker = map.addMarker(new MarkerOptions()
                        .position(latlng)
                        .title("Lấy hàng đơn hàng " + (index + 1)/2)
                        .snippet(data.get((index + 1)/2 - 1).getWare_house().getAddress())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                );
                //MapUtil.dropPinEffect(pMarker);

            }else {
                Marker dMarker = map.addMarker(new MarkerOptions()
                        .position(latlng)
                        .title("Giao đơn hàng " + (index/2))
                        .snippet(data.get(index/2 - 1).getRecipient().getAddress())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                );
                //MapUtil.dropPinEffect(dMarker);
            }
        }
    }


    public static void moveMarker(final Context context, final GoogleMap map, final Marker marker,
                                  final List<LatLng> directionPoint,
                                  final boolean hideMarker) {

        // Path
        int[] androidColors = context.getResources().getIntArray(R.array.colors);
        final int randomAndroidColor = androidColors[new Random().nextInt(androidColors.length)];
        PolylineOptions polylineOptions = new PolylineOptions().width(10).color(randomAndroidColor);
        final Polyline polyLine = map.addPolyline(polylineOptions);

        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final long duration = 30000;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            int i = 0;

            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;

                float t = Math.max(1 - interpolator.getInterpolation((float)elapsed/duration)
                        , 0);

                if (i < directionPoint.size()){
                    LatLng latLng = directionPoint.get(i);
                    marker.setPosition(latLng);

                    //update polyline
                    List<LatLng> points = polyLine.getPoints();
                    points.add(latLng);
                    polyLine.setPoints(points);
                }

                i++;

                if (t > 0.0 && i < directionPoint.size()) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 20);
                } else {

                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
    }


    public static Path getPath(DirectionResults directionResults){
        Path path;
        double distance = 0.0;
        double duration = 0.0;
        List<List<LatLng>> list = new ArrayList<>();

        if(directionResults.getRoutes().size()>0){

            // default route index is 0
            Route route = directionResults.getRoutes().get(0);

            if(route.getLegs().size()>0){
                for(Leg leg: route.getLegs()){
                    ArrayList<LatLng> routeList = new ArrayList<>();
                    // duration and distance
                    distance += leg.getDistance().getValue();
                    duration += leg.getDuration().getValue();

                    // steps
                    ArrayList<Step> steps = leg.getSteps();
                    Location location;
                    String polyline;
                    for(Step step:steps){
                        location =step.getStart_location();
                        routeList.add(new LatLng(location.getLat(), location.getLng()));

                        polyline = step.getPolyline().getPoints();
                        ArrayList<LatLng> decodeList = RouteDecode.decodePoly(polyline);
                        routeList.addAll(decodeList);

                        location =step.getEnd_location();
                        routeList.add(new LatLng(location.getLat() ,location.getLng()));
                    }

                    list.add(routeList);
                }
            }
        }
        path = new Path();
        path.setDistance(distance);
        path.setDuration(duration);
        path.setList(list);
        return path;
    }


    public static String getAddress(Context context, LatLng latLng){
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            return addresses.get(0).getAddressLine(0);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

}
