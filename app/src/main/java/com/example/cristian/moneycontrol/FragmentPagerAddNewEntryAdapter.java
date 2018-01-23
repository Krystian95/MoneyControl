package com.example.cristian.moneycontrol;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Cristian on 09/01/2018.
 */

public class FragmentPagerAddNewEntryAdapter extends FragmentPagerAdapter {

    final int PAGE_COUNT = 2;
    private String tabTitles[] = new String[]{
            "Entrata",
            "Spesa"
    };
    private Context context;
    private FragmentManager fm;

    public FragmentPagerAddNewEntryAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.fm = fm;
        this.context = context;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0: // Income
                //fm.beginTransaction().add(R.id.viewpager, new AddNewIncomeFragment()).commit();
                return AddNewIncomeFragment.newInstance("?", "?");

            case 1: // Expense
                return AddNewExpenseFragment.newInstance("?", "?");
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