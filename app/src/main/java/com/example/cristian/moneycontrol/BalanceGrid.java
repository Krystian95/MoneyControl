package com.example.cristian.moneycontrol;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class BalanceGrid extends BaseAdapter {

    private Context mContext;
    private List<String> fields;
    private List<Float> incomes;
    private List<Float> expenses;
    private String type;
    private String current_day;
    private String current_month;
    private String current_year;
    private CustomCalendar calendar;

    public BalanceGrid(Context c, String type, List<String> fields, List<Float> incomes, List<Float> expenses) {
        mContext = c;
        this.fields = fields;
        this.incomes = incomes;
        this.expenses = expenses;
        this.type = type;

        calendar = new CustomCalendar();
        this.current_day = calendar.getCurrentDay();
        this.current_month = calendar.getCurrentMonth();
        this.current_year = calendar.getCurrentYear();
    }

    @Override
    public int getCount() {
        return fields.size();
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

        TextView field = (TextView) grid.findViewById(R.id.label_text);
        field.setText(this.fields.get(position));

        String printed = this.fields.get(position);

        switch (this.type) {
            case "day":
                if (printed.equals(this.current_day)) {
                    field.setBackgroundColor(mContext.getResources().getColor(R.color.orange));
                }
                break;
            case "month":
                if (printed.equals(calendar.getMonthAbbreviationByNumber(this.current_month))) {
                    field.setBackgroundColor(mContext.getResources().getColor(R.color.orange));
                }
                break;
            case "year":
                if (printed.equals(this.current_year)) {
                    field.setBackgroundColor(mContext.getResources().getColor(R.color.orange));
                }
                break;
        }

        TextView income = (TextView) grid.findViewById(R.id.total_income);
        income.setText(Utils.formatNumber(this.incomes.get(position)));

        TextView expense = (TextView) grid.findViewById(R.id.total_expense);
        expense.setText(Utils.formatNumber(this.expenses.get(position)));

        return grid;
    }

}
