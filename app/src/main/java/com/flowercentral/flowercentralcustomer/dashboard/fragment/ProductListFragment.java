package com.flowercentral.flowercentralcustomer.dashboard.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flowercentral.flowercentralcustomer.R;
import com.flowercentral.flowercentralcustomer.common.interfaces.OnFragmentInteractionListener;

public class ProductListFragment extends Fragment {

    private final String TAG = ProductListFragment.class.getSimpleName ();

    private OnFragmentInteractionListener mListener;

    public ProductListFragment () {
        // Required empty public constructor
    }

    public static ProductListFragment newInstance (Bundle _args) {
        ProductListFragment fragment = new ProductListFragment ();
        fragment.setArguments (_args);
        return fragment;
    }

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        if (getArguments () != null) {

        }
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate (R.layout.fragment_product_list, container, false);
    }

    @Override
    public void onViewCreated (View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated (view, savedInstanceState);
    }

    @Override
    public void onAttach (Context context) {
        super.onAttach (context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException (context.toString ()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach () {
        super.onDetach ();
        mListener = null;
    }

}
