package com.example.cristian.moneycontrol.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

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

    public static void populateWithTestDataEntry(AppDatabase db) {

    }
}