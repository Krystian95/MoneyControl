package com.example.cristian.moneycontrol;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        Thread welcomeThread = new Thread() {

            @Override
            public void run() {
                try {
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
}
