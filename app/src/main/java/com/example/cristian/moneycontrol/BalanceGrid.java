package com.example.cristian.moneycontrol;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Cristian on 15/01/2018.
 */

public class BalanceGrid extends BaseAdapter {

    private Context mContext;
    List<String> years;
    List<Float> years_income;
    List<Float> years_expense;

    public BalanceGrid(Context c, List<String> years, List<Float> years_income, List<Float> years_expense) {
        mContext = c;
        this.years = years;
        this.years_income = years_income;
        this.years_expense = years_expense;
    }

    @Override
    public int getCount() {
        return years.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View grid;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        grid = new View(mContext);
        grid = inflater.inflate(R.layout.grid_element_balance, null);

        TextView year = (TextView) grid.findViewById(R.id.label_text);
        year.setText(this.years.get(position));

        TextView year_income = (TextView) grid.findViewById(R.id.total_income);
        year_income.setText(Utils.formatNumber(this.years_income.get(position)));

        TextView year_expense = (TextView) grid.findViewById(R.id.total_expense);
        year_expense.setText(Utils.formatNumber(this.years_expense.get(position)));

        return grid;
    }

}
