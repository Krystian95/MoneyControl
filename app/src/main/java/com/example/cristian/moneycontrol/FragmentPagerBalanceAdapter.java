package com.example.cristian.moneycontrol;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Cristian on 09/01/2018.
 */

public class FragmentPagerBalanceAdapter extends FragmentPagerAdapter {

    final int PAGE_COUNT = 4;
    private String tabTitles[] = new String[]{
            "Giorno",
            "Settimana",
            "Mese",
            "Anno"
    };

    public FragmentPagerBalanceAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0: // Daily
                return YearlyBalanceFragment.newInstance("?", "?");

            case 1: // Weekly
                return YearlyBalanceFragment.newInstance("?", "?");

            case 2: // Monthly
                return YearlyBalanceFragment.newInstance("?", "?");

            case 3: // Yearly
                return YearlyBalanceFragment.newInstance("?", "?");
        }

        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    public interface OnFragmentInteractionListener {
    }
}