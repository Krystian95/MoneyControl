package com.example.cristian.moneycontrol;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

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
import java.util.Random;

public class AddNewEntryDetailsActivity extends AppCompatActivity {

    protected static final int REQUEST_TAKE_PHOTO = 0;
    protected static final int PERMISSION_REQUEST_CAMERA = 1;
    protected static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 2;
    private static final String PHOTO_NAME_SEPARATOR = "-";

    ImageView mImageView;
    String lastPhotoAbsolutePath;

    private View mLayout;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_entry_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        EditText input_number = findViewById(R.id.amount);
        TextView checkbox_paid_label = findViewById(R.id.checkbox_paid_label);
        final PlacesAutocompleteTextView address = findViewById(R.id.places_autocomplete);
        final CheckBox checkbox_paid = (CheckBox) findViewById(R.id.checkbox_paid);
        final TextView date = (TextView) findViewById(R.id.date);
        final TextView time = (TextView) findViewById(R.id.time);
        final Button take_photo = (Button) findViewById(R.id.new_photo);
        mImageView = findViewById(R.id.image_view_test);

        Intent intent = getIntent();

        /* Title */

        String entry_type = "";

        if (intent != null) {
            if (intent.hasExtra("entry_type")) {
                entry_type = intent.getStringExtra("entry_type");
            }
        }

        setTitle(getString(R.string.expense_new_title) + " " + entry_type);

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

        address.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    hideKeyboard();
                }
                return false;
            }
        });

        /* photo */

        mLayout = findViewById(R.id.layout_add_new_entry_details);

        setupExistingPhotos();

        take_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("PHOTO BUTTON", "CLICKED");
                //dispatchTakePictureIntent();
                //createThumbsList();
                startCameraAction();
            }
        });
    }

    /*
    Setup the thumbs list for the existing photos of the current entry
     */
    private void setupExistingPhotos() {

        ArrayList<File> photos = new ArrayList<File>();
        File path = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        String photo_id = "23";

        photos = filterFileInFolder(path, photo_id);
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
                // Permission has been granted. Start camera preview Activity.
                Snackbar.make(mLayout, "Camera permission was granted. Starting preview.",
                        Snackbar.LENGTH_SHORT)
                        .show();
                startCamera();
            } else {
                // Permission request was denied.
                Snackbar.make(mLayout, "Camera permission request was denied.",
                        Snackbar.LENGTH_SHORT)
                        .show();
            }
        }
    }

    /*
    Check if the app has the permission to use the camera, if not it lunch the request
     */
    private void startCameraAction() {

        // Check if the Camera permission has been granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            // Permission is already available, start camera preview
            Snackbar.make(mLayout,
                    "Camera permission is available. Starting preview.",
                    Snackbar.LENGTH_SHORT).show();
            startCamera();
        } else {
            // Permission is missing and must be requested.
            requestCameraPermission();
        }
    }

    /*
    Returns all File in the photo folder that match with the filter (photo's id)
     */
    public ArrayList<File> filterFileInFolder(File dir, String photo_id) {

        ArrayList<File> photos = new ArrayList<File>();

        if (dir.isDirectory()) {

            File[] list = dir.listFiles();

            for (int i = 0; i < list.length; i++) {

                String fileName = list[i].getName();

                if (count(fileName, PHOTO_NAME_SEPARATOR.charAt(0)) == 2) {

                    String[] separated = fileName.split(PHOTO_NAME_SEPARATOR);

                    if (separated[1].equals(photo_id)) {
                        photos.add(list[i]);
                        Log.e("Photo Found", photos.get(i).getAbsolutePath());
                    }
                }
            }
        }

        return photos;
    }

    /*
    Count the occurences of a Char in a String
     */
    public static int count(String s, char c) {
        return s.length() == 0 ? 0 : (s.charAt(0) == c ? 1 : 0) + count(s.substring(1), c);
    }

    /*
    Request the camera permission
     */
    private void requestCameraPermission() {
        // Permission has not been granted and must be requested.
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with a button to request the missing permission.
            Snackbar.make(mLayout, "Camera access is required to display the camera preview.",
                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Request the permission
                    ActivityCompat.requestPermissions(AddNewEntryDetailsActivity.this,
                            new String[]{Manifest.permission.CAMERA},
                            PERMISSION_REQUEST_CAMERA);
                }
            }).show();

        } else {
            Snackbar.make(mLayout,
                    "Permission is not available. Requesting camera permission.",
                    Snackbar.LENGTH_SHORT).show();
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
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        }
    }

    /*
    Create the file image where to put the taken photo
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        int entry_id = 65;
        int photo_id = 23;

        String imageFileName = entry_id + PHOTO_NAME_SEPARATOR + photo_id + PHOTO_NAME_SEPARATOR + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        // Save a file: path for use with ACTION_VIEW intents
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

                Toast.makeText(this, "Picture was taken", Toast.LENGTH_SHORT);

            } else {
                Toast.makeText(this, "Picture was not taken", Toast.LENGTH_LONG);
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
    Add a single thumb to the tumbs list
     */
    private void addThumb(File photo, LinearLayout linearLayout){

        Bitmap myBitmap = BitmapFactory.decodeFile(photo.getAbsolutePath());

        FrameLayout frameLayout = new FrameLayout(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, getPixelsToDP(90));
        layoutParams.rightMargin = getPixelsToDP(10);
        frameLayout.setLayoutParams(layoutParams);

        final ImageView imgView = new ImageView(this);
        LinearLayout.LayoutParams lpImage = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        imgView.setImageBitmap(myBitmap);
        //imgView.setImageResource(R.drawable.ic_account_balance_black_24dp);
        imgView.setLayoutParams(new android.view.ViewGroup.LayoutParams(250, 250));
        imgView.setMaxHeight(200);
        imgView.setMaxWidth(200);

        //TODO replace random id
        Random r = new Random();
        int id = r.nextInt(999) + 1;
        imgView.setId(id);

        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO replace with image opening
                Log.e("TOUCHED", "Image# " + v.getId());
            }
        });

        frameLayout.addView(imgView);
        linearLayout.addView(frameLayout);
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
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(lastPhotoAbsolutePath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(lastPhotoAbsolutePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(lastPhotoAbsolutePath, bmOptions);
        mImageView.setImageBitmap(bitmap);
    }

}
