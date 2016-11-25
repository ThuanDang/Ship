package com.example.mrt.ship.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.mrt.ship.R;
import com.jrummyapps.android.widget.AnimatedSvgView;


public class SplashActivity extends AppCompatActivity {
    private AnimatedSvgView svgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        final Intent intent1 = new Intent(this, LoginActivity.class);
        final Intent intent2 = new Intent(this, MainActivity.class);
        svgView = (AnimatedSvgView) findViewById(R.id.animated_svg_view);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final String token = preferences.getString("token", "");
        //preferences.edit().remove("token").apply();
            preferences.edit().putString("firstLaunch", "x").apply();
            svgView.postDelayed(new Runnable() {

                @Override
                public void run() {
                    svgView.start();
                }
            }, 250);

            svgView.setOnStateChangeListener(new AnimatedSvgView.OnStateChangeListener() {

                @Override public void onStateChange(int state) {
                    if (state == AnimatedSvgView.STATE_FINISHED) {
                        if (token.isEmpty()) {
                            startActivity(intent1);
                        } else {
                            startActivity(intent2);
                        }
                        SplashActivity.this.finish();
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                }
            });

    }
}
