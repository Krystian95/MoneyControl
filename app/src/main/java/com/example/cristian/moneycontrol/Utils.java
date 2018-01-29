package com.example.cristian.moneycontrol;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

public class Utils {

    public static String formatNumber(float number){
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.getDefault());
        formatter.setCurrency(Currency.getInstance(Locale.getDefault()));
        formatter.setDecimalSeparatorAlwaysShown(false);
        formatter.setMaximumFractionDigits(2);
        return formatter.format(number);
    }
}
