package com.example.cristian.moneycontrol;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.seatgeek.placesautocomplete.OnPlaceSelectedListener;
import com.seatgeek.placesautocomplete.PlacesAutocompleteTextView;
import com.seatgeek.placesautocomplete.model.Place;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddNewEntryDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_entry_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        EditText input_number = findViewById(R.id.amount);
        TextView checkbox_paid_label = findViewById(R.id.checkbox_paid_label);
        PlacesAutocompleteTextView address = findViewById(R.id.places_autocomplete);
        final CheckBox checkbox_paid = (CheckBox) findViewById(R.id.checkbox_paid);
        final TextView date = (TextView) findViewById(R.id.date);
        final TextView time = (TextView) findViewById(R.id.time);

        Intent intent = getIntent();

        /* Title */

        String entry_type = "";

        if (intent != null) {
            if (intent.hasExtra("entry_type")) {
                entry_type = intent.getStringExtra("entry_type");
            }
        }

        setTitle(getString(R.string.expense_new_title) + entry_type);

        /* Keyboard show for input number */

        input_number.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        input_number.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
                return false;
            }
        });


        /* Address */

        address.setOnPlaceSelectedListener(
                new OnPlaceSelectedListener() {
                    @Override
                    public void onPlaceSelected(final Place place) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    }
                }
        );

        /* TextView descrizione stato new entry (Pagata, Prevista, Scaduta */

        checkbox_paid_label.setOnTouchListener(new View.OnTouchListener() {

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (checkbox_paid.isChecked()) {
                        checkbox_paid.setChecked(false);
                    } else {
                        checkbox_paid.setChecked(true);
                    }
                }
                return false;
            }
        });

        /* Checkbox */

        checkbox_paid.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                     @Override
                                                     public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                                                         TextView checkbox_paid_label = findViewById(R.id.checkbox_paid_label);
                                                         if (isChecked) {
                                                             checkbox_paid_label.setText(R.string.paid_expense);
                                                         } else {
                                                             String dateTemp = (String) date.getText();
                                                             String timeTemp = (String) time.getText();

                                                             if (dateIsInTheFuture(dateTemp, timeTemp)) {
                                                                 checkbox_paid_label.setText(R.string.future_expense);
                                                             } else {
                                                                 checkbox_paid_label.setText(R.string.overdue_expense);
                                                             }
                                                         }
                                                     }
                                                 }
        );

        /* Datepicker */

        final Calendar c = Calendar.getInstance();
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        int mMonth = c.get(Calendar.MONTH) + 1;
        int mYear = c.get(Calendar.YEAR);

        String selectedDay = String.valueOf(mDay);
        String selectedMonth = String.valueOf(mMonth);
        String selectedYear = String.valueOf(mYear);

        if (mDay < 10) {
            selectedDay = "0" + selectedDay;
        }

        if ((mMonth + 1) < 10) {
            selectedMonth = "0" + selectedMonth;
        }

        date.setText(selectedDay + "/" + selectedMonth + "/" + selectedYear);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(AddNewEntryDetailsActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                                String selectedDay = String.valueOf(dayOfMonth);
                                String selectedMonth = String.valueOf(monthOfYear + 1);
                                String selectedYear = String.valueOf(year);

                                if (dayOfMonth < 10) {
                                    selectedDay = "0" + selectedDay;
                                }

                                if ((monthOfYear + 1) < 10) {
                                    selectedMonth = "0" + selectedMonth;
                                }

                                date.setText(selectedDay + "/" + selectedMonth + "/" + selectedYear);

                                String dateTemp = (String) date.getText();
                                String timeTemp = (String) time.getText();

                                if (!checkbox_paid.isChecked()) {
                                    TextView checkbox_paid_label = findViewById(R.id.checkbox_paid_label);
                                    if (dateIsInTheFuture(dateTemp, timeTemp)) {
                                        checkbox_paid_label.setText(R.string.future_expense);
                                    } else {
                                        checkbox_paid_label.setText(R.string.overdue_expense);
                                    }
                                }

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        /* Timepicker */

        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        time.setText(hour + ":" + minute);

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AddNewEntryDetailsActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                        String selectedHourString = String.valueOf(selectedHour);
                        String selectedMinuteString = String.valueOf(selectedMinute);

                        if (selectedHour < 10) {
                            selectedHourString = "0" + selectedHour;
                        }

                        if (selectedMinute < 10) {
                            selectedMinuteString = "0" + selectedMinute;
                        }

                        time.setText(selectedHourString + ":" + selectedMinuteString);

                        String dateTemp = (String) date.getText();
                        String timeTemp = (String) time.getText();

                        if (!checkbox_paid.isChecked()) {
                            TextView checkbox_paid_label = findViewById(R.id.checkbox_paid_label);
                            if (dateIsInTheFuture(dateTemp, timeTemp)) {
                                checkbox_paid_label.setText(R.string.future_expense);
                            } else {
                                checkbox_paid_label.setText(R.string.overdue_expense);
                            }
                        }
                    }
                }, hour, minute, true);
                mTimePicker.show();

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

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    private boolean dateIsInTheFuture(String date, String time) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm");
        Date strDate = null;
        try {
            strDate = sdf.parse(date + " " + time);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
        if (new Date().after(strDate)) {
            return false;
        } else {
            return true;
        }
    }


}
