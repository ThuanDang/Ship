package com.example.mrt.ship.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

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


public class MainActivity extends AppCompatActivity implements
        OnFragmentOrdersListener,
        OnFragmentOptionsListener,
        OnFragmentMapListener ,
        OnFragmentReceivedListener{

    private String[] titles = { "Các đơn hàng đang chờ", "Các đơn hàng đã nhận",
            "Đơn hàng quanh đây", "Tùy chọn"};
    private String[] counts = {"", "", "", ""};

    private View tabLayoutForm;
    private TextView title;
    private TextView count;
    private View topView;

    private View search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Tab and viewpager
        setTabToViewPager();


        tabLayoutForm = findViewById(R.id.tab_layout_form);
        topView = findViewById(R.id.top_view);
        // title form
        title = (TextView)findViewById(R.id.title_text);
        title.setText(titles[0]);
        count = (TextView)findViewById(R.id.count);



        //search form
        search = findViewById(R.id.search_form);
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
        // Get the ViewPager
        ViewPager viewPager = (ViewPager)findViewById(R.id.viewpager);
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
    public void countOrders(int n, int page) {
        count.setText(String.valueOf(n));
        counts[page] = String.valueOf(n);
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
}
