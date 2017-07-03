package com.flowercentral.flowercentralcustomer.order.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flowercentral.flowercentralcustomer.R;
import com.flowercentral.flowercentralcustomer.common.model.Product;
import com.flowercentral.flowercentralcustomer.order.adapter.MyOrderItemAdapter;


import java.util.ArrayList;


public class OrderFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private ArrayList<Product> mProducts;
    private Context mContext;

    public OrderFragment () {
    }

    // TODO: Customize parameter initialization

    public static OrderFragment newInstance (int columnCount, ArrayList<Product> _products) {
        OrderFragment fragment = new OrderFragment ();
        Bundle args = new Bundle ();
        args.putInt (ARG_COLUMN_COUNT, columnCount);
        args.putParcelableArrayList ("data", _products);
        fragment.setArguments (args);
        return fragment;
    }

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);

        if (getArguments () != null) {
            mColumnCount = getArguments ().getInt (ARG_COLUMN_COUNT);
            mProducts = getArguments ().getParcelableArrayList ("data");
        }
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        View view = inflater.inflate (R.layout.fragment_order_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext ();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager (new LinearLayoutManager (context));
            } else {
                recyclerView.setLayoutManager (new GridLayoutManager (context, mColumnCount));
            }
            recyclerView.setAdapter (new MyOrderItemAdapter (mContext, mProducts));
        }
        return view;
    }


    @Override
    public void onAttach (Context _context) {
        super.onAttach (_context);
        mContext = _context;
        /*if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException (context.toString ()
                    + " must implement OnListFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach () {
        super.onDetach ();

    }

}
