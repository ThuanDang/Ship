package com.example.mrt.ship.networks;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by mrt on 26/10/2016.
 */

public class RESTfulApi {

    private static String baseUrl = "http://192.168.43.170:8000";
    private static ApiInterface instance = null;

    public static ApiInterface getApi(){
        if(instance == null){
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            instance = retrofit.create(ApiInterface.class);
        }

        return instance;
    }
}
