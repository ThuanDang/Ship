package com.example.mrt.ship.fragments;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
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

import com.example.mrt.ship.R;
import com.example.mrt.ship.adapters.CustomWindowAdapter;
import com.example.mrt.ship.models.Location;
import com.example.mrt.ship.models.Order;
import com.example.mrt.ship.preferences.OnFragmentMapListener;
import com.example.mrt.ship.sync.ApiInterface;
import com.example.mrt.ship.utils.ApiUtils;
import com.example.mrt.ship.utils.MapUtils;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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
    private ApiInterface api;
    private List<Order> data;
    private String token;
    private android.location.Location myLocation;
    private GoogleMap map;
    private Handler handler = new Handler();
    private OnFragmentMapListener mListener;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = ApiUtils.getApi();
        token = PreferenceManager.getDefaultSharedPreferences(getContext())
                .getString("token", "");
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
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        LatLng HUST = new LatLng(21.005744, 105.843348);
        CameraUpdate init = CameraUpdateFactory.newLatLngZoom(HUST, 13);
        map.animateCamera(init);


        map.setInfoWindowAdapter(new CustomWindowAdapter(getActivity().getLayoutInflater()));

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
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
                    MapUtils.checkGPS(getContext());
                }
            }, 400);

            if(myLocation == null){
                start = true;
            }else {
                if(MapUtils.GPSisON(getContext())){
                    fetchData();
                }
            }
        }

    }

    public void fetchData(){
        if(myLocation != null){
            Call<List<Order>> call = api.searchOnMap("Token " + token,
                    myLocation.getLatitude(),
                    myLocation.getLongitude());
            //move camera to my location
            CameraUpdate cameraUpdate = CameraUpdateFactory
                    .newLatLngZoom(new LatLng(myLocation.getLatitude(),
                            myLocation.getLongitude()), 15);
            map.animateCamera(cameraUpdate, 450, null);

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
                            mListener.countInMap(data.size());
                            map.clear();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    for(Order item: data){
                                        Location address = item.getFrom_address();
                                        Marker marker = map.addMarker(new MarkerOptions()
                                                .position(new LatLng(address.getLatitude(),
                                                        address.getLongitude()))
                                                .title(item.getName())
                                                .snippet(address.getName())
                                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker)));
                                        MapUtils.dropPinEffect(marker);
                                    }
                                }
                            }, 500);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getContext().getResources().getString(R.string.connect_error_title));
        builder.setMessage(getContext().getResources().getString(R.string.connect_error_message));
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
        if (context instanceof OnFragmentMapListener) {
            mListener = (OnFragmentMapListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentOrdersListener");
        }
    }
}
