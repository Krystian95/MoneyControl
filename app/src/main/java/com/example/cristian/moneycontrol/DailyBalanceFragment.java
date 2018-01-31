package com.example.cristian.moneycontrol;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.example.cristian.moneycontrol.database.AppDatabase;
import com.example.cristian.moneycontrol.database.Category;
import com.example.cristian.moneycontrol.database.Entry;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.List;

public class DailyBalanceFragment extends Fragment {

    private static final String ARG_YEAR = "year";
    private static final String ARG_MONTH = "month";

    private String year;
    private String month;

    private OnFragmentInteractionListener mListener;

    GridView grid;
    List<String> days;
    List<Float> days_income;
    List<Float> days_expense;

    public DailyBalanceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param year  Parameter 1.
     * @param month Parameter 2.
     * @return A new instance of fragment DailyBalanceFragment.
     */
    public static DailyBalanceFragment newInstance(String year, String month) {
        DailyBalanceFragment fragment = new DailyBalanceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_YEAR, year);
        args.putString(ARG_MONTH, month);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            year = getArguments().getString(ARG_YEAR);
            month = getArguments().getString(ARG_MONTH);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_daily_balance, container, false);

        CustomCalendar calendar = new CustomCalendar();

        AppDatabase db = AppDatabase.getAppDatabase(this.getContext());
        Entry[] entries = AppDatabase.getAllEntries(db);

        if (this.year == null || this.month == null) {
            this.year = calendar.getCurrentYear();
            this.month = calendar.getCurrentMonth();
        } else if (Integer.valueOf(this.month) < 10) {
            this.month = "0" + this.month;
        }

        setupArrays();

        for (int i = 0; i < entries.length; i++) {

            String year = calendar.getYearFromDate(entries[i].getDate());
            int month = calendar.getMonthFromDate(entries[i].getDate());

            if (year.equals(this.year) && Integer.valueOf(this.month) == month) {

                int day = calendar.getDayFromDate(entries[i].getDate());
                day--;

                Category category = AppDatabase.getCategoryById(db, String.valueOf(entries[i].getIdCategory()));

                if (category.getType().equals("expense")) {
                    float expense = days_expense.get(day);
                    expense += entries[i].getAmount();
                    days_expense.set(day, expense);
                } else {
                    float income = days_income.get(day);
                    income += entries[i].getAmount();
                    days_income.set(day, income);
                }
            }
        }

        BalanceGrid adapter = new BalanceGrid(view.getContext(), "day", days, days_income, days_expense);
        grid = (GridView) view.findViewById(R.id.gridViewBalanceDaily);
        grid.setAdapter(adapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(view.getContext(), SingleDayActivity.class);
                intent.putExtra("day", position + 1);
                intent.putExtra("month", month);
                intent.putExtra("year", year);
                startActivity(intent);
            }
        });

        TextView title = (TextView) view.findViewById(R.id.month_year_current);
        String title_text = calendar.getMonthByNumber(Integer.parseInt(this.month)) + " " + this.year;
        title.setText(title_text);

        float total_income_value = 0;
        for (int i = 0; i < days_income.size(); i++) {
            total_income_value += days_income.get(i);
        }

        float total_expense_value = 0;
        for (int i = 0; i < days_expense.size(); i++) {
            total_expense_value += days_expense.get(i);
        }

        float total_saving_value = total_income_value - total_expense_value;

        TextView total_income = (TextView) view.findViewById(R.id.total_income);
        total_income.setText(Utils.formatNumber(total_income_value));
        TextView total_expense = (TextView) view.findViewById(R.id.total_expense);
        total_expense.setText(Utils.formatNumber(total_expense_value));
        TextView total_saving = (TextView) view.findViewById(R.id.total_saving);
        total_saving.setText(Utils.formatNumber(total_saving_value));

        List<BarEntry> char_data = new ArrayList<>();
        char_data.add(new BarEntry(3, total_income_value));
        char_data.add(new BarEntry(2, total_expense_value));
        char_data.add(new BarEntry(1, total_saving_value));

        BarDataSet dataSet = new BarDataSet(char_data, "Label"); // add entries to dataset
        dataSet.setColors(new int[]{R.color.green, R.color.red, R.color.light_gray}, getContext());
        dataSet.setDrawValues(false);

        BarData bar_data = new BarData(dataSet);

        HorizontalBarChart chart = (HorizontalBarChart) view.findViewById(R.id.chart);
        chart.animateY(1300);
        chart.setData(bar_data);
        chart.getDescription().setEnabled(false);
        chart.getAxisLeft().setTextColor(Color.GRAY);
        chart.getAxisRight().setDrawLabels(false);
        chart.getXAxis().setDrawLabels(false);
        chart.getLegend().setEnabled(false);
        chart.setTouchEnabled(false);
        chart.invalidate(); // refresh

        return view;
    }

    private void setupArrays() {

        this.days = new ArrayList<>();
        this.days_income = new ArrayList<>();
        this.days_expense = new ArrayList<>();

        CustomCalendar calendar = new CustomCalendar();
        int limit = calendar.getMonthLimit(this.month);

        for (int i = 1; i <= limit; i++) {
            days.add(String.valueOf(i));
            this.days_income.add((float) 0);
            this.days_expense.add((float) 0);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
