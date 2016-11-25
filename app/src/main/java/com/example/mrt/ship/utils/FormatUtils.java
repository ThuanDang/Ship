package com.example.mrt.ship.utils;

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
}
