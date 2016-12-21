package com.example.mrt.ship.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.mrt.ship.R;
import com.example.mrt.ship.adapters.CustomWindowAdapter;
import com.example.mrt.ship.adapters.OrdersSuggestAdapter;
import com.example.mrt.ship.models.Order;
import com.example.mrt.ship.models.maps.DirectionResults;
import com.example.mrt.ship.models.maps.Path;
import com.example.mrt.ship.networks.MapApi;
import com.example.mrt.ship.networks.MyApi;
import com.example.mrt.ship.networks.Token;
import com.example.mrt.ship.preferences.FixMapFragment;
import com.example.mrt.ship.utils.DialogUtil;
import com.example.mrt.ship.utils.FormatUtil;
import com.example.mrt.ship.utils.MapUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailOrderActivity extends AppCompatActivity implements OnMapReadyCallback {

    @BindView(R.id.btn_receive)
    Button buttonReceive;
    @BindView(R.id.btn_cancel)
    Button buttonCancel;
    @BindView(R.id.btn_pickup)
    Button buttonPickup;
    @BindView(R.id.btn_delivery)
    Button buttonDelivery;
    @BindView(R.id.distance)
    TextView distance;
    @BindView(R.id.duration)
    TextView duration;
    @BindView(R.id.list_suggest)
    RecyclerView listSuggest;
    @BindView(R.id.scroll)
    ScrollView scrollView;
    @BindView(R.id.type)
    TextView orderType;
    @BindView(R.id.commodity_name)
    TextView commodityName;
    @BindView(R.id.commodity_count)
    TextView commodityCount;
    @BindView(R.id.owner_name)
    TextView ownerName;
    @BindView(R.id.owner_address)
    TextView ownerAddress;
    @BindView(R.id.smooth_progress)
    SmoothProgressBar smoothProgress;
    @BindView(R.id.cancel_or_pickup)
    View cancel_pickup_view;

    private Order order;
    private List<Order> dataSuggest;
    Handler handler = new Handler();
    OrdersSuggestAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_order);

        dataSuggest = new ArrayList<>();
        order = getIntent().getExtras().getParcelable("order");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);

        if(order != null){
            getSupportActionBar().setTitle(order.getName());
            setData();
            setButton();
            initSuggest();

            FixMapFragment fragment = (FixMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            fragment.getMapAsync(this);
            fragment.setListener(new FixMapFragment.OnTouchListener() {
                @Override
                public void onTouch() {
                    scrollView.requestDisallowInterceptTouchEvent(true);
                }
            });
        }else {
            order = new Order();
            String id = getIntent().getExtras().getString("id");
            fetchData(id);
        }

    }


    @OnClick(R.id.btn_receive)
    public void receiveOrder() {
        smoothProgress.setVisibility(View.VISIBLE);
        cancel_pickup_view.setVisibility(View.GONE);
        buttonReceive.setVisibility(View.GONE);

        MyApi.getInstance().receiveOrder(Token.share(this), order.getId())
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        final int status = response.code();
                        Context context = DetailOrderActivity.this;
                        smoothProgress.setVisibility(View.GONE);
                        if (status == 200) {
                            DialogUtil.receiveSuccess(context);
                            cancel_pickup_view.setVisibility(View.VISIBLE);

                        } else if (status == 404) {
                            DialogUtil.conflictReceiveOrder(context,
                                    new DialogUtil.Callback() {
                                        @Override
                                        public void onPositiveButtonClick() {
                                            Intent intent = new Intent(DetailOrderActivity.this,
                                                    MainActivity.class);
                                            startActivity(intent);
                                            DetailOrderActivity.this.finish();
                                        }
                                    });
                            buttonReceive.setVisibility(View.VISIBLE);
                        } else if (status == 402) {
                            DialogUtil.notEnoughMoney(context);
                            buttonReceive.setVisibility(View.VISIBLE);
                        } else {
                            DialogUtil.connectError(context);
                            buttonReceive.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        DialogUtil.connectError(DetailOrderActivity.this);
                        buttonReceive.setVisibility(View.VISIBLE);
                    }
                });
    }

    @OnClick(R.id.btn_pickup)
    public void pickup() {
        cancel_pickup_view.setVisibility(View.GONE);
        smoothProgress.setVisibility(View.VISIBLE);

        MyApi.getInstance().pickupOrder(Token.share(this),
                order.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if (response.code() != 200) {
                    DialogUtil.connectError(DetailOrderActivity.this);
                }

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                DialogUtil.connectError(DetailOrderActivity.this);
            }
        });
    }

    @OnClick(R.id.btn_delivery)
    public void delivery() {
        smoothProgress.setVisibility(View.VISIBLE);
        buttonDelivery.setVisibility(View.GONE);
        MyApi.getInstance().deliveryOrder(Token.share(this),
                order.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() != 200) {
                    DialogUtil.connectError(DetailOrderActivity.this);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                DialogUtil.connectError(DetailOrderActivity.this);
            }
        });
    }

    @OnClick(R.id.btn_cancel)
    public void cancelOrder() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xác nhận hủy");
        builder.setMessage("Hủy đơn hàng sẽ dẫn đến việc bạn bị trừ thành tích cá nhân. " +
                "Vẫn tiếp tục?");

        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                cancel_pickup_view.setVisibility(View.GONE);
                smoothProgress.setVisibility(View.VISIBLE);
                MyApi.getInstance().cancelOrder(Token.share(DetailOrderActivity.this),
                        order.getId()).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {


                        if (response.code() != 200) {
                            DialogUtil.connectError(DetailOrderActivity.this);
                            cancel_pickup_view.setVisibility(View.VISIBLE);
                            smoothProgress.setVisibility(View.GONE);
                        } else {
                            buttonReceive.setVisibility(View.VISIBLE);
                            smoothProgress.setVisibility(View.GONE);
                            DialogUtil.cancelOrderSuccess(DetailOrderActivity.this);
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        DialogUtil.connectError(DetailOrderActivity.this);
                        cancel_pickup_view.setVisibility(View.VISIBLE);
                        smoothProgress.setVisibility(View.GONE);
                    }
                });
            }
        });

        builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();
    }


    @Override
    public void onMapReady(final GoogleMap googleMap) {

        CameraPosition cameraPosition = new CameraPosition.Builder().bearing(90f)
                .target(new LatLng(order.getWare_house().getLatitude(),
                        order.getWare_house().getLongitude()))
                .zoom(16).build();

        googleMap.setInfoWindowAdapter(new CustomWindowAdapter(getLayoutInflater()));

        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                if(marker.getTitle().equals("Nhận hàng")){
                    call(order.getRecipient().getPhone());
                }else{
                    call(order.getWare_house().getPhone());
                }
            }
        });

        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 350, null);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Marker delivery = googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(order.getRecipient().getLatitude(),
                                order.getRecipient().getLongitude()))
                        .title("Giao hàng")
                        .snippet(order.getRecipient().getAddress())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                );
                MapUtil.dropPinEffect(delivery);

                Marker pickup = googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(order.getWare_house().getLatitude(),
                                order.getWare_house().getLongitude()))
                        .title("Nhận hàng")
                        .snippet(order.getWare_house().getAddress())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                );
                MapUtil.dropPinEffect(pickup);
            }
        }, 500);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                direct(googleMap);
            }
        }, 800);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getDataSuggest();
            }
        }, 1000);
    }


    public void call(String number) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + number));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(callIntent);
    }


    public void initSuggest(){
        adapter = new OrdersSuggestAdapter(this, dataSuggest);
        listSuggest.setAdapter(adapter);
        listSuggest.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,
                true));
        listSuggest.hasFixedSize();

        adapter.setOnItemClickListener(new OrdersSuggestAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                Intent intent = new Intent(DetailOrderActivity.this, DetailOrderActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("order", adapter.getData().get(position));
                intent.putExtras(bundle);
                startActivity(intent);
                DetailOrderActivity.this.finish();
            }
        });
    }


    public void setData(){
        if(order.getType() == 1){
            orderType.setText("Chỉ giao hàng");
        }else {
            orderType.setText("Thu tiền hộ");
        }

        commodityName.setText(order.getCommodities().getName());
        commodityCount.setText(String.valueOf(order.getCommodities().getCount()));
        ownerName.setText(order.getCustomer().getUsers().getName());
        ownerAddress.setText(order.getCustomer().getUsers().getAddress());

    }

    public void setButton(){
        switch (order.getStatus()){
            case 1:
                buttonReceive.setVisibility(View.VISIBLE);
            case 2:
                cancel_pickup_view.setVisibility(View.VISIBLE);
                break;
            case 3:
                smoothProgress.setVisibility(View.VISIBLE);
                break;
            case 4:
                buttonDelivery.setVisibility(View.VISIBLE);
                break;
            case 5:
                smoothProgress.setVisibility(View.VISIBLE);
                break;
        }
    }


    public void getDataSuggest(){
        MyApi.getInstance().getSuggest(Token.share(this),
                order.getRecipient().getLatitude(), order.getRecipient().getLongitude(),
                order.getWare_house().getLatitude(), order.getWare_house().getLongitude())
                .enqueue(new Callback<List<Order>>() {
                    @Override
                    public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                        if(response.body() != null){
                            adapter.swapItems(response.body());

                        }
                    }

                    @Override
                    public void onFailure(Call<List<Order>> call, Throwable t) {

                    }
                });
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

    public void direct(final GoogleMap map){
        MapApi.getInstance().direct(
                order.getWare_house().getLatitude() + "," + order.getWare_house().getLongitude(),
                order.getRecipient().getLatitude() + "," + order.getRecipient().getLongitude())
                .enqueue(new Callback<DirectionResults>() {
                    @Override
                    public void onResponse(Call<DirectionResults> call, Response<DirectionResults> response) {
                        Log.d("test", "onResponse: " + response.body().getStatus());

                        PolylineOptions polylineOptions = new PolylineOptions().width(10).color(Color.RED);
                        final Polyline polyLine = map.addPolyline(polylineOptions);

                        Path path = MapUtil.getPath(response.body());
                        if(path.getList().size() > 0){
                            for(int i = 0; i < path.getList().size(); i++){
                                polyLine.setPoints(path.getList().get(i));
                            }



                            duration.setText(FormatUtil.timeConvert((int)path.getDuration()));
                            distance.setText(String.format("%s km", path.getDistance() / 1000));
                        }
                    }

                    @Override
                    public void onFailure(Call<DirectionResults> call, Throwable t) {
                        Log.d("test", "onFailure: " + t.toString());
                    }
                });
    }


    public void fetchData(String id){
        MyApi.getInstance().getOrderDetail(Token.share(this), id)
                .enqueue(new Callback<Order>() {
                    @Override
                    public void onResponse(Call<Order> call, Response<Order> response) {
                        if(response.code() == 200){
                            order = response.body();
                            getSupportActionBar().setTitle(order.getName());
                            setData();
                            setButton();
                            initSuggest();


                            FixMapFragment fragment = (FixMapFragment) getSupportFragmentManager()
                                    .findFragmentById(R.id.map);
                            fragment.getMapAsync(DetailOrderActivity.this);
                            fragment.setListener(new FixMapFragment.OnTouchListener() {
                                @Override
                                public void onTouch() {
                                    scrollView.requestDisallowInterceptTouchEvent(true);
                                }
                            });
                        }
                    }


                    @Override
                    public void onFailure(Call<Order> call, Throwable t) {

                    }
                });
    }
}
