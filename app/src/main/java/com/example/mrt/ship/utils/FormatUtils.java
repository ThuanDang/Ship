package com.example.mrt.ship.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

/**
 * Created by mrt on 27/10/2016.
 */

public class FormatUtils {
    public static final String VN = "VND";
    public static final String US = "USD";

    public static String formatCurrency(Double value, String code){
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.getDefault());
        format.setCurrency(Currency.getInstance(code));
        return format.format(value);
    }

    public static float dipToPixels(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }
}
