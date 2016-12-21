package com.example.mrt.ship.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.mrt.ship.R;
import com.example.mrt.ship.models.Order;
import com.example.mrt.ship.models.maps.DirectionResults;
import com.example.mrt.ship.models.maps.DistanceMatrix;
import com.example.mrt.ship.models.maps.Path;
import com.example.mrt.ship.networks.MapApi;
import com.example.mrt.ship.utils.MapUtil;
import com.example.mrt.ship.utils.PDTSP;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScheduleWayActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap map;
    private List<Order> data;
    private String origins = "";
    private DistanceMatrix matrix;
    private boolean start = true;

    private List<String> P = new ArrayList<>();
    private List<String> D = new ArrayList<>();
    private List<String> H;

    private Path path;

    private Button next;
    private Handler handler = new Handler();
    private int index = 0;
    private Marker myMarker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_way);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Lịch trình");

        init();

        next = (Button)findViewById(R.id.btn_next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                index++;
                if(path!= null && map != null && index < path.getList().size() ){
                    MapUtil.moveMarker(ScheduleWayActivity.this, map, myMarker,
                            path.getList().get(index), false);
                }
            }
        });


        SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        fragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
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
                    origins = location.getLatitude() + "," + location.getLongitude() + "|"
                            + origins.substring(0, origins.length() - 1);
                    String url = MapApi.baseDistanceMatrix
                            + "origins=" + origins + "&&destinations=" + origins;

                    getMatrix(url);
                    start = false;
                }
            }
        });

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                MapUtil.checkGPS(ScheduleWayActivity.this);
            }
        }, 300);

    }

    public void getMatrix(String url){
        Call<DistanceMatrix> call = MapApi.getInstance().getDistanceMatrix(url);
        call.enqueue(new Callback<DistanceMatrix>() {
            @Override
            public void onResponse(Call<DistanceMatrix> call, Response<DistanceMatrix> response) {
                matrix = response.body();
                Log.d("test", "onResponse: "  + matrix.getStatus());

                if(matrix != null){
                    if(matrix.getRows().size() != 0){
                        PDTSP f = new PDTSP(P, D, matrix);
                        H = f.solve();

                        for (String h:H){
                            Log.d("test", "onResponse: " + H);
                        }


                        MapUtil.addMarkerPickupAndDelivery(map, H, origins, data);

                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                directAll(H);
                            }
                        }, 500);

                    }
                }
            }

            @Override
            public void onFailure(Call<DistanceMatrix> call, Throwable t) {
                Log.d("test", "onFailure: " + t.toString());
            }
        });
    }


    public void directAll(final List<String> H){

        final String[] locations = origins.split("\\|");

        String origin = "origin=" + locations[Integer.valueOf(H.get(0).substring(1))];

        String destination = "destination=" + locations[Integer.valueOf(H.get(H.size()-1).substring(1))];

        String waypoints = "waypoints=";
        for(int i = 1; i < H.size() - 1; i++){
            waypoints += locations[Integer.valueOf(H.get(i).substring(1))] + "|";
        }

        String url = MapApi.baseDirection + origin + "&" + destination + "&"  + waypoints;

        Call<DirectionResults> call = MapApi.getInstance().directAll(url);


        call.enqueue(new Callback<DirectionResults>() {
            @Override
            public void onResponse(Call<DirectionResults> call, Response<DirectionResults> response) {

                DirectionResults directionResults = response.body();
                path = MapUtil.getPath(directionResults);

                if(path.getList().size() > 0){
                    final List<LatLng> points = path.getList().get(0);
                    if(points.size() > 0){
                        LatLng myLatLng = points.get(0);

                        myMarker = map.addMarker(new MarkerOptions()
                                .position(myLatLng)
                                .title("Vị trí của bạn")
                                .flat(true)
                                .icon(BitmapDescriptorFactory.fromResource(com.example.mrt.ship.R.drawable.santa))
                        );

                        MapUtil.dropPinEffect(myMarker);

                        CameraPosition cameraPosition = new CameraPosition.Builder().target(myLatLng)
                                .bearing(90f).zoom(16f).build();
                        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));



                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                MapUtil.moveMarker(ScheduleWayActivity.this, map, myMarker, points, false);
                            }
                        }, 1500);

                    }
                }

            }


            @Override
            public void onFailure(Call<DirectionResults> call, Throwable t) {

            }
        });

    }


    public void init(){
        Bundle bundle = getIntent().getExtras();
        data = bundle.getParcelableArrayList("data");
        for (Order order : data) {
            origins += order.getWare_house().getLatitude()
                    + ","
                    + order.getWare_house().getLongitude()
                    + "|"
                    + order.getRecipient().getLatitude()
                    + ","
                    + order.getRecipient().getLongitude()
                    + "|";
        }

        for(int i = 1; i <= data.size()*2; i ++){
            if(i%2 == 1){
                P.add("p" + i);
            }else {
                D.add("d" + i);
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
