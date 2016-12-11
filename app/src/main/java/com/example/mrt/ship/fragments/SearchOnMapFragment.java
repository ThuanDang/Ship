package com.example.mrt.ship.fragments;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.mrt.ship.R;
import com.example.mrt.ship.activities.MainActivity;
import com.example.mrt.ship.interfaces.OnFragmentMapListener;
import com.example.mrt.ship.models.Order;
import com.example.mrt.ship.models.WareHouse;
import com.example.mrt.ship.networks.MyApi;
import com.example.mrt.ship.utils.MapUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mrt on 13/10/2016.
 */

public class SearchOnMapFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener {

    private View view;
    private boolean start = false;
    private List<Order> data;
    private String token;
    private android.location.Location myLocation;
    private GoogleMap map;
    private OnFragmentMapListener mListener;

    private Handler handler = new Handler();
    private Context context;

    private SeekBar radius;
    private TextView text_radius;
    private double r = 1;

    List<Marker> markers;
    Circle circle;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        token = PreferenceManager.getDefaultSharedPreferences(context)
                .getString("token", "");
        markers = new ArrayList<>();
        data = new ArrayList<>();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_search_on_map, container, false);
        }

        SupportMapFragment fragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        fragment.getMapAsync(this);

        radius = (SeekBar)view.findViewById(R.id.seek_bar);
        text_radius = (TextView)view.findViewById(R.id.text_radius);

        radius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                r = seekBar.getProgress()/5.0;
                text_radius.setText(String.format("%s km", r));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                r = seekBar.getProgress()/5.0;
                if(r == 0){
                    if(map != null){
                        map.clear();
                        markers.clear();
                    }
                    return;
                }
                fetchData();
            }
        });
        return view;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        LatLng HUST = new LatLng(21.005744, 105.843348);
        CameraUpdate init = CameraUpdateFactory.newLatLngZoom(HUST, 13);
        map.moveCamera(init);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.setMyLocationEnabled(true);
        map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(android.location.Location location) {
                myLocation = location;
                if(start){
                    fetchData();
                    start = false;
                }

            }
        });

    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if(isVisibleToUser){
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    MapUtil.checkGPS(context);
                }
            }, 450);

            if(myLocation == null){
                start = true;
            }else {
                if(MapUtil.GPSisON(context)){
                    fetchData();
                }
            }
        }else {
            handler.removeCallbacksAndMessages(null);
            MapUtil.cancelDialog();
        }
    }


    public void fetchData(){
        if(myLocation != null){
            Call<List<Order>> call = MyApi.getInstance().searchOnMap("Bearer " + token,
                    myLocation.getLatitude(),
                    myLocation.getLongitude(), r);

            CameraPosition cameraPosition = new CameraPosition.Builder().bearing(90f)
                    .target(new LatLng(myLocation.getLatitude(),
                            myLocation.getLongitude()))
                    .zoom(14).build();
            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 350, null);

            // draw circle with radius
            if(circle != null){
                circle.remove();
                circle = map.addCircle(new CircleOptions()
                        .center(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()))
                        .radius(r*1000)
                        .strokeWidth(1)
                        .strokeColor(Color.argb(100, 100, 181, 246))
                        .fillColor(Color.argb(60, 100, 181, 246)));
            }else {
                circle = map.addCircle(new CircleOptions()
                        .center(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()))
                        .radius(r*1000)
                        .strokeWidth(1)
                        .strokeColor(Color.argb(100, 100, 181, 246))
                        .fillColor(Color.argb(60, 100, 181, 246)));
            }


            call.enqueue(new Callback<List<Order>>() {
                int status;
                @Override
                public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                    status = response.code();
                    data = response.body();

                    if(status != 200){
                        showError();
                    }else {
                        if(data != null){
                            mListener.countOrders(data.size(), 2, true);
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    int time = 0;
                                    boolean next;
                                    for(final Order item: data){
                                        next = false;
                                        final WareHouse address = item.getWare_house();
                                        final LatLng latLng = new LatLng(address.getLatitude(),
                                                address.getLongitude());

                                        for(Marker marker: markers){
                                            if(marker.getPosition().equals(latLng)){
                                                next = true;
                                                marker.setTag(1);
                                                break;
                                            }
                                        }
                                        if(next) continue;

                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                Marker marker = map.addMarker(new MarkerOptions()
                                                        .position(latLng)
                                                        .title(item.getName())
                                                        .snippet(address.getAddress())
                                                        .icon(BitmapDescriptorFactory.fromResource(
                                                                R.drawable.ic_marker))
                                                );
                                                markers.add(marker);
                                                MapUtil.dropPinEffect(marker);
                                            }
                                        }, time+=150);

                                    }

                                    List<Marker> delete = new ArrayList<>();
                                    for(Marker marker: markers){
                                        if(marker.getTag() == null){
                                            marker.remove();
                                            delete.add(marker);
                                        }
                                    }
                                    markers.removeAll(delete);
                                    for(Marker marker: markers){
                                        marker.setTag(null);
                                    }
                                }
                            }, 400);
                        }

                    }
                }

                @Override
                public void onFailure(Call<List<Order>> call, Throwable t) {
                    showError();
                }
            });

        }

    }

    public void showError(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getResources().getString(com.example.mrt.ship.R.string.connect_error_title));
        builder.setMessage(context.getResources().getString(com.example.mrt.ship.R.string.connect_error_message));
        builder.setCancelable(false);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }


    @Override
    public void onInfoWindowClick(Marker marker) {

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof OnFragmentMapListener) {
            mListener = (OnFragmentMapListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentOrdersListener");
        }
    }

}
