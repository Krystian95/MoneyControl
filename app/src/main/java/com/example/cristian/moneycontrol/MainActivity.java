package com.example.cristian.moneycontrol;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements
        TodayFragment.OnFragmentInteractionListener,
        TodayDetailsFragment.OnFragmentInteractionListener,
        BalanceFragment.OnFragmentInteractionListener,
        BalanceDetailsFragment.OnFragmentInteractionListener,
        YearlyBalanceFragment.OnFragmentInteractionListener,
        MonthlyBalanceFragment.OnFragmentInteractionListener,
        DailyBalanceFragment.OnFragmentInteractionListener {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            Fragment fragment;

            switch (item.getItemId()) {
                case R.id.navigation_today:

                    fragment = new TodayFragment();
                    transaction
                            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                            .replace(R.id.fragment_container, fragment)
                            .addToBackStack(null)
                            .commit();

                    return true;
                case R.id.navigation_balance:

                    fragment = new BalanceFragment();
                    transaction
                            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                            .replace(R.id.fragment_container, fragment)
                            .addToBackStack(null)
                            .commit();

                    return true;
                case R.id.navigation_notifications:

                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();

        Fragment fragment = new TodayFragment();

        BottomNavigationView navigation = findViewById(R.id.navigation_bottom);

        if (intent != null) {
            if (intent.hasExtra("balance") && intent.getBooleanExtra("balance", false)) {
                if (intent.hasExtra("balance_monthly")) {
                    String year = intent.getStringExtra("balance_monthly");
                    fragment = BalanceDetailsFragment.newInstance("monthly", year, null);
                }
                if (intent.hasExtra("balance_daily")) {
                    String year = intent.getStringExtra("year");
                    int month = intent.getIntExtra("balance_daily", 0);
                    fragment = BalanceDetailsFragment.newInstance("daily", year, String.valueOf(month));
                }
                navigation.setSelectedItemId(R.id.navigation_balance);
            }
        }

        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fragment).commit();

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
