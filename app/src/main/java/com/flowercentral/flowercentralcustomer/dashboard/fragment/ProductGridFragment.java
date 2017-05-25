package com.flowercentral.flowercentralcustomer.dashboard.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import java.util.List;

public class ProductGridFragment extends Fragment implements OnItemClickListener{

    private final String TAG = ProductGridFragment.class.getSimpleName ();

    private OnFragmentInteractionListener mListener;
    private Context mContext;
    private LinearLayout mProductWrapper;
    private RecyclerView mRVProductList;
    private SwipeRefreshLayout mProductRefreshLayout;
    private boolean mInternetAvailable;

    private List<Product> mProductList;
    private GridLayoutManager mGridLayoutManager;
    private int mSelectedView;
    private ProductViewAdapter mProductViewAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    public ProductGridFragment () {
        // Required empty public constructor
    }

    public static ProductGridFragment newInstance (Bundle _args) {
        ProductGridFragment fragment = new ProductGridFragment ();
        fragment.setArguments (_args);
        return fragment;
    }

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        if (getArguments () != null) {
            mSelectedView = getArguments ().getInt ("selectedView");
            mProductList = getArguments ().getParcelableArrayList ("products");
        }
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate (R.layout.fragment_product_grid, container, false);
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
        if(mSelectedView == AppConstant.VIEW_TYPE.GRID.ordinal ()){
            //Get Adapter
            mProductViewAdapter = new ProductViewAdapter (mContext, mSelectedView, mProductList, this);
            mGridLayoutManager = new GridLayoutManager (mContext,2);
            mRVProductList.setLayoutManager(mGridLayoutManager);

        } else if(mSelectedView == AppConstant.VIEW_TYPE.LIST.ordinal ()){
            //Get Adapter
            mProductViewAdapter = new ProductViewAdapter (mContext, mSelectedView, mProductList, this);
            mLinearLayoutManager = new LinearLayoutManager(mContext);
            mRVProductList.setLayoutManager(mLinearLayoutManager);

        }else{
            //Get Adapter
            mProductViewAdapter = new ProductViewAdapter (mContext, mSelectedView, mProductList, this);
            mGridLayoutManager = new GridLayoutManager (mContext,2);
            mRVProductList.setLayoutManager(mGridLayoutManager);

        }
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
        if(!TextUtils.isEmpty (_type) && _type.trim ().equalsIgnoreCase ("item")){
            Product product = (Product) _data;
            if(mListener != null){
                Bundle args = new Bundle ();
                args.putInt ("action", AppConstant.ACTIONS.PRODUCT_DETAILS.ordinal ());
                args.putParcelable ("data", product);
                mListener.onFragmentInteraction (args);
            }
        }else if(!TextUtils.isEmpty (_type) && _type.trim ().equalsIgnoreCase ("item")){

        }else if(!TextUtils.isEmpty (_type) && _type.trim ().equalsIgnoreCase ("item")){

        }else if(!TextUtils.isEmpty (_type) && _type.trim ().equalsIgnoreCase ("item")){

        }
    }

    @Override
    public void onItemDeleted (int _position, Object _data) {
        Toast.makeText (mContext, "Delete is clicked", Toast.LENGTH_SHORT).show ();
    }

}
