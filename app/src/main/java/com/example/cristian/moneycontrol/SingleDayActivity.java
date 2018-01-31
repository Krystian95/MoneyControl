package com.example.cristian.moneycontrol;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.example.cristian.moneycontrol.database.AppDatabase;
import com.example.cristian.moneycontrol.database.Category;
import com.example.cristian.moneycontrol.database.Entry;

import java.util.ArrayList;
import java.util.List;

public class SingleDayActivity extends AppCompatActivity {

    GridView gridTodayEntries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_day);

        Intent intent = getIntent();

        setTitle(getString(R.string.ricerca_giorno));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CustomCalendar calendar = new CustomCalendar();

        String day = calendar.getCurrentDay();
        String month = calendar.getCurrentMonth();
        String year = calendar.getCurrentYear();

        if (intent != null) {
            if (intent.hasExtra("day") && intent.hasExtra("month") && intent.hasExtra("year")) {
                day = String.valueOf(intent.getIntExtra("day", 01));
                month = intent.getStringExtra("month");
                year = intent.getStringExtra("year");

                if (Integer.valueOf(day) < 10) {
                    day = "0" + day;
                }
            }
        }

        TextView day_label = (TextView) findViewById(R.id.day);
        day_label.setText(day);

        TextView month_label = (TextView) findViewById(R.id.month);
        month_label.setText(calendar.getMonthByNumber(Integer.valueOf(month)));

        TextView year_label = (TextView) findViewById(R.id.year);
        year_label.setText(year);

        String date = year + "-" + month + "-" + day;

        AppDatabase db = AppDatabase.getAppDatabase(getBaseContext());
        Entry[] entries = AppDatabase.getEntriesByDate(db, date);

        final List<String> day_entries_id = new ArrayList<>();
        float total_expense_value = 0;
        float total_income_value = 0;

        for (int i = 0; i < entries.length; i++) {
            day_entries_id.add(String.valueOf(entries[i].getIdEntry()));
            Category category = AppDatabase.getCategoryById(db, String.valueOf(entries[i].getIdCategory()));
            if (category.getType().equals("expense")) {
                total_expense_value += entries[i].getAmount();
            } else {
                total_income_value += entries[i].getAmount();
            }
        }

        float total_saving_value = total_income_value - total_expense_value;

        TextView total_income = (TextView) findViewById(R.id.total_income);
        total_income.setText(Utils.formatNumber(total_income_value));
        TextView total_expense = (TextView) findViewById(R.id.total_expense);
        total_expense.setText(Utils.formatNumber(total_expense_value));
        TextView total_saving = (TextView) findViewById(R.id.total_saving);
        total_saving.setText(Utils.formatNumber(total_saving_value));

        gridTodayEntries = (GridView) findViewById(R.id.gridViewBalanceDaily);

        ViewGroup.LayoutParams layoutParams = gridTodayEntries.getLayoutParams();
        layoutParams.height = getGridViewSuitedHeight(day_entries_id.size());
        gridTodayEntries.setLayoutParams(layoutParams);

        EntriesGrid adapter = new EntriesGrid(getBaseContext(), day_entries_id);
        gridTodayEntries.setAdapter(adapter);

        gridTodayEntries.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(view.getContext(), EntryDetailsActivity.class);
                intent.putExtra("entry_id", day_entries_id.get(+position));
                startActivity(intent);
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private int getGridViewSuitedHeight(int entries_number) {

        int delta = 56;
        int grid_height = delta;

        if (entries_number > 0) {
            grid_height = delta * entries_number;
        }

        if (entries_number > 3) {
            grid_height = delta * 3;
        }

        return getPixelsToDP(grid_height);
    }

    /*
    Convert dp to pixel to print thumb correct aspect ratio
     */
    private int getPixelsToDP(int dp) {
        float scale = getResources().getDisplayMetrics().density;
        int pixels = (int) (dp * scale + 0.5f);
        return pixels;
    }
}
