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
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

public class EntryDetailsActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CAMERA = 1;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 2;
    private static final String PHOTO_NAME_SEPARATOR = "-";

    private ArrayList<String> photos_indexed = new ArrayList<>();
    private String lastPhotoAbsolutePath;

    private View layout_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        EditText input_number = findViewById(R.id.amount);
        TextView checkbox_paid_label = findViewById(R.id.checkbox_paid_label);
        final PlacesAutocompleteTextView address = findViewById(R.id.places_autocomplete);
        final CheckBox checkbox_paid = (CheckBox) findViewById(R.id.checkbox_paid);
        final TextView date = (TextView) findViewById(R.id.date);
        final TextView time = (TextView) findViewById(R.id.time);
        final Button take_photo = (Button) findViewById(R.id.new_photo);

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

        //TODO do it only if it's new entry
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

        address.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    hideKeyboard();
                }
                return false;
            }
        });

        /* photo */

        layout_main = findViewById(R.id.layout_add_new_entry_details);

        setupExistingPhotos();

        take_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                startCamera();
            } else {
                // Permission request was denied.
                Snackbar.make(layout_main, R.string.permissions_camera_denied,
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
    Count the occurrences of a Char in a String
     */
    public static int count(String s, char c) {
        return s.length() == 0 ? 0 : (s.charAt(0) == c ? 1 : 0) + count(s.substring(1), c);
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

        int entry_id = 65;
        int photo_id = 23;

        String imageFileName = entry_id + PHOTO_NAME_SEPARATOR + photo_id + PHOTO_NAME_SEPARATOR + timeStamp + "_";
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

                Toast.makeText(this, R.string.picture_taken, Toast.LENGTH_SHORT);

            } else {
                Toast.makeText(this, R.string.picture_taken_failure, Toast.LENGTH_LONG);
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

        Bitmap myBitmap = BitmapFactory.decodeFile(absolute_path);

        FrameLayout frameLayout = new FrameLayout(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, getPixelsToDP(90));
        layoutParams.rightMargin = getPixelsToDP(10);
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
        bmOptions.inPurgeable = true;

        imgView.setImageBitmap(myBitmap);
        imgView.setLayoutParams(new android.view.ViewGroup.LayoutParams(250, 250));

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
        lp.setMargins(150, 220, 0, 0);

        ImageButton imageButton = new ImageButton(this);
        imageButton.setLayoutParams(lp);
        imageButton.setImageResource(R.drawable.ic_delete_black_24dp);
        imageButton.setColorFilter(this.getResources().getColor(R.color.red));
        imageButton.setAlpha(0.8f);
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
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

}
