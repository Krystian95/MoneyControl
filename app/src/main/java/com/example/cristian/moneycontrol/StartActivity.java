package com.example.cristian.moneycontrol;

import android.annotation.SuppressLint;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.cristian.moneycontrol.database.AppDatabase;
import com.example.cristian.moneycontrol.database.Category;
import com.example.cristian.moneycontrol.database.Entry;

import java.io.File;
import java.util.Date;

public class StartActivity extends AppCompatActivity {

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... params) {
                Log.e("DATABASE", "ok3");

                AppDatabase db = AppDatabase.getAppDatabase(getApplicationContext());

                AppDatabase.deleteAllCategories(db);

                Category category = new Category();
                category.setName("Giardinaggio");
                AppDatabase.addCategory(db, category);

                Category[] categories = AppDatabase.getAllCategories(db);

                Log.e("DATABASE", "Categories Count: " + categories.length);

                for (Category category_temp : categories) {
                    Log.e("DATABASE", category_temp.toString());
                }

                AppDatabase.deleteAllEntries(db);

                Entry entry = new Entry();
                entry.setAddress("Via Martiri di Villamarzana 6, Occhiobello, RO, 45030 Italy");
                entry.setAmount((float) 50.12);
                Date currentTime = java.util.Calendar.getInstance().getTime();
                entry.setDateTime(currentTime.getTime());
                entry.setDescription("Lore impus domini");
                entry.setRecurrenceRule("rRule");
                entry.setIdCategory(1);

                AppDatabase.addEntry(db, entry);

                Entry[] entries = AppDatabase.getAllEntries(db);

                Log.e("DATABASE", "Entries Count: " + entries.length);

                for (Entry entry_temp : entries) {
                    Log.e("DATABASE", entry_temp.toString());
                }

                return 0;
            }

            @Override
            protected void onPostExecute(Integer agentsCount) {
                // TODO start correct activity
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
