package com.example.cristian.moneycontrol;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.cristian.moneycontrol.database.AppDatabase;
import com.example.cristian.moneycontrol.database.Category;

import java.io.File;

public class StartActivity extends AppCompatActivity {

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... params) {

                AppDatabase db = AppDatabase.getAppDatabase(getApplicationContext());

                //AppDatabase.deleteAllEntries(db);
                //AppDatabase.deleteAllCategories(db);

                /*Category category = new Category();
                category.setName("Giardinaggio");
                AppDatabase.addCategory(db, category);

                Category[] categories = AppDatabase.getAllCategories(db);

                Log.e("DATABASE", "Categories Count: " + categories.length);

                for (Category category_temp : categories) {
                    Log.e("DATABASE", category_temp.toString());
                }

                Entry entry = new Entry();
                entry.setAddress("Via Martiri di Villamarzana 6, Occhiobello, RO, 45030 Italy");
                entry.setAmount((float) 50.12);

                SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-DD HH:mm:ss");
                Date myDate = new Date();
                String today_string = format.format(myDate);

                entry.setDateTime(today_string);

                entry.setDescription("Lore impus domini");
                entry.setRecurrenceRule("rRule");
                entry.setIdCategory(1);

                AppDatabase.addEntry(db, entry);

                Entry[] entries = AppDatabase.getTodayEntries(db);

                Log.e("DATABASE", "Entries Count: " + entries.length);

                for (Entry entry_temp : entries) {
                    Log.e("DATABASE", entry_temp.toString());
                }*/

                /*
                categories = AppDatabase.getAllCategories(db);

                Log.e("DATABASE", "Category Count: " + categories.length);

                for (Category category_temp : categories) {
                    Log.e("DATABASE", category_temp.toString());
                }*/

                Category[] categories = AppDatabase.getAllCategories(db);

                if (categories.length == 0) {
                    AppDatabase.setupDefaultCategories(db);
                }

                return 0;
            }

            @Override
            protected void onPostExecute(Integer agentsCount) {

            }
        }.execute();

        Thread welcomeThread = new Thread() {

            @Override
            public void run() {
                try {
                    deleteTempFiles(getExternalFilesDir(Environment.DIRECTORY_PICTURES));

                    sleep(1000);
                } catch (Exception e) {

                } finally {
                    Intent todayIntent = new Intent(StartActivity.this, MainActivity.class);
                    startActivity(todayIntent);
                    finish();
                }
            }
        };
        welcomeThread.start();

        setContentView(R.layout.activity_start);
    }

    private boolean deleteTempFiles(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.isDirectory()) {
                        deleteTempFiles(f);
                    } else {
                        if (f.length() == 0) {
                            f.delete();
                        }
                    }
                }
            }
        }
        return file.delete();
    }
}
