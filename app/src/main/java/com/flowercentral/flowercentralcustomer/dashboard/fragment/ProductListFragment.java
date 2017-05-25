package com.flowercentral.flowercentralcustomer.dashboard.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.flowercentral.flowercentralcustomer.R;
import com.flowercentral.flowercentralcustomer.common.interfaces.OnFragmentInteractionListener;
import com.flowercentral.flowercentralcustomer.common.interfaces.OnItemClickListener;
import com.flowercentral.flowercentralcustomer.common.model.Product;
import com.flowercentral.flowercentralcustomer.dashboard.adapter.ProductViewAdapter;
import com.flowercentral.flowercentralcustomer.setting.AppConstant;
import com.flowercentral.flowercentralcustomer.util.Util;

import java.util.ArrayList;

public class ProductListFragment extends Fragment implements OnItemClickListener {

    private final String TAG = ProductListFragment.class.getSimpleName ();

    private OnFragmentInteractionListener mListener;
    private int mSelectedView;
    private ArrayList<Product> mProductList;
    private Context mContext;
    private LinearLayout mProductWrapper;
    private RecyclerView mRVProductList;
    private SwipeRefreshLayout mProductRefreshLayout;
    private boolean mInternetAvailable;
    private ProductViewAdapter mProductViewAdapter;
    private LinearLayoutManager mLinearLayoutManager;

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
            if (getArguments () != null) {
                mSelectedView = getArguments ().getInt ("selectedView");
                mProductList = getArguments ().getParcelableArrayList ("products");
            }
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
        mContext = getContext ();
        mProductWrapper = (LinearLayout) view.findViewById (R.id.ll_product_list_wrapper);
        mRVProductList = (RecyclerView) view.findViewById (R.id.rv_product_list);
        mProductRefreshLayout = (SwipeRefreshLayout) view.findViewById (R.id.swipeRefreshProduct);

        mInternetAvailable = Util.checkInternet(mContext);



        //Setup recycler view
        /*if(mSelectedView == AppConstant.VIEW_TYPE.GRID.ordinal ()){
            //Get Adapter
            mProductViewAdapter = new ProductViewAdapter (mContext, mSelectedView, mProductList, this);
            mGridLayoutManager = new GridLayoutManager (mContext,2);
            mRVProductList.setLayoutManager(mGridLayoutManager);

        }else if(mSelectedView == AppConstant.VIEW_TYPE.LIST.ordinal ()){*/
            //Get Adapter
            mProductViewAdapter = new ProductViewAdapter (mContext, mSelectedView, mProductList, this);
            mLinearLayoutManager = new LinearLayoutManager (mContext);
            mRVProductList.setLayoutManager(mLinearLayoutManager);

        /*}else{
            //Get Adapter
            mProductViewAdapter = new ProductViewAdapter (mContext, mSelectedView, mProductList, this);
            mGridLayoutManager = new GridLayoutManager (mContext,2);
            mRVProductList.setLayoutManager(mGridLayoutManager);

        }*/
        mRVProductList.setHasFixedSize(true);
        mRVProductList.setAdapter (mProductViewAdapter);
        mProductViewAdapter.notifyDataSetChanged ();
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


    @Override
    public void onItemClicked (String _type, int _position, Object _data) {
        Toast.makeText (mContext, _type +" "+String.valueOf (_position), Toast.LENGTH_SHORT).show ();
    }

    @Override
    public void onItemDeleted (int _position, Object _data) {
        Toast.makeText (mContext, "Delete is clicked", Toast.LENGTH_SHORT).show ();
    }
}
