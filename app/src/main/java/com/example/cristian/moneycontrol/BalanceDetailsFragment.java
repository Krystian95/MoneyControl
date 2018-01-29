package com.example.cristian.moneycontrol;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

public class BalanceDetailsFragment extends Fragment implements
        FragmentPagerBalanceAdapter.OnFragmentInteractionListener,
        YearlyBalanceFragment.OnFragmentInteractionListener {

    private static final String ARG_TYPE = "type";
    private static final String ARG_YEAR = "year";
    private static final String ARG_MONTH = "month";

    private String type;
    private String year;
    private String month;

    private OnFragmentInteractionListener mListener;

    public BalanceDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param type Parameter 1.
     * @param year Parameter 2.
     * @return A new instance of fragment BalanceDetailsFragment.
     */
    public static BalanceDetailsFragment newInstance(String type, String year, String month) {
        BalanceDetailsFragment fragment = new BalanceDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type);
        args.putString(ARG_YEAR, year);
        args.putString(ARG_MONTH, month);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            type = getArguments().getString(ARG_TYPE);
            year = getArguments().getString(ARG_YEAR);
            month = getArguments().getString(ARG_MONTH);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_balance, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_balance_details, container, false);

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager_balance);
        viewPager.setAdapter(new FragmentPagerBalanceAdapter(this.getFragmentManager(), year, month));

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.sliding_tabs_balance);
        tabLayout.setupWithViewPager(viewPager);

        int current = 1;

        if (this.type != null) {
            switch (this.type) {
                case "daily":
                    current = 0;
                    break;
                case "monthly":
                    current = 1;
                    break;
            }
        }

        viewPager.setCurrentItem(current);

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

    @Override
    public void onFragmentInteraction(Uri uri) {

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
