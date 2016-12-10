package com.example.mrt.ship.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.example.mrt.ship.fragments.OptionsFragment;
import com.example.mrt.ship.fragments.OrdersWaitingFragment;
import com.example.mrt.ship.fragments.ReceivedOrdersFragment;
import com.example.mrt.ship.fragments.SearchOnMapFragment;

/**
 * Created by mrt on 11/10/2016.
 */

public class PagerAdapter extends FragmentPagerAdapter {
    private Context context;
    private int[] imageResId = {com.example.mrt.ship.R.drawable.ic_orders, com.example.mrt.ship.R.drawable.ic_tasks,
            com.example.mrt.ship.R.drawable.ic_map, com.example.mrt.ship.R.drawable.ic_list_menu};

    public PagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0){
            return new OrdersWaitingFragment();

        }
        else if (position == 1){
            return new ReceivedOrdersFragment();
        }
        else if (position == 2){

            return new SearchOnMapFragment();
        }else {
            return new OptionsFragment();
        }
    }

    @Override
    public int getCount() {
        return 4;
    }

    public View getTabItem(int position){
        View v = LayoutInflater.from(context).inflate(com.example.mrt.ship.R.layout.item_tab, null);
        ImageView img = (ImageView)v.findViewById(com.example.mrt.ship.R.id.tab_icon);
        ColorStateList colors;
        if (Build.VERSION.SDK_INT >= 23) {
            colors = context.getResources().getColorStateList(com.example.mrt.ship.R.color.tab_icon, context.getTheme());
        }
        else {
            colors = context.getResources().getColorStateList(com.example.mrt.ship.R.color.tab_icon);
        }
        img.setImageTintList(colors);
        img.setImageResource(imageResId[position]);
        return v;
    }
}
