package com.example.cristian.moneycontrol;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements
        TodayFragment.OnFragmentInteractionListener,
        TodayDetailsFragment.OnFragmentInteractionListener,
        BalanceFragment.OnFragmentInteractionListener,
        BalanceDetailsFragment.OnFragmentInteractionListener {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            switch (item.getItemId()) {
                case R.id.navigation_today:

                    TodayFragment todayFragment = new TodayFragment();
                    transaction
                            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                            .replace(R.id.fragment_container, todayFragment)
                            .addToBackStack(null)
                            .commit();

                    return true;
                case R.id.navigation_balance:

                    BalanceFragment balanceFragment = new BalanceFragment();
                    transaction
                            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                            .replace(R.id.fragment_container, balanceFragment)
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

        TodayFragment todayFragment = new TodayFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, todayFragment).commit();

        BottomNavigationView navigation = findViewById(R.id.navigation_bottom);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
