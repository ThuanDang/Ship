package com.example.mrt.ship.networks;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by mrt on 26/10/2016.
 */
public class Token {
    private String token;
    private static SharedPreferences preferences;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public static String share(Context context){
        if(preferences == null){
            preferences = PreferenceManager.getDefaultSharedPreferences(context);
        }
        return preferences.getString("token", "");
    }

    public static void save(Context context, String token){
        if(preferences == null){
            preferences = PreferenceManager.getDefaultSharedPreferences(context);
        }
        preferences.edit().putString("token", "Bearer " + token).apply();
    }

    public static void remove(Context context){
        if(preferences == null){
            preferences = PreferenceManager.getDefaultSharedPreferences(context);
        }
        preferences.edit().remove("token").apply();
    }
}
