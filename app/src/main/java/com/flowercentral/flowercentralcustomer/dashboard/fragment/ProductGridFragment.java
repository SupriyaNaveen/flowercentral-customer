package com.flowercentral.flowercentralcustomer.dashboard.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
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
import com.flowercentral.flowercentralcustomer.dao.LocalDAO;
import com.flowercentral.flowercentralcustomer.dashboard.adapter.ProductViewAdapter;
import com.flowercentral.flowercentralcustomer.rest.BaseModel;
import com.flowercentral.flowercentralcustomer.rest.QueryBuilder;
import com.flowercentral.flowercentralcustomer.setting.AppConstant;
import com.flowercentral.flowercentralcustomer.util.Util;
import com.flowercentral.flowercentralcustomer.volley.ErrorData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
            mGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    switch (mProductViewAdapter.getItemViewType(position)) {
                        case ProductViewAdapter.VIEW_TYPE_EMPTY_LIST:
                            return 2;
                        case ProductViewAdapter.VIEW_TYPE_NON_EMPTY_LIST:
                            return 1;
                        default:
                            return 1;
                    }
                }
            });
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

        //Refresh product on Refresh
        mProductRefreshLayout.setOnRefreshListener (new SwipeRefreshLayout.OnRefreshListener () {
            @Override
            public void onRefresh () {
                getProductsFromServer(mContext);
            }
        });

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
        int action = -1;
        if(!TextUtils.isEmpty (_type) && _type.trim ().equalsIgnoreCase ("item")){
            action = AppConstant.ACTIONS.PRODUCT_DETAILS.ordinal ();
        }else if(!TextUtils.isEmpty (_type) && _type.trim ().equalsIgnoreCase ("buy_now")){
            action = AppConstant.ACTIONS.BUY_NOW.ordinal ();
        }else if(!TextUtils.isEmpty (_type) && _type.trim ().equalsIgnoreCase ("like")){
            action = AppConstant.ACTIONS.LIKE.ordinal ();
        }else if(!TextUtils.isEmpty (_type) && _type.trim ().equalsIgnoreCase ("cart")){
            action = AppConstant.ACTIONS.ADD_TO_CART.ordinal ();
        }

        Product product = (Product) _data;
        if(mListener != null){
            Bundle args = new Bundle ();
            args.putInt ("action", action);
            args.putParcelable ("data", product);
            mListener.onFragmentInteraction (args);
        }
    }

    @Override
    public void onItemDeleted (int _position, Object _data) {
        Toast.makeText (mContext, "Delete is clicked", Toast.LENGTH_SHORT).show ();
    }

    //Sort product list by category or price
    public void sort(int _sortingOption){
        if(AppConstant.SORTING_OPTION.BY_CATEGORY.ordinal () == _sortingOption){
            Collections.sort (mProductList, Product.sortByCategory);

        }else if(AppConstant.SORTING_OPTION.BY_PRICE.ordinal () == _sortingOption) {
            Collections.sort (mProductList, Product.sortByPrice);
        }else {

        }
        mProductViewAdapter.notifyDataSetChanged ();
    }

    //Get product by selected category
    private List<Product> getProductListByCategory(List<Product> _products, String _category){
        List<Product> productsByCategory = null;
        if(_products != null){
            productsByCategory = new ArrayList<Product> ();
            for(Product p : _products){
                if(p.getCategory ().equalsIgnoreCase (_category)){
                    productsByCategory.add (p);
                }
            }
        }

        return productsByCategory;
    }

    //Show product by selected category
    public void showProductByCategory(String _selectedCategory){
        List<Product> productsByCategory = null;
        if(_selectedCategory == null){
            productsByCategory = mProductList;
        }else{
            productsByCategory = getProductListByCategory (mProductList, _selectedCategory);
        }

        mProductViewAdapter.setData (productsByCategory);

    }

    //Update product list
    public void updateProductList (List<Product> _productList){
        if(_productList != null){
            mProductList = _productList;
            mProductViewAdapter.notifyDataSetChanged ();
        }
    }

    private void getProductsFromServer (final Context _context) {

        BaseModel<JSONArray> baseModel = new BaseModel<JSONArray> (_context) {

            @Override
            public void onSuccess (int statusCode, Map<String, String> headers, JSONArray _response) {

                if (_response != null & _response.length ()>0) {
                    try {

                        LocalDAO localDAO = new LocalDAO (_context);
                        localDAO.addProducts (_response, false);

                        //update product list
                        JSONArray jsonArrayProducts = localDAO.getProducts ();
                        Gson gson = new Gson ();
                        Type listType = new TypeToken<ArrayList<Product>> () {}.getType();
                        ArrayList<Product> products = gson.fromJson(_response.toString(), listType);
                        mProductList = products;
                        mProductViewAdapter.addAll (mProductList);
                        mProductRefreshLayout.setRefreshing (false);

                    } catch (Exception ex) {
                        Snackbar.make (mProductWrapper, ex.getMessage (), Snackbar.LENGTH_SHORT).show ();
                    }
                } else {
                    Snackbar.make (mProductWrapper, "No response from server", Snackbar.LENGTH_SHORT).show ();
                }

            }

            @Override
            public void onError (ErrorData error) {

                mProductRefreshLayout.setRefreshing (false);
                
                if (error != null) {

                    switch (error.getErrorType ()) {
                        case NETWORK_NOT_AVAILABLE:
                            Snackbar.make (mProductWrapper, getResources ().getString (R.string.msg_internet_unavailable), Snackbar.LENGTH_SHORT).show ();
                            break;
                        case INTERNAL_SERVER_ERROR:
                            Snackbar.make (mProductWrapper, error.getErrorMessage (), Snackbar.LENGTH_SHORT).show ();
                            break;
                        case CONNECTION_TIMEOUT:
                            Snackbar.make (mProductWrapper, error.getErrorMessage (), Snackbar.LENGTH_SHORT).show ();
                            break;
                        case APPLICATION_ERROR:
                            Snackbar.make (mProductWrapper, error.getErrorMessage (), Snackbar.LENGTH_SHORT).show ();
                            break;
                        case INVALID_INPUT_SUPPLIED:
                            Snackbar.make (mProductWrapper, error.getErrorMessage (), Snackbar.LENGTH_SHORT).show ();
                            break;
                        case AUTHENTICATION_ERROR:
                            Snackbar.make (mProductWrapper, error.getErrorMessage (), Snackbar.LENGTH_SHORT).show ();
                            break;
                        case UNAUTHORIZED_ERROR:
                            Snackbar.make (mProductWrapper, error.getErrorMessage (), Snackbar.LENGTH_SHORT).show ();
                            break;
                    }
                }
            }
        };

        String url = QueryBuilder.getProducts ();
        baseModel.executeGetJsonArrayRequest (url, TAG);

    }

    public void searchItem(String _searchText){
        if(mProductViewAdapter != null){
            mProductViewAdapter.getFilter ().filter (_searchText);
        }

    }

}
