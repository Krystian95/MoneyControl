package com.example.cristian.moneycontrol;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.codetroopers.betterpickers.recurrencepicker.RecurrencePickerDialogFragment;
import com.example.cristian.moneycontrol.database.AppDatabase;
import com.example.cristian.moneycontrol.database.Category;
import com.example.cristian.moneycontrol.database.Entry;
import com.example.cristian.moneycontrol.database.Photo;
import com.seatgeek.placesautocomplete.OnPlaceSelectedListener;
import com.seatgeek.placesautocomplete.PlacesAutocompleteTextView;
import com.seatgeek.placesautocomplete.model.Place;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class EntryDetailsActivity extends AppCompatActivity implements RecurrencePickerDialogFragment.OnRecurrenceSetListener {

    private static final int PERMISSION_REQUEST_CAMERA = 1;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 2;
    private static final int PERMISSION_REQUEST_LOCATION = 34;
    private static final int PERMISSION_REQUEST_GPS_SETTINGS = 56;

    private ArrayList<String> photos_indexed = new ArrayList<>();
    private String lastPhotoAbsolutePath;

    private View layout_main;
    private TextView date;
    private TextView time;
    private Switch switch_repeat;
    private EditText amount;
    private PlacesAutocompleteTextView address;
    private EditText description;

    private static final String FRAG_TAG_RECUR_PICKER = "recurrencePickerDialogFragment";
    private boolean isNewEntry = false;

    private String mRrule = null;
    private int category_id;
    private String idEntry;
    private Entry current_entry;
    private Category current_category;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        amount = findViewById(R.id.amount);
        TextView checkbox_paid_label = findViewById(R.id.checkbox_paid_label);
        TextView category_name = findViewById(R.id.category_name);
        ImageView category_image = findViewById(R.id.category_image);
        address = findViewById(R.id.address);
        final CheckBox checkbox_paid = (CheckBox) findViewById(R.id.checkbox_paid);
        description = (EditText) findViewById(R.id.description);
        date = (TextView) findViewById(R.id.date);
        time = (TextView) findViewById(R.id.time);
        switch_repeat = (Switch) findViewById(R.id.switch_repeat);
        final Button take_photo = (Button) findViewById(R.id.new_photo);
        final Button save = (Button) findViewById(R.id.save_entry);

        Intent intent = getIntent();

        /* Title */

        String entry_type = "";

        if (intent != null) {
            db = AppDatabase.getAppDatabase(getApplicationContext());

            if (intent.hasExtra("category_id")) {
                category_id = intent.getIntExtra("category_id", 0);
                Category category = AppDatabase.getCategoryById(db, String.valueOf(category_id));
                category_name.setText(category.getName());
                category_image.setImageResource(category.getIcon());

                if (category.getType().equals("expense")) {
                    entry_type = getString(R.string.Expense);
                } else {
                    entry_type = getString(R.string.Income);
                }

                isNewEntry = true;
            } else if (intent.hasExtra("entry_id")) {
                idEntry = intent.getStringExtra("entry_id");

                current_entry = AppDatabase.getEntryById(db, idEntry);
                current_category = AppDatabase.getCategoryById(db, String.valueOf(current_entry.getIdCategory()));

                entry_type = current_category.getName();

                isNewEntry = false;
            }
        }

        Button delete_button = (Button) findViewById(R.id.delete_entry);

        if (isNewEntry) {
            setTitle(getString(R.string.expense_new_title) + " " + entry_type);
            delete_button.setVisibility(View.INVISIBLE);
        } else {
            setTitle(entry_type);
            delete_button.setVisibility(View.VISIBLE);

            category_name.setText(current_category.getName());
            category_image.setImageResource(current_category.getIcon());
            amount.setText(String.valueOf(current_entry.getAmount()));
            address.setText(current_entry.getAddress());
            description.setText(current_entry.getDescription());
            date.setText(current_entry.getDate());
            time.setText(current_entry.getTime());
            String mRrule_temp = current_entry.getRecurrenceRule();

            if (mRrule_temp.equals("null") || mRrule_temp == null || mRrule_temp.equals("")) {
                mRrule = null;
                switch_repeat.setChecked(false);
            } else {
                mRrule = mRrule_temp;
                switch_repeat.setChecked(true);
            }
        }

        layout_main = findViewById(R.id.layout_add_new_entry_details);

        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AppDatabase.deleteEntryById(db, idEntry);

                Toast.makeText(v.getContext(), getString(R.string.entry_deleted), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(EntryDetailsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        /* Keyboard show for amount input number */

        if (isNewEntry) {
            amount.requestFocus();
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        } else {
            amount.clearFocus();
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }

        amount.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
                return false;
            }
        });

        /* Datepicker */

        if (isNewEntry) {
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
        }

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(EntryDetailsActivity.this,
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
                                    if (dateTimeIsInTheFuture(dateTemp, timeTemp)) {
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

        if (isNewEntry) {
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);

            String selectedHourString = String.valueOf(hour);
            String selectedMinuteString = String.valueOf(minute);

            if (hour < 10) {
                selectedHourString = "0" + hour;
            }

            if (minute < 10) {
                selectedMinuteString = "0" + minute;
            }

            time.setText(selectedHourString + ":" + selectedMinuteString);
        }

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(EntryDetailsActivity.this, new TimePickerDialog.OnTimeSetListener() {
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
                            if (dateTimeIsInTheFuture(dateTemp, timeTemp)) {
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

                                                             if (dateTimeIsInTheFuture(dateTemp, timeTemp)) {
                                                                 checkbox_paid_label.setText(R.string.future_expense);
                                                             } else {
                                                                 checkbox_paid_label.setText(R.string.overdue_expense);
                                                             }
                                                         }
                                                     }
                                                 }
        );

        /* Address */

        address.setOnPlaceSelectedListener(
                new OnPlaceSelectedListener() {
                    @Override
                    public void onPlaceSelected(final Place place) {
                        hideKeyboard();
                    }
                }
        );

        address.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                    startLocationAction();
                }
                return false;
            }
        });

        address.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    hideKeyboard();
                }
                return false;
            }
        });

        /* photo */

        setupExistingPhotos();

        take_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCameraAction();
            }
        });

        /* repeat */

        switch_repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (switch_repeat.isChecked()) {
                    FragmentManager fm = getSupportFragmentManager();
                    Bundle bundle = new Bundle();
                    android.text.format.Time time = new android.text.format.Time();
                    time.setToNow();
                    bundle.putLong(RecurrencePickerDialogFragment.BUNDLE_START_TIME_MILLIS, time.toMillis(false));
                    bundle.putString(RecurrencePickerDialogFragment.BUNDLE_TIME_ZONE, time.timezone);
                    bundle.putString(RecurrencePickerDialogFragment.BUNDLE_RRULE, mRrule);
                    bundle.putBoolean(RecurrencePickerDialogFragment.BUNDLE_HIDE_SWITCH_BUTTON, true);

                    RecurrencePickerDialogFragment rpd = (RecurrencePickerDialogFragment) fm.findFragmentByTag(FRAG_TAG_RECUR_PICKER);
                    if (rpd != null) {
                        rpd.dismiss();
                    }
                    rpd = new RecurrencePickerDialogFragment();
                    rpd.setArguments(bundle);
                    rpd.setOnRecurrenceSetListener(EntryDetailsActivity.this);
                    rpd.show(fm, FRAG_TAG_RECUR_PICKER);
                }
            }
        });

        /* save */

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Entry entry = new Entry();

                if (!isNewEntry) {
                    entry = current_entry;
                } else {
                    entry.setIdCategory(category_id);
                }

                String date_raw = date.getText().toString();
                CustomCalendar calendar = new CustomCalendar();
                String date_time = calendar.convertToDateFormat(date_raw) + " " + time.getText().toString() + ":00";

                Float amount = Float.parseFloat("0");

                if (EntryDetailsActivity.this.amount.getText() != null && !EntryDetailsActivity.this.amount.getText().toString().isEmpty()) {
                    amount = Float.parseFloat(EntryDetailsActivity.this.amount.getText().toString());
                }

                entry.setAmount(amount);
                entry.setDateTime(date_time);
                entry.setDescription(description.getText().toString());
                entry.setAddress(address.getText().toString());

                if (switch_repeat.isChecked()) {
                    entry.setRecurrenceRule(mRrule);
                } else {
                    entry.setRecurrenceRule("null");
                }

                long new_entry_insered;

                if (isNewEntry) {
                    new_entry_insered = AppDatabase.insertEntry(db, entry);
                } else {
                    AppDatabase.updateEntry(db, entry);
                    new_entry_insered = entry.getIdEntry();
                }

                setupIdEntryToPhotos(String.valueOf(new_entry_insered));

                Toast.makeText(v.getContext(), getString(R.string.entry_saved_succes), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(EntryDetailsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void startLocationAction() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            getLocation();
        } else {
            requestLocationPermission();
        }

    }

    private void getLocation() {

        GPSTracker gpsTracker = new GPSTracker(EntryDetailsActivity.this);

        if (gpsTracker.canGetLocation()) {
            double lat = gpsTracker.latitude;
            double lon = gpsTracker.longitude;

            if (lat != 0 && lon != 0) {
                String addressLine = gpsTracker.getAddressLine(this);
                address.setText(addressLine);
            }
        } else {
            buildAlertMessageNoGps();
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.gps_disabled)
                .setCancelable(false)
                .setPositiveButton(R.string.Yes, new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),
                                PERMISSION_REQUEST_GPS_SETTINGS);
                    }
                })
                .setNegativeButton(R.string.No, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {

            showSnackbar(R.string.Income, android.R.string.ok,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            startLocationPermissionRequest();
                        }
                    });

        } else {
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            startLocationPermissionRequest();
        }
    }

    private void startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                PERMISSION_REQUEST_LOCATION);
    }

    /**
     * Shows a {@link Snackbar}.
     *
     * @param mainTextStringId The id for the string resource for the Snackbar text.
     * @param actionStringId   The text of the action item.
     * @param listener         The listener associated with the Snackbar action.
     */
    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    /*
    Set the entry id of all photos with id entry "null" with the correct id entry (last insered)
     */
    private void setupIdEntryToPhotos(String id_entry) {

        Photo[] unlinked_photos = AppDatabase.getAllPhotosUnlinked(db);

        for (Photo unlinked_photo : unlinked_photos) {
            AppDatabase.updateIdEntryByAbsolutePath(db, unlinked_photo.getAbsolute_path(), id_entry);
        }
    }

    /*
    Setup the thumbs list for the existing photos of the current entry
     */
    private void setupExistingPhotos() {
        ArrayList<File> photos;
        photos = getEntryPhotos();
        createThumbsList(photos);
    }

    @Override
    /*
    Handle actions after permission request
     */
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            // Request for camera permission.
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                // Permission request was denied.
                Snackbar.make(layout_main, R.string.permissions_camera_denied,
                        Snackbar.LENGTH_SHORT)
                        .show();
            }
        } else if (requestCode == PERMISSION_REQUEST_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                // Permission request was denied.
                Snackbar.make(layout_main, R.string.permissions_location_denied,
                        Snackbar.LENGTH_SHORT)
                        .show();
            }
        }
    }

    /*
    Check if the app has the permission to use the camera, if not it lunch the request
     */
    private void startCameraAction() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            requestCameraPermission();
        }
    }

    /*
    Request the camera permission
     */
    private void requestLocationPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)) {

            Snackbar.make(layout_main, R.string.permission_location_required,
                    Snackbar.LENGTH_INDEFINITE).setAction(R.string.Ok, new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ActivityCompat.requestPermissions(EntryDetailsActivity.this,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                            PERMISSION_REQUEST_LOCATION);
                }
            }).show();

        } else {
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSION_REQUEST_LOCATION);
        }
    }

    /*
    Returns all File in the photo folder that match with the filter (photo's id)
     */
    public ArrayList<File> getEntryPhotos() {

        ArrayList<File> photos = new ArrayList<>();

        Photo[] photos_path = AppDatabase.getPhotosByEntryId(db, idEntry);

        for (Photo photo : photos_path) {
            File photo_temp = new File(photo.getAbsolute_path());
            photos.add(photo_temp);
        }

        return photos;
    }

    /*
    Request the camera permission
     */
    private void requestCameraPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {

            Snackbar.make(layout_main, R.string.permission_camera_required,
                    Snackbar.LENGTH_INDEFINITE).setAction(R.string.Ok, new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ActivityCompat.requestPermissions(EntryDetailsActivity.this,
                            new String[]{Manifest.permission.CAMERA},
                            PERMISSION_REQUEST_CAMERA);
                }
            }).show();

        } else {
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    PERMISSION_REQUEST_CAMERA);
        }
    }

    /*
    Start the camera activity
     */
    private void startCamera() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            File photoFile = null;
            try {
                photoFile = createImageFile();

                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this,
                            "com.example.android.fileprovider",
                            photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                }
            } catch (IOException ex) {

            }
        }
    }

    /*
    Create the file image where to put the taken photo
     */
    private File createImageFile() throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        String imageFileName = timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        lastPhotoAbsolutePath = image.getAbsolutePath();
        return image;
    }

    /*
    Called once returned from the camera activity
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {

                LinearLayout linearLayout = findViewById(R.id.thumb_container);
                File photo = new File(lastPhotoAbsolutePath);
                addThumb(photo, linearLayout);

                Photo new_photo = new Photo();
                new_photo.setAbsolute_path(lastPhotoAbsolutePath);
                new_photo.setIdEntry("null");

                if (!isNewEntry) {
                    new_photo.setIdEntry(idEntry);
                }

                AppDatabase.insertPhoto(db, new_photo);

                Toast.makeText(this, R.string.picture_taken, Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this, R.string.picture_taken_failure, Toast.LENGTH_SHORT).show();
            }
        } else {
            if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
                deleteFileCustom(lastPhotoAbsolutePath);
            }
        }
    }

    /*
    Hide the user's system keyboard
     */
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    /*
    Test if a date and time is in the future or in the past
     */
    private boolean dateTimeIsInTheFuture(String date, String time) {

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

    /*
    Add a single thumb to the thumbs list
     */
    private void addThumb(File photo, LinearLayout linearLayout) {

        photos_indexed.add(photo.getAbsolutePath());

        final String absolute_path = photo.getAbsolutePath();

        //TODO add to database

        Bitmap myBitmap = BitmapFactory.decodeFile(absolute_path);

        FrameLayout frameLayout = new FrameLayout(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, getPixelsToDP(90));
        frameLayout.setLayoutParams(layoutParams);

        final ImageView imgView = new ImageView(this);

        // Dimensions of the View
        int viewHeight = 200;
        int viewWidth = 200;

        imgView.setMaxHeight(viewHeight);
        imgView.setMaxWidth(viewWidth);

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photo.getAbsolutePath(), bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / viewWidth, photoH / viewHeight);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        imgView.setImageBitmap(myBitmap);
        imgView.setLayoutParams(new android.view.ViewGroup.LayoutParams(200, 250));

        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView imageView = (ImageView) v;
                loadPhoto(imageView);
            }
        });

        frameLayout.addView(imgView);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT, 1);
        lp.addRule(RelativeLayout.ALIGN_BASELINE, 1);
        lp.setMargins(120, 220, 0, 0);

        ImageButton imageButton = new ImageButton(this);
        imageButton.setLayoutParams(lp);
        imageButton.setImageResource(R.drawable.ic_icons8_elimina);
        imageButton.setBackground(null);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePhotoAlert(v.getContext(), absolute_path);
            }
        });

        frameLayout.addView(imageButton);

        linearLayout.addView(frameLayout);
    }

    /*
    Remove a specific index from the thumbs' layout
     */
    private void removeThumb(int index) {

        LinearLayout linearLayout = findViewById(R.id.thumb_container);
        linearLayout.removeViewAt(index);
    }

    /*
    Show the alert before image delete
     */
    private void deletePhotoAlert(final Context context, final String photo_path) {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage(R.string.alert_delete_photo);
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                R.string.Yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                        if (!deleteFileCustom(photo_path)) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage(R.string.delete_file_failure)
                                    .setCancelable(false)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();
                        } else {

                            /* remove the thumb from the thumbs list and from the indexed photos */

                            int indexToRemove = photos_indexed.indexOf(photo_path);

                            removeThumb(indexToRemove);
                            photos_indexed.remove(indexToRemove);

                            AppDatabase.deletePhotoByAbsolutePath(db, photo_path);

                            Toast.makeText(context, R.string.photo_deleted, Toast.LENGTH_SHORT).show();
                        }

                    }
                });

        builder1.setNegativeButton(
                R.string.No,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    /*
    Delete a specific file, input given its full absolute path
     */
    private boolean deleteFileCustom(String photo_path) {

        File file = new File(photo_path);

        AppDatabase.deletePhotoByAbsolutePath(db, photo_path);

        if (file.exists()) {
            file.delete();
            return true;
        }

        return false;
    }

    /*
    Show a popup dialog with the full image shown
     */
    private void loadPhoto(ImageView imageView) {

        ImageView tempImageView = imageView;

        final Dialog dialog = new Dialog(this) {
            @Override
            public boolean onTouchEvent(MotionEvent event) {
                this.dismiss();
                return true;
            }
        };

        dialog.setCancelable(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_fullimage_dialog);
        ImageView image = (ImageView) dialog.findViewById(R.id.fullimage);
        image.setImageDrawable(tempImageView.getDrawable());
        dialog.show();
        dialog.setCanceledOnTouchOutside(true);
    }

    /*
    Call the method addThumb() foreach thumb to show
     */
    private void createThumbsList(ArrayList<File> photos) {

        LinearLayout linearLayout = findViewById(R.id.thumb_container);

        for (int i = 0; i < photos.size(); i++) {
            addThumb(photos.get(i), linearLayout);
        }
    }

    /*
    Convert dp to pixel to print thumb correct aspect ratio
     */
    private int getPixelsToDP(int dp) {
        float scale = getResources().getDisplayMetrics().density;
        int pixels = (int) (dp * scale + 0.5f);
        return pixels;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                deleteTempPhotos();
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public void onRecurrenceSet(String rrule) {
        mRrule = rrule;
    }

    private void deleteTempPhotos() {
        AppDatabase.deleteAllUnlinked(db);
    }

    @Override
    public void onBackPressed() {
        deleteTempPhotos();
        super.onBackPressed();
    }
}
