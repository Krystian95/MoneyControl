package com.example.cristian.moneycontrol;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;

import java.io.File;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

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
