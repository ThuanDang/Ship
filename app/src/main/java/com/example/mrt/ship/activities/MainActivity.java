package com.example.mrt.ship.activities;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.example.mrt.ship.R;
import com.example.mrt.ship.adapters.PagerAdapter;
import com.example.mrt.ship.interfaces.OnFragmentMapListener;
import com.example.mrt.ship.interfaces.OnFragmentOptionsListener;
import com.example.mrt.ship.interfaces.OnFragmentOrdersListener;
import com.example.mrt.ship.interfaces.OnFragmentReceivedListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.messaging.RemoteMessage;
import com.pusher.android.PusherAndroid;
import com.pusher.android.notifications.ManifestValidator;
import com.pusher.android.notifications.PushNotificationRegistration;
import com.pusher.android.notifications.fcm.FCMPushNotificationReceivedListener;
import com.pusher.android.notifications.interests.InterestSubscriptionChangeListener;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity implements
        OnFragmentOrdersListener,
        OnFragmentOptionsListener,
        OnFragmentMapListener ,
        OnFragmentReceivedListener {

    private String[] titles = { "Các đơn hàng đang chờ", "Các đơn hàng đã nhận",
            "Đơn hàng quanh đây", "Tùy chọn"};
    private String[] counts = {"", "", "", ""};
    private Handler handler = new Handler();
    public static boolean oneCount = true;


    @BindView(R.id.tab_layout_form) View tabLayoutForm;
    @BindView(R.id.title_text) TextView title;
    @BindView(R.id.count) TextView count;
    @BindView(R.id.top_view) View topView;
    @BindView(R.id.viewpager) ViewPager viewPager;
    @BindView(R.id.search_form) View search;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        // title
        title.setText(titles[0]);

        // Tab and viewpager
        setTabToViewPager();

        final Bundle bundle = getIntent().getExtras();
        if(bundle != null){

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    viewPager.setCurrentItem(bundle.getInt("page"));
                }
            }, 500);

        }

        setFCM();


        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
                overridePendingTransition(0, R.anim.search_out);

            }
        });

    }

    private void setTabToViewPager(){
        // Get PagerAdapter
        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager(),
                MainActivity.this);
        // Set PagerAdapter for Viewpager
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(4);

        // Give TabLayout to ViewPager
        TabLayout tabLayout = (TabLayout)findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        // Set icon for TabLayout
        for(int i = 0; i < pagerAdapter.getCount(); i++){
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setCustomView(pagerAdapter.getTabItem(i));
        }

        // Set title when change page
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(final int position) {
                title.setText(titles[position]);
                if(position == 3){
                    count.setVisibility(View.GONE);
                }else {

                    if(!count.isShown()){
                        count.setVisibility(View.VISIBLE);
                    }
                    count.setText(counts[position]);
                }

                if(position == 0){
                    topView.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
                }else {
                    topView.animate().translationY(-search.getHeight())
                            .setInterpolator(new AccelerateInterpolator(2));
                    }
                }

            @Override
            public void onPageScrollStateChanged(int state) {

            }

        });

    }

// ----------------- Hide TabLayout-----------------------

    @Override
    public void hideTab() {
        tabLayoutForm.animate().translationY(tabLayoutForm.getHeight())
                .setInterpolator(new AccelerateInterpolator(2));
    }

    @Override
    public void showTab() {
        tabLayoutForm.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
    }

    @Override
    public void countOrders(int n, int page, boolean countable) {
        String total = String.valueOf(n);

        if(oneCount){

            if(!total.equals(counts[page]) && countable){
                count.setText(total);
            }

        }
        counts[page] = total;
    }

    @Override
    public void hideSearch() {
        topView.animate().translationY(-search.getHeight())
                .setInterpolator(new AccelerateInterpolator(2));
    }

    @Override
    public void showSearch() {
        topView.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
    }


    private boolean playServicesAvailable() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, 9000)
                        .show();
            } else {
                Log.i("test", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }


    public void setFCM(){
        if (playServicesAvailable()) {
            PusherAndroid pusher = new PusherAndroid("72f8c2eed83c7dd0f357");
            PushNotificationRegistration nativePusher = pusher.nativePusher();
            try {
                nativePusher.registerFCM(this);
            } catch (ManifestValidator.InvalidManifestException e) {
                e.printStackTrace();
            }

            nativePusher.subscribe("kittens", new InterestSubscriptionChangeListener() {
                @Override
                public void onSubscriptionChangeSucceeded() {

                }

                @Override
                public void onSubscriptionChangeFailed(int statusCode, String response) {

                }
            });

            nativePusher.setFCMListener(new FCMPushNotificationReceivedListener() {
                @Override
                public void onMessageReceived(RemoteMessage remoteMessage) {
                    showNotification(remoteMessage);
                }
            });
        }
    }



    public void showNotification(RemoteMessage remoteMessage){

        Intent intent = new Intent(this, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("page", 1);
        intent.putExtras(bundle);
        //unique requestID to differentiate between various notification with same NotifId
        int requestID = (int) System.currentTimeMillis();
        int flags = PendingIntent.FLAG_CANCEL_CURRENT; // cancel old intent and create new one
        PendingIntent pIntent = PendingIntent.getActivity(MainActivity.this, requestID, intent, flags);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.snowman);
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(MainActivity.this);

        builder.setSmallIcon(R.drawable.ic_app)
                .setContentTitle(remoteMessage.getData().get("title"))
                .setContentText(remoteMessage.getData().get("body"))
                .setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                        + "://" + getPackageName() + "/raw/christmas"))
                .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .setLargeIcon(bitmap)
                .setColor(Color.argb(255, 66, 165, 245));

        // Get the notification manager system service
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(requestID, builder.build());

    }

}
