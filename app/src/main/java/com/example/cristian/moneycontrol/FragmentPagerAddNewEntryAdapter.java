package com.example.cristian.moneycontrol;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;

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

    public FragmentPagerAddNewEntryAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0: // Income
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