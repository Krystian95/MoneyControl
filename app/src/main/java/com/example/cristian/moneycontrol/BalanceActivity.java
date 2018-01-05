package com.example.cristian.moneycontrol;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class BalanceActivity extends AppCompatActivity {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_today:
                    Intent todayIntent = new Intent(BalanceActivity.this, TodayActivity.class);
                    startActivity(todayIntent);
                    return true;

                case R.id.navigation_balance:
                    Intent balanceIntent = new Intent(BalanceActivity.this, BalanceActivity.class);
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
        setContentView(R.layout.activity_balance);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.balance_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

}
