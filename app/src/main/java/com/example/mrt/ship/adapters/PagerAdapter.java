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

import com.example.mrt.ship.R;
import com.example.mrt.ship.fragments.OptionsFragment;
import com.example.mrt.ship.fragments.OrdersFragment;
import com.example.mrt.ship.fragments.ReceivedOrdersFragment;
import com.example.mrt.ship.fragments.SearchOnMapFragment;

/**
 * Created by mrt on 11/10/2016.
 */

public class PagerAdapter extends FragmentPagerAdapter {
    private Context context;
    private int[] imageResId = {R.drawable.ic_orders, R.drawable.ic_map,
            R.drawable.ic_tasks, R.drawable.ic_list_menu};

    public PagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0){
            return OrdersFragment.newInstance();

        }
        else if (position == 1){
            return new SearchOnMapFragment();
        }
        else if (position == 2){
            return ReceivedOrdersFragment.newInstance(3);
        }else {
            return new OptionsFragment();
        }
    }

    @Override
    public int getCount() {
        return 4;
    }

    public View getTabItem(int position){
        View v = LayoutInflater.from(context).inflate(R.layout.tab_item, null);
        ImageView img = (ImageView)v.findViewById(R.id.tab_icon);
        ColorStateList colors;
        if (Build.VERSION.SDK_INT >= 23) {
            colors = context.getResources().getColorStateList(R.color.tab_icon, context.getTheme());
        }
        else {
            colors = context.getResources().getColorStateList(R.color.tab_icon);
        }
        img.setImageTintList(colors);
        img.setImageResource(imageResId[position]);
        return v;
    }
}
