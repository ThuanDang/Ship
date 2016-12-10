package com.example.mrt.ship.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.support.design.widget.NavigationView;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.TextView;

import com.example.mrt.ship.preferences.CustomTypefaceSpan;

/**
 * Created by mrt on 16/10/2016.
 */

public class FontUtil {
    private Context context;

    private FontUtil(){}
    private static FontUtil instance = new FontUtil();
    public static FontUtil from(Context context){
        instance.setContext(context);
        return instance;
    }

    public void applyFontToMenuItem(MenuItem mi, String fontName) {
        Typeface font = Typeface.createFromAsset(context.getAssets(), fontName);
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("" , font), 0 , mNewTitle.length(),
                Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    public void applyFontToNav(NavigationView nav, String fontName){
        Menu m = nav.getMenu();
        for (int i=0;i<m.size();i++) {
            MenuItem mi = m.getItem(i);

            //for applying a font to subMenu ...
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu != null && subMenu.size() > 0) {
                for (int j = 0; j < subMenu.size(); j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem, fontName);
                }
            }
            //the method we have create in activity
            applyFontToMenuItem(mi, fontName);
        }
    }

    public void applyFontToTextView(TextView textView, String fontName ){
        Typeface font = Typeface.createFromAsset(context.getAssets(), fontName);
        textView.setTypeface(font);
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
