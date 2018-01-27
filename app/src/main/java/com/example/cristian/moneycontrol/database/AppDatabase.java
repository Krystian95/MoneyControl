package com.example.cristian.moneycontrol.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.util.Log;

import com.example.cristian.moneycontrol.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Database(entities = {Entry.class, Category.class, Photo.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public static final String DB_NAME = "money_control_db";

    private static AppDatabase INSTANCE;

    public abstract EntryDao entryDao();

    public abstract CategoryDao categoryDao();

    public abstract PhotoDao photoDao();

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

    public static void insertCategory(final AppDatabase db, Category category) {
        db.categoryDao().insert(category);
    }

    public static long insertEntry(final AppDatabase db, Entry entry) {
        return db.entryDao().insert(entry);
    }

    public static void updateEntry(final AppDatabase db, Entry entry) {
        db.entryDao().update(entry);
    }

    public static void deleteAllUnlinked(final AppDatabase db) {
        db.photoDao().deleteAllUnlinked();
    }

    public static void insertPhoto(final AppDatabase db, Photo photo) {
        db.photoDao().insert(photo);
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

    public static Category getCategoryById(final AppDatabase db, String category_id) {
        return db.categoryDao().getCategoryById(category_id);
    }

    public static Entry getEntryById(final AppDatabase db, String entry_id) {
        return db.entryDao().getEntryById(entry_id);
    }

    public static Entry[] getAllEntries(final AppDatabase db) {
        return db.entryDao().getAll();
    }

    public static Entry[] getAllWithRrule(final AppDatabase db) {
        return db.entryDao().getAllWithRrule();
    }

    public static Photo[] getPhotosByEntryId(final AppDatabase db, String entry_id) {
        return db.photoDao().getPhotosByEntryId(entry_id);
    }

    public static Entry[] getTodayEntries(final AppDatabase db) {

        SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-DD");
        Date myDate = new Date();
        String today_string = format.format(myDate);
        String from_date = today_string + " 00:00:00";
        String to_date = today_string + " 23:59:59";

        return db.entryDao().getEntriesByDateRange(from_date, to_date);
    }

    public static Photo[] getAllPhotosUnlinked(final AppDatabase db) {
        return db.photoDao().getAllUnlinked();
    }

    public static void deletePhotoByAbsolutePath(final AppDatabase db, String absolute_path) {
        db.photoDao().deletePhotoByAbsolutePath(absolute_path);
    }

    public static void deleteEntryById(final AppDatabase db, String entry_id) {
        db.entryDao().deleteEntryById(entry_id);
    }

    public static void updateIdEntryByAbsolutePath(final AppDatabase db, String absolute_path, String id_entry) {
        db.photoDao().updateIdEntryByAbsolutePath(absolute_path, id_entry);
    }

    public static void deleteAllCategories(final AppDatabase db) {
        db.categoryDao().deleteAll();
    }

    public static void deleteAllEntries(final AppDatabase db) {
        db.entryDao().deleteAll();
    }

    public static void printAllPhotos(final AppDatabase db) {
        Photo[] photos = db.photoDao().getAll();

        for (Photo photo : photos) {
            Log.e("DATABASE", photo.toString());
        }
    }

    public static void printAllEntries(final AppDatabase db) {
        Entry[] entries = db.entryDao().getAll();

        for (Entry entry : entries) {
            Log.e("DATABASE", entry.toString());
        }
    }

    public static void setupDefaultCategories(AppDatabase db) {

        List<Category> income_categories;

        String[] income_category_name = {
                "Stipendio",
                "Regali",
                "Lavoro",
                "Dividendi",
                "Interessi",
                "Altro"
        };

        int[] income_category_image = {
                R.drawable.ic_icons8_trasferimento_di_denaro, // Stipendio
                R.drawable.ic_icons8_regalo, // Regali
                R.drawable.ic_icons8_ventiquattrore, // Lavoro
                R.drawable.ic_icons8_sveglia, // Dividendi
                R.drawable.ic_icons8_monete, // Interessi
                R.drawable.ic_icons8_soldi // Altro
        };

        Category category;

        for (int i = 0; i < income_category_name.length; i++) {
            category = new Category();
            category.setIcon(income_category_image[i]);
            category.setName(income_category_name[i]);
            category.setType("income");
            insertCategory(db, category);
        }

        String[] expense_category_name = {
                "Automobile",
                "Casa",
                "Pasti",
                "Alimenti",
                "Personale",
                "Formazione",
                "Divertimento",
                "Debito",
                "Bollette",
                "Assicurazione",
                "Tasse",
                "Abbigliamento",
                "Carburante",
                "Salute",
                "Vacanza",
                "Regali",
                "Bambini",
                "Shopping",
                "Sport",
                "Trasporti",
                "Donazioni",
                "Scuola",
                "Viaggi",
                "Animali",
                "Hotel",
                "Investimenti",
                "Varie"
        };

        int[] expense_category_image = {
                R.drawable.ic_icons8_berlina, // Automobile
                R.drawable.ic_icons8_cottage, // Casa
                R.drawable.ic_icons8_tavolo_del_ristorante, // Pasti
                R.drawable.ic_icons8_ingredienti, // Alimenti
                R.drawable.ic_icons8_utente_maschile, // Personale
                R.drawable.ic_icons8_lezione, // Formazione
                R.drawable.ic_icons8_carosello, // Divertimento
                R.drawable.ic_icons8_storico_dei_pagamenti, // Debito
                R.drawable.ic_icons8_ordine_di_acquisto, // Bollette
                R.drawable.ic_icons8_ombrello, // Assicurazione
                R.drawable.ic_icons8_biblioteca, // Tasse
                R.drawable.ic_icons8_maglietta, // Abbigliamento
                R.drawable.ic_icons8_stazione_di_servizio, // Carburante
                R.drawable.ic_icons8_stetoscopio, // Salute
                R.drawable.ic_icons8_spiaggia, // Vacanza
                R.drawable.ic_icons8_regalo, // Regali
                R.drawable.ic_icons8_volti_di_bambini, // Bambini
                R.drawable.ic_icons8_carrello_della_spesa, // Shopping
                R.drawable.ic_icons8_modalita_sport, // Sport
                R.drawable.ic_icons8_tram_2, // Trasporti
                R.drawable.ic_icons8_cuori, // Donazioni
                R.drawable.ic_icons8_libri, // Scuola
                R.drawable.ic_icons8_modalita_aereo_attiva, // Viaggi
                R.drawable.ic_icons8_cane, // Animali
                R.drawable.ic_icons8_hotel_a_4_stelle, // Hotel
                R.drawable.ic_icons8_finanziamento_collettivo, // Investimenti
                R.drawable.ic_icons8_soldi_in_mano, // Varie
        };

        for (int i = 0; i < expense_category_name.length; i++) {
            category = new Category();
            category.setIcon(expense_category_image[i]);
            category.setName(expense_category_name[i]);
            category.setType("expense");
            insertCategory(db, category);
        }

    }
}