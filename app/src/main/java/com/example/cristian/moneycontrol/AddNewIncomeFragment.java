package com.example.cristian.moneycontrol;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.cristian.moneycontrol.database.AppDatabase;
import com.example.cristian.moneycontrol.database.Category;

public class AddNewIncomeFragment extends Fragment {

    GridView grid;

    private OnFragmentInteractionListener mListener;

    public AddNewIncomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AddNewIncomeFragment.
     */
    public static AddNewIncomeFragment newInstance(String param1, String param2) {
        AddNewIncomeFragment fragment = new AddNewIncomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_new_income, container, false);

        AppDatabase db = AppDatabase.getAppDatabase(this.getContext());
        Category[] categories = AppDatabase.getIncomeCategories(db);

        final String[] category_name = new String[categories.length];
        final int[] category_icon = new int[categories.length];
        final int[] category_id = new int[categories.length];

        for (int i = 0; i < categories.length; i++) {
            category_name[i] = categories[i].getName();
            category_icon[i] = categories[i].getIcon();
            category_id[i] = categories[i].getIdCategory();
        }

        CategoryGrid adapter = new CategoryGrid(view.getContext(), category_name, category_icon);
        grid = (GridView) view.findViewById(R.id.gridViewCategoryIncome);
        grid.setAdapter(adapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent addNewEntryDetailsIntent = new Intent(view.getContext(), EntryDetailsActivity.class);
                addNewEntryDetailsIntent.putExtra("category_id", category_id[+position]);
                startActivity(addNewEntryDetailsIntent);
            }
        });

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
