package com.example.cristian.moneycontrol;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.cristian.moneycontrol.database.AppDatabase;
import com.example.cristian.moneycontrol.database.Category;
import com.example.cristian.moneycontrol.database.Entry;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Iterator;
import java.util.Map;

public class MapFragment extends BaseGoogleMapsFragment {

    private Context mContext;
    private AppDatabase db;
    private Map map_entries;
    private Map map_categories;
    private GPSLocator gps_locator;

    private OnFragmentInteractionListener mListener;

    public MapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MapFragment.
     */
    public static MapFragment newInstance() {
        MapFragment fragment = new MapFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mContext = getContext();
        db = AppDatabase.getAppDatabase(this.getContext());

        setupMap(googleMap);

        gps_locator = new GPSLocator(mContext);

        Utils utils = new Utils();

        LatLng init_coords = gps_locator.getLocationFromAddress(mContext, "Italy");
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(init_coords, 5));

        map_entries = utils.buildEntriesMap(mContext);
        map_categories = utils.buildCategoriesMap(mContext);
        Category category;
        LatLng coords;

        Iterator it = map_entries.values().iterator();

        while (it.hasNext()) {

            Entry entry = (Entry) it.next();

            if (!entry.getAddress().isEmpty()) {
                category = (Category) map_categories.get(entry.getIdCategory());
                coords = gps_locator.getLocationFromAddress(mContext, entry.getAddress());

                String amount = Utils.formatNumber(entry.getAmount()) + " €";
                if (category.getType().equals("expense")) {
                    amount = "-" + amount;
                }

                Marker marker = googleMap.addMarker(new MarkerOptions()
                        .draggable(true)
                        .position(coords)
                        .title(category.getName())
                        .snippet(entry.getDate() + " " + entry.getTime() + "\n" + amount)
                        .icon(bitmapDescriptorFromVector(mContext, category.getIcon()))
                );

                String id_entry = String.valueOf(entry.getIdEntry());
                marker.setTag(id_entry);
            }
        }

        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                LinearLayout info = new LinearLayout(mContext);
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(mContext);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                int id_entry = Integer.parseInt(marker.getTag().toString());
                Entry entry = (Entry) map_entries.get(id_entry);
                Category category = (Category) map_categories.get(entry.getIdCategory());

                if (category.getType().equals("expense")) {
                    title.setTextColor(getResources().getColor(R.color.red));
                } else {
                    title.setTextColor(getResources().getColor(R.color.green));
                }

                TextView snippet = new TextView(mContext);
                snippet.setTextColor(Color.GRAY);
                snippet.setGravity(Gravity.CENTER_HORIZONTAL);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });

        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            public void onInfoWindowClick(Marker marker) {
                String id_entry = marker.getTag().toString();
                Intent intent = new Intent(mContext, EntryDetailsActivity.class);
                intent.putExtra("entry_id", id_entry);
                startActivity(intent);
            }
        });

        googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker arg0) {
            }

            @SuppressWarnings("unchecked")
            @Override
            public void onMarkerDragEnd(Marker marker) {

                Entry entry = (Entry) map_entries.get(Integer.valueOf((String) marker.getTag()));

                gps_locator.setLatitude(marker.getPosition().latitude);
                gps_locator.setLongitude(marker.getPosition().longitude);

                String addressLine = gps_locator.getAddressLine(mContext);
                entry.setAddress(addressLine);

                AppDatabase.updateEntry(db, entry);
            }

            @Override
            public void onMarkerDrag(Marker arg0) {
            }
        });
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
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
