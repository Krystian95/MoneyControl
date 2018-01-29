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

public class YearlyBalanceFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    GridView grid;
    List<String> years;
    private View view;

    private OnFragmentInteractionListener mListener;

    public YearlyBalanceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment YearlyBalanceFragment.
     */
    public static YearlyBalanceFragment newInstance() {
        YearlyBalanceFragment fragment = new YearlyBalanceFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_yearly_balance, container, false);

        CustomCalendar calendar = new CustomCalendar();

        AppDatabase db = AppDatabase.getAppDatabase(this.getContext());
        Entry[] entries = AppDatabase.getAllEntries(db);

        years = new ArrayList<>();
        List<Float> years_income = new ArrayList<>();
        List<Float> years_expense = new ArrayList<>();

        for (int i = 0; i < entries.length; i++) {
            String year = calendar.getYearFromDate(entries[i].getDate());
            if (!years.contains(year)) {
                years.add(year);
                years_income.add((float) 0);
                years_expense.add((float) 0);
            }

            int index = years.indexOf(year);
            Category category = AppDatabase.getCategoryById(db, String.valueOf(entries[i].getIdCategory()));

            if (category.getType().equals("expense")) {
                float expense = years_expense.get(index);
                expense += entries[i].getAmount();
                years_expense.set(index, expense);
            } else {
                float income = years_income.get(index);
                income += entries[i].getAmount();
                years_income.set(index, income);
            }
        }

        BalanceGrid adapter = new BalanceGrid(view.getContext(), years, years_income, years_expense);
        grid = (GridView) view.findViewById(R.id.gridViewBalanceDaily);
        grid.setAdapter(adapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(view.getContext(), MainActivity.class);
                intent.putExtra("balance", true);
                intent.putExtra("balance_monthly", years.get(position));
                startActivity(intent);
            }
        });

        float total_income_value = 0;
        for (int i = 0; i < years_income.size(); i++) {
            total_income_value += years_income.get(i);
        }

        float total_expense_value = 0;
        for (int i = 0; i < years_expense.size(); i++) {
            total_expense_value += years_expense.get(i);
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
