package com.example.mrt.ship.networks;

import com.example.mrt.ship.models.Order;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Url;

/**
 * Created by mrt on 24/10/2016.
 */

public interface ApiInterface {

    @GET("/api/ship/orders")
    Call<GetJson> getListOrder(@Header("Authorization") String token);

    @GET
    Call<GetJson> getListOrderMore(@Header("Authorization") String token, @Url String url);

    @FormUrlEncoded
    @POST("/api/ship/login/")
    Call<TokenAuthentication> login(@Field("email") String username,
                       @Field("password") String password);

    @GET("/ship/logout/")
    Call<GetJson> logOut(@Header("Authorization") String token);

    @GET("/api/ship/orders-nearest/{lat}/{lng}/{radius}")
    Call<List<Order>> searchOnMap(@Header("Authorization") String token, @Path("lat") double lat,
                                  @Path("lng") double lng, @Path("radius") double radius);

    @GET("/api/ship/received-orders")
    Call<List<Order>> getReceivedOrders(@Header("Authorization") String token);
}
