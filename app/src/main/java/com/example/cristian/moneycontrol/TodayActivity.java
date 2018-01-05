package com.example.cristian.moneycontrol;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Map;

public class TodayActivity extends AppCompatActivity {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Log.e("OK", "OK: ");

            switch (item.getItemId()) {
                case R.id.navigation_today:
                    Intent todayIntent = new Intent(TodayActivity.this, TodayActivity.class);
                    startActivity(todayIntent);
                    return true;

                case R.id.navigation_balance:
                    Intent balanceIntent = new Intent(TodayActivity.this, BalanceActivity.class);
                    startActivity(balanceIntent);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_today);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.today_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        setContentView(R.layout.fragment_today);
    }

}
