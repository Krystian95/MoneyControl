package com.example.cristian.moneycontrol.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.util.Log;

import java.util.Calendar;
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

    public static void deleteAllCategories(final AppDatabase db){
        db.categoryDao().deleteAll();
    }

    public static void deleteAllEntries(final AppDatabase db){
        db.entryDao().deleteAll();
    }

    public static void populateWithTestDataEntry(AppDatabase db) {

        Date currentTime = Calendar.getInstance().getTime();

        Entry entry = new Entry();
        entry.setAddress("Via Martiri di Villamarzana 6, Occhiobello, RO, 45030 Italy");
        entry.setAmount((float) 50.12);
        entry.setDateTime(currentTime.getTime());
        entry.setDescription("Lore impus domini");

        addEntry(db, entry);

        Entry[] userList = db.entryDao().getAll();
        Log.e("DATABASE", "Entries Count: " + userList.length);
    }
}