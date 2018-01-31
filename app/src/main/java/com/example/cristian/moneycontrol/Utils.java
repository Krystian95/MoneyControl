package com.example.cristian.moneycontrol;

import android.content.Context;

import com.example.cristian.moneycontrol.database.AppDatabase;
import com.example.cristian.moneycontrol.database.Category;
import com.example.cristian.moneycontrol.database.Entry;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/*
Contains utilities containing for general purposes.
 */
public class Utils {

    /*
    Format a number with the default Locale.
     */
    public static String formatNumber(float number) {
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.getDefault());
        formatter.setCurrency(Currency.getInstance(Locale.getDefault()));
        formatter.setDecimalSeparatorAlwaysShown(false);
        formatter.setMaximumFractionDigits(2);
        return formatter.format(number);
    }

    /*
    Returns a Map<Integer, Category> with all existing categories
     */
    public Map buildCategoriesMap(Context context) {
        AppDatabase db = AppDatabase.getAppDatabase(context);
        Category[] categories = AppDatabase.getAllCategories(db);

        Map<Integer, Category> map = new HashMap<>();

        for (Category category : categories) {
            map.put(category.getIdCategory(), category);
        }

        return map;
    }

    /*
    Returns a Map<Integer, Entry> with all existing entries
     */
    public Map buildEntriesMap(Context context) {
        AppDatabase db = AppDatabase.getAppDatabase(context);
        Entry[] entries = AppDatabase.getAllEntries(db);

        Map<Integer, Entry> map = new HashMap<>();

        for (Entry entry : entries) {
            map.put(entry.getIdEntry(), entry);
        }

        return map;
    }
}
