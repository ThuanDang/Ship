package com.example.mrt.ship.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.mrt.ship.R;
import com.example.mrt.ship.models.maps.DirectionResults;
import com.example.mrt.ship.networks.MapApi;
import com.example.mrt.ship.networks.MyApi;
import com.example.mrt.ship.utils.MapUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DirectionActivity extends AppCompatActivity implements OnMapReadyCallback{
    private GoogleMap map;
    private Button change;
    private List<List<LatLng>> multiRoute;
    private boolean start = true;
    private double lat;
    private double lng;
    private int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction);

        lat = getIntent().getExtras().getDouble("lat");
        lng = getIntent().getExtras().getDouble("lng");

        change = (Button)findViewById(R.id.btn_next);

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                index++;
                if(multiRoute!= null && map != null && index < multiRoute.size() ){
                    int[] androidColors = getResources().getIntArray(R.array.colors);
                    final int randomAndroidColor = androidColors[new Random().nextInt(androidColors.length)];
                    PolylineOptions polylineOptions = new PolylineOptions().width(10).color(randomAndroidColor);
                    final Polyline polyLine = map.addPolyline(polylineOptions);
                    polyLine.setPoints(multiRoute.get(index));
                }
            }
        });

        SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        fragment.getMapAsync(this);
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setTrafficEnabled(true);
        map = googleMap;
        map.getUiSettings().setMapToolbarEnabled(false);

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(21.006183, 105.843125), 13));


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        map.setMyLocationEnabled(true);
        map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(android.location.Location location) {

                if(start){
                    MapApi.getInstance().direct(
                            location.getLatitude() + "," + location.getLongitude(),
                            lat + "," + lng).enqueue(new Callback<DirectionResults>() {
                        @Override
                        public void onResponse(Call<DirectionResults> call, Response<DirectionResults> response) {
                            multiRoute = MapUtil.getMutilRoute(response.body());
                            if(multiRoute != null && multiRoute.size() > 0){
                                Log.d("test", "onResponse: " + multiRoute.size());
                                PolylineOptions polylineOptions = new PolylineOptions().width(10).color(Color.RED);
                                final Polyline polyLine = map.addPolyline(polylineOptions);
                                polyLine.setPoints(multiRoute.get(0));
                            }

                        }

                        @Override
                        public void onFailure(Call<DirectionResults> call, Throwable t) {

                        }
                    });
                    start = false;
                }
            }
        });



    }
}
