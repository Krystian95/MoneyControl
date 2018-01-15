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
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddNewExpenseFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddNewExpenseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddNewExpenseFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    GridView grid;

    String[] web = {
            "Google",
            "Github",
            "Instagram",
            "Facebook",
            "Flickr",
            "Pinterest",
            "Quora",
            "Twitter",
            "Vimeo",
            "WordPress",
            "Youtube",
            "Stumbleupon",
            "SoundCloud",
            "Reddit",
            "Blogger"

    };
    int[] imageId = {
            R.drawable.ic_account_balance_black_24dp,
            R.drawable.ic_account_balance_black_24dp,
            R.drawable.ic_account_balance_black_24dp,
            R.drawable.ic_account_balance_black_24dp,
            R.drawable.ic_account_balance_black_24dp,
            R.drawable.ic_account_balance_black_24dp,
            R.drawable.ic_account_balance_black_24dp,
            R.drawable.ic_account_balance_black_24dp,
            R.drawable.ic_account_balance_black_24dp,
            R.drawable.ic_account_balance_black_24dp,
            R.drawable.ic_account_balance_black_24dp,
            R.drawable.ic_account_balance_black_24dp,
            R.drawable.ic_account_balance_black_24dp,
            R.drawable.ic_account_balance_black_24dp,
            R.drawable.ic_account_balance_black_24dp,

    };

    private OnFragmentInteractionListener mListener;

    public AddNewExpenseFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddNewExpenseFragment.
     */

    public static AddNewExpenseFragment newInstance(String param1, String param2) {
        AddNewExpenseFragment fragment = new AddNewExpenseFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_new_expense, container, false);

        /*button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent addNewEntryDetailsIntent = new Intent(v.getContext(), EntryDetailsActivity.class);
                addNewEntryDetailsIntent.putExtra("entry_type", "Spesa");
                startActivity(addNewEntryDetailsIntent);
            }
        });*/


        CategoryGrid adapter = new CategoryGrid(view.getContext(), web, imageId);
        grid = (GridView) view.findViewById(R.id.gridView1);
        grid.setAdapter(adapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Toast.makeText(AddNewExpenseFragment.this.getContext(), "You Clicked at " + web[+position], Toast.LENGTH_SHORT).show();
                Intent addNewEntryDetailsIntent = new Intent(view.getContext(), EntryDetailsActivity.class);
                addNewEntryDetailsIntent.putExtra("entry_type", "Spesa");
                addNewEntryDetailsIntent.putExtra("category_name", web[+position]);
                startActivity(addNewEntryDetailsIntent);
            }
        });


        return view;
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
