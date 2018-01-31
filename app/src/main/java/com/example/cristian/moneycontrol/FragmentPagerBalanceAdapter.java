package com.example.cristian.moneycontrol;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class FragmentPagerBalanceAdapter extends FragmentPagerAdapter {

    private final int PAGE_COUNT = 3;
    private String tabTitles[] = new String[]{
            "Giorno",
            "Mese",
            "Anno"
    };

    private String year;
    private String month;

    public FragmentPagerBalanceAdapter(FragmentManager fm, String year, String month) {
        super(fm);
        this.year = year;
        this.month = month;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0: // Daily
                return DailyBalanceFragment.newInstance(year, month);

            case 1: // Monthly
                return MonthlyBalanceFragment.newInstance(year);

            case 2: // Yearly
                return YearlyBalanceFragment.newInstance();
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