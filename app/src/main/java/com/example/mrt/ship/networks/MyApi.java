package com.example.mrt.ship.networks;

import com.example.mrt.ship.models.Order;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Url;

/**
 * Created by mrt on 07/12/2016.
 */

public class MyApi {

    private static MyApiDef instance = null;


    public static MyApiDef getInstance(){
        if(instance == null){
            String baseUrl = "http://192.168.43.170:8000";

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            instance = retrofit.create(MyApiDef.class);
        }
        return instance;
    }


    public interface MyApiDef{

        @GET("/api/ship/received")
        Call<List<Order>> getReceivedOrders(@Header("Authorization") String token);

        @GET("/api/ship/orders")
        Call<Result> getListOrderWaiting(@Header("Authorization") String token);

        @GET
        Call<Result> loadMoreOrder(@Header("Authorization") String token, @Url String url);

        @FormUrlEncoded
        @POST("/api/ship/login/")
        Call<Token> login(@Field("email") String username,
                          @Field("password") String password);

        @GET("/ship/logout/")
        Call<Result> logOut(@Header("Authorization") String token);

        @GET("/api/ship/orders-nearest/{lat}/{lng}/{radius}")
        Call<List<Order>> searchOnMap(@Header("Authorization") String token,
                                      @Path("lat") double lat,
                                      @Path("lng") double lng,
                                      @Path("radius") double radius);

        @GET("/api/ship/receive-order/{id}")
        Call<Void> receiveOrder(@Header("Authorization") String token, @Path("id") int id);


        @GET("/api/ship/pickup-order/{id}")
        Call<Void> pickupOrder(@Header("Authorization") String token, @Path("id") int id);

        @GET("/api/ship/delivery-order/{id}")
        Call<Void> deliveryOrder(@Header("Authorization") String token, @Path("id") int id);


        @GET("/api/ship/cancel-order/{id}")
        Call<Void> cancelOrder(@Header("Authorization") String token, @Path("id") int id);

        @GET("/api/ship/orders-nearest-to-order/" +
                "{latRecipient}/{lngRecipient}/{latWareHouse}/{lngWareHouse}")
        Call<List<Order>> getSuggest(@Header("Authorization") String token,
                                     @Path("latRecipient") double latRecipient,
                                     @Path("lngRecipient") double lngRecipient,
                                     @Path("latWareHouse") double latWareHouse,
                                     @Path("lngWareHouse") double lngWareHouse);

        @FormUrlEncoded
        @POST("/api/ship/register")
        Call<Void> signUp(@Field("email") String username,
                          @Field("password") String password,
                          @Field("name") String name,
                          @Field("phone") String phone,
                          @Field("acount_id") String account,
                          @Field("address") String address);
    }
}
