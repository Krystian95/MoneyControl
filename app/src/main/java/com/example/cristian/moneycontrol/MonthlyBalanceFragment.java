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

public class MonthlyBalanceFragment extends Fragment {

    private static final String ARG_CURRENT_YEAR = "currentYear";

    private String currentYear;

    GridView grid;
    List<String> months;
    List<Float> months_income;
    List<Float> months_expense;

    private OnFragmentInteractionListener mListener;

    public MonthlyBalanceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param currentYear Parameter 1.
     * @return A new instance of fragment MonthlyBalanceFragment.
     */
    public static MonthlyBalanceFragment newInstance(String currentYear) {
        MonthlyBalanceFragment fragment = new MonthlyBalanceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CURRENT_YEAR, currentYear);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentYear = getArguments().getString(ARG_CURRENT_YEAR);
        }
    }

    private void setupArrays() {

        this.months = new ArrayList<>();
        this.months_income = new ArrayList<>();
        this.months_expense = new ArrayList<>();

        months.add("Gen");
        months.add("Feb");
        months.add("Mar");
        months.add("Apr");
        months.add("Mag");
        months.add("Giu");
        months.add("Lug");
        months.add("Ago");
        months.add("Set");
        months.add("Ott");
        months.add("Nov");
        months.add("Dic");

        for (int i = 0; i < this.months.size(); i++) {
            this.months_income.add((float) 0);
            this.months_expense.add((float) 0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_monthly_balance, container, false);

        CustomCalendar calendar = new CustomCalendar();

        AppDatabase db = AppDatabase.getAppDatabase(this.getContext());
        Entry[] entries = AppDatabase.getAllEntries(db);

        if (this.currentYear == null) {
            this.currentYear = calendar.getCurrentYear();
        }

        setupArrays();

        for (int i = 0; i < entries.length; i++) {

            String year = calendar.getYearFromDate(entries[i].getDate());

            if (year.equals(this.currentYear)) {
                int month = calendar.getMonthFromDate(entries[i].getDate());
                month--;
                Category category = AppDatabase.getCategoryById(db, String.valueOf(entries[i].getIdCategory()));

                if (category.getType().equals("expense")) {
                    float expense = months_expense.get(month);
                    expense += entries[i].getAmount();
                    months_expense.set(month, expense);
                } else {
                    float income = months_income.get(month);
                    income += entries[i].getAmount();
                    months_income.set(month, income);
                }
            }
        }

        BalanceGrid adapter = new BalanceGrid(view.getContext(),"month", months, months_income, months_expense);
        grid = (GridView) view.findViewById(R.id.gridViewBalanceDaily);
        grid.setAdapter(adapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(view.getContext(), MainActivity.class);
                intent.putExtra("balance", true);
                intent.putExtra("balance_daily", position + 1);
                intent.putExtra("year", currentYear);
                startActivity(intent);
            }
        });

        TextView title = (TextView) view.findViewById(R.id.total_label);
        title.setText(this.currentYear);

        float total_income_value = 0;
        for (int i = 0; i < months_income.size(); i++) {
            total_income_value += months_income.get(i);
        }

        float total_expense_value = 0;
        for (int i = 0; i < months_expense.size(); i++) {
            total_expense_value += months_expense.get(i);
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
