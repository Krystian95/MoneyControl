package com.example.cristian.moneycontrol.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.example.cristian.moneycontrol.R;

import java.text.SimpleDateFormat;
import java.util.Date;

@Database(entities = {Entry.class, Category.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public static final String DB_NAME = "money_control_db";

    private static AppDatabase INSTANCE;

    public abstract EntryDao entryDao();

    public abstract CategoryDao categoryDao();

    public static AppDatabase getAppDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DB_NAME)
                            .allowMainThreadQueries()
                            .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    public static void addCategory(final AppDatabase db, Category category) {
        db.categoryDao().insert(category);
    }

    public static void addEntry(final AppDatabase db, Entry entry) {
        db.entryDao().insert(entry);
    }

    public static Category[] getAllCategories(final AppDatabase db) {
        return db.categoryDao().getAll();
    }

    public static Category[] getExpenseCategories(final AppDatabase db) {
        return db.categoryDao().getExpenseCategory();
    }

    public static Category[] getIncomeCategories(final AppDatabase db) {
        return db.categoryDao().getIncomeCategory();
    }

    public static Category getCategoryByName(final AppDatabase db, String category_name) {
        return db.categoryDao().getCategoryByName(category_name);
    }

    public static Entry[] getAllEntries(final AppDatabase db) {
        return db.entryDao().getAll();
    }

    public static Entry[] getTodayEntries(final AppDatabase db) {

        SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-DD");
        Date myDate = new Date();
        String today_string = format.format(myDate);
        String from_date = today_string + " 00:00:00";
        String to_date = today_string + " 23:59:59";

        return db.entryDao().getEntriesByDateRange(from_date, to_date);
    }

    public static void deleteAllCategories(final AppDatabase db) {
        db.categoryDao().deleteAll();
    }

    public static void deleteAllEntries(final AppDatabase db) {
        db.entryDao().deleteAll();
    }

    public static void setupDefaultCategories(AppDatabase db) {

        String[] income_category_name = {
                "Stipendio",
                "Regali",
                "Lavoro",
                "Dividendi",
                "Interessi",
                "Altro"
        };

        int[] income_category_image = {
                R.drawable.ic_attach_money_black_24dp,
                R.drawable.ic_card_giftcard_black_24dp,
                R.drawable.ic_work_black_24dp,
                R.drawable.ic_alarm_add_black_24dp,
                R.drawable.ic_account_balance_black_24dp,
                R.drawable.ic_star_black_24dp,
        };

        Category category;

        for (int i = 0; i < income_category_name.length; i++) {
            category = new Category();
            category.setIcon(income_category_image[i]);
            category.setName(income_category_name[i]);
            category.setType("income");
            addCategory(db, category);
        }

        String[] expense_category_name = {
                "Automobile",
                "Casa",
                "Pasti",
                "Alimenti",
                "Personale",
                "Formazione",
                "Divertimenti",
                "Debito",
                "Bollette",
                "Assicurazione",
                "Tasse",
                "Varie"
        };

        int[] expense_category_image = {
                R.drawable.ic_account_balance_black_24dp,
                R.drawable.ic_dashboard_black_24dp,
                R.drawable.ic_dashboard_black_24dp,
                R.drawable.ic_dashboard_black_24dp,
                R.drawable.ic_dashboard_black_24dp,
                R.drawable.ic_dashboard_black_24dp,
                R.drawable.ic_dashboard_black_24dp,
                R.drawable.ic_dashboard_black_24dp,
                R.drawable.ic_dashboard_black_24dp,
                R.drawable.ic_dashboard_black_24dp,
                R.drawable.ic_dashboard_black_24dp,
                R.drawable.ic_dashboard_black_24dp,
        };

        for (int i = 0; i < expense_category_name.length; i++) {
            category = new Category();
            category.setIcon(expense_category_image[i]);
            category.setName(expense_category_name[i]);
            category.setType("expense");
            addCategory(db, category);
        }

    }
}