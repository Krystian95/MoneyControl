package com.example.cristian.moneycontrol;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cristian.moneycontrol.database.AppDatabase;
import com.example.cristian.moneycontrol.database.Category;
import com.example.cristian.moneycontrol.database.Entry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TodayDetailsFragment extends Fragment {

    GridView gridFutureEntries;
    GridView gridTodayEntries;

    private OnFragmentInteractionListener mListener;

    public TodayDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TodayDetailsFragment.
     */
    public static TodayDetailsFragment newInstance() {
        TodayDetailsFragment fragment = new TodayDetailsFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("LongLogTag")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_today_details, container, false);

        /* Add New Expense button */
        ImageView newExpenseImage = view.findViewById(R.id.add_new_expense);
        newExpenseImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    Intent addNewExpenseIntent = new Intent(v.getContext(), AddNewEntryActivity.class);
                    startActivity(addNewExpenseIntent);
                }

                return true;
            }
        });

        /* Today date informations */

        CustomCalendar calendar = new CustomCalendar();
        Map date = calendar.getCurrentDateTime();

        TextView today_number = (TextView) view.findViewById(R.id.today_number);
        TextView today_letter = (TextView) view.findViewById(R.id.today_letter);
        TextView today_month_year = (TextView) view.findViewById(R.id.today_month_year);

        today_number.setText((String) date.get("day_number"));
        today_letter.setText((String) date.get("day_letter"));
        today_month_year.setText((String) date.get("month_year"));

        /* Future Entries */

        AppDatabase db = AppDatabase.getAppDatabase(this.getContext());

        calendar.getActualDateTimeFormatted();

        Entry[] entries = AppDatabase.getAllWithRrule(db);

        final List<String> future_entries_id = new ArrayList<>();

        for (int i = 0; i < entries.length; i++) {

            boolean eventHasFutureOccurrenceas = calendar.eventHasFutureOccurrences(entries[i].getRecurrenceRule(), entries[i].getDate(), entries[i].getTime());

            if (eventHasFutureOccurrenceas) {
                future_entries_id.add(String.valueOf(entries[i].getIdEntry()));
            }
        }

        gridFutureEntries = (GridView) view.findViewById(R.id.gridViewTodayEntriesFuture);

        ViewGroup.LayoutParams layoutParams = gridFutureEntries.getLayoutParams();
        layoutParams.height = getGridViewSuitedHeight(future_entries_id.size());
        gridFutureEntries.setLayoutParams(layoutParams);

        EntriesGrid adapter = new EntriesGrid(view.getContext(), future_entries_id);
        gridFutureEntries.setAdapter(adapter);

        gridFutureEntries.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(view.getContext(), EntryDetailsActivity.class);
                intent.putExtra("entry_id", future_entries_id.get(+position));
                startActivity(intent);
            }
        });

        /* Today Entries */

        entries = AppDatabase.getTodayEntries(db);

        final List<String> today_entries_id = new ArrayList<>();
        float total_expense = 0;
        float total_income = 0;

        for (int i = 0; i < entries.length; i++) {
            today_entries_id.add(String.valueOf(entries[i].getIdEntry()));
            Category category = AppDatabase.getCategoryById(db, String.valueOf(entries[i].getIdCategory()));
            if (category.getType().equals("expense")) {
                total_expense += entries[i].getAmount();
            } else {
                total_income += entries[i].getAmount();
            }
        }

        gridTodayEntries = (GridView) view.findViewById(R.id.gridViewBalanceDaily);

        layoutParams = gridTodayEntries.getLayoutParams();
        layoutParams.height = getGridViewSuitedHeight(today_entries_id.size());
        gridTodayEntries.setLayoutParams(layoutParams);

        adapter = new EntriesGrid(view.getContext(), today_entries_id);
        gridTodayEntries.setAdapter(adapter);

        gridTodayEntries.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(view.getContext(), EntryDetailsActivity.class);
                intent.putExtra("entry_id", today_entries_id.get(+position));
                startActivity(intent);
            }
        });

        TextView total_expense_text = (TextView) view.findViewById(R.id.total_expense);
        total_expense_text.setText(Utils.formatNumber(total_expense));
        TextView total_income_text = (TextView) view.findViewById(R.id.total_income);
        total_income_text.setText(Utils.formatNumber(total_income));

        return view;
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
