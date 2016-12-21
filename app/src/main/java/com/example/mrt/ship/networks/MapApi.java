package com.example.mrt.ship.networks;

import com.example.mrt.ship.models.maps.DirectionResults;
import com.example.mrt.ship.models.maps.DistanceMatrix;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by mrt on 26/10/2016.
 */

public class MapApi {

    public static final String baseDistanceMatrix = "http://maps.googleapis.com/maps/api/distancematrix/json?";
    public static final String baseDirection = "http://maps.googleapis.com/maps/api/directions/json?";


    private static GoogleMapApi mapApi = null;

    public static GoogleMapApi getInstance(){
        if(mapApi == null){
            String baseUrl = "http://maps.googleapis.com";
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            mapApi = retrofit.create(GoogleMapApi.class);
        }
        return mapApi;
    }

    public interface GoogleMapApi{

        @GET
        Call<DistanceMatrix> getDistanceMatrix(@Url String url);

        @GET
        Call<DirectionResults> directAll(@Url String url);

        @GET("/maps/api/directions/json")
        Call<DirectionResults> direct(@Query("origin") String origin,
                                      @Query("destination") String destination);
    }

}
