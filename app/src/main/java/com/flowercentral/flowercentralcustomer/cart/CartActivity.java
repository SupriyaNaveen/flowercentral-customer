package com.flowercentral.flowercentralcustomer.cart;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.andexert.library.RippleView;
import com.flowercentral.flowercentralcustomer.BaseActivity;
import com.flowercentral.flowercentralcustomer.R;
import com.flowercentral.flowercentralcustomer.cart.adapter.CartItemAdapter;
import com.flowercentral.flowercentralcustomer.common.interfaces.OnItemClickListener;
import com.flowercentral.flowercentralcustomer.common.model.Product;
import com.flowercentral.flowercentralcustomer.common.model.ShoppingCart;
import com.flowercentral.flowercentralcustomer.dao.LocalDAO;
import com.flowercentral.flowercentralcustomer.delivery.AddressActivity;
import com.flowercentral.flowercentralcustomer.rest.BaseModel;
import com.flowercentral.flowercentralcustomer.rest.QueryBuilder;
import com.flowercentral.flowercentralcustomer.setting.AppConstant;
import com.flowercentral.flowercentralcustomer.util.Util;
import com.flowercentral.flowercentralcustomer.volley.ErrorData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class CartActivity extends BaseActivity implements RippleView.OnRippleCompleteListener, OnItemClickListener{

    private static final String TAG = CartActivity.class.getSimpleName ();

    private Toolbar mToolbar;
    private String mAction;
    private ArrayList<Product> mProductList;
    private CartItemAdapter mCartItemAdapter;
    private RecyclerView mRVCartItemList;
    private LinearLayoutManager mLinearLayoutManager;
    private ArrayList<ShoppingCart> mCartItems;
    private RelativeLayout mButtonWrapper;
    private TextView txtCartItem;
    private TextView txtTotalItemPrice;
    private RippleView btnCheckout;
    private MaterialDialog mProgressDialog;
    private RelativeLayout mOuterWrapper;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_cart);
        mContext = this;

        //Get data from Intent
        Intent intent = getIntent ();
        if(intent != null){
            Bundle bundle = intent.getExtras ();
            if(bundle != null){
                mAction = bundle.getString ("action");
            }
        }

        //Initialize view elements
        initializeView();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish ();
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater ().inflate (R.menu.dashboard, menu);

        MenuItem search = menu.findItem (R.id.action_search);
        if(search != null){
            search.setVisible (false);
        }

        MenuItem toggleView = menu.findItem (R.id.action_toggle_view);
        if(toggleView != null){
            toggleView.setVisible (false);
        }


        MenuItem cart = menu.findItem (R.id.action_cart);
        if(cart != null){
            cart.setVisible (false);
        }

        this.invalidateOptionsMenu ();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        switch (id) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                break;

        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private void initializeView () {
        mOuterWrapper = (RelativeLayout) findViewById (R.id.rl_outer_wrapper);
        mToolbar = (Toolbar) findViewById (R.id.toolbar);

        txtCartItem = (TextView) findViewById (R.id.txt_total_cart_item);
        txtTotalItemPrice = (TextView) findViewById (R.id.txt_total_price);
        mButtonWrapper = (RelativeLayout) findViewById (R.id.rl_button_wrapper);

        btnCheckout = (RippleView) findViewById (R.id.btn_checkout);

        mRVCartItemList = (RecyclerView) findViewById (R.id.rv_cart_item_list);


        //Get items from local db
        LocalDAO localDAO = new LocalDAO (mContext);
        mCartItems = localDAO.getCartItems ();

        if(mCartItems == null || mCartItems.size () == 0){
            mButtonWrapper.setVisibility (View.GONE);
        }else{
            mButtonWrapper.setVisibility (View.VISIBLE);
        }

        //Setup toolbar
        setSupportActionBar (mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString (R.string.title_activity_cart));

        //Setup recycler view
        mCartItemAdapter = new CartItemAdapter (mContext, mCartItems, this);
        mLinearLayoutManager = new LinearLayoutManager (mContext);
        mRVCartItemList.setLayoutManager (mLinearLayoutManager);
        mRVCartItemList.setHasFixedSize (true);
        mRVCartItemList.setAdapter (mCartItemAdapter);

        //Add listener
        btnCheckout.setOnRippleCompleteListener (this);
        updateCartSummary(mCartItems);

    }

    @Override
    public void onComplete (RippleView v) {
        int id = v.getId ();
        switch (id){
            case R.id.btn_checkout:
                showDeliveryAddress();
                break;
        }
    }

    @Override
    public void onItemClicked (String _type, int _position, Object _data) {
        ShoppingCart cartItem = null;
        LocalDAO localDAO = new LocalDAO (mContext);
        if(mRVCartItemList != null){
            CartItemAdapter cartItemAdapter = (CartItemAdapter) mRVCartItemList.getAdapter ();
            if(mCartItems != null && mCartItems.size ()>0){
                cartItem = mCartItems.get (_position);
            }

            if(cartItemAdapter != null){
                if(!TextUtils.isEmpty (_type) && _type.trim ().equalsIgnoreCase ("plus")){
                    cartItemAdapter.updateQuantity(_type, _position, 1);
                    localDAO.updateItemQuantity (cartItem.getProductID (), 1);

                }else if(!TextUtils.isEmpty (_type) && _type.trim ().equalsIgnoreCase ("minus")){
                    cartItemAdapter.updateQuantity(_type, _position, 1);
                    localDAO.updateItemQuantity (cartItem.getProductID (), -1);

                }else{
                    //Nothing
                }
                //Update cart summary
                updateCartSummary(mCartItems);
            }

        }
    }

    @Override
    public void onItemDeleted (int _position, Object _data) {
        if(mRVCartItemList != null){
            CartItemAdapter cartItemAdapter = (CartItemAdapter) mRVCartItemList.getAdapter ();
            if(cartItemAdapter != null){

                //Delete item from local database
                LocalDAO localDAO = new LocalDAO (mContext);
                ShoppingCart cartItem = mCartItems.get (_position);
                localDAO.deleteItem (cartItem.getProductID ());

                //Delete from list
                cartItemAdapter.removeAt (_position);

                if(mCartItems == null || mCartItems.size () == 0){
                    mButtonWrapper.setVisibility (View.GONE);
                }else{
                    mButtonWrapper.setVisibility (View.VISIBLE);
                }

                updateCartSummary(mCartItems);
            }
        }
    }

    private void updateCartSummary (ArrayList<ShoppingCart> _cartItems) {
        if(_cartItems == null){
            txtCartItem.setText ("0");
            txtTotalItemPrice.setText (String.format("$%S", "0.00"));
            return;
        }
        if(_cartItems.size ()==0){
            txtCartItem.setText ("0");
            txtTotalItemPrice.setText (String.format("$%S", "0.00"));

        }else{
            int itemCount = _cartItems.size ();
            double price = 0.00;
            for(ShoppingCart item : _cartItems){
                price = price + (item.getShoppingCartQuantity () * item.getProductPrice ());
            }
            txtCartItem.setText (String.valueOf (itemCount));
            txtTotalItemPrice.setText (String.format("$%S", String.valueOf (price)));
        }
    }

    private void showDeliveryAddress(){
        Intent intent = new Intent (mContext, AddressActivity.class);
        intent.setAction (AppConstant.ACTIONS.SHOW_DELIVERY_ADDR.name ());
        intent.addFlags (Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity (intent);
    }

    private void pullCartItemsFromServer(Context _context, Activity _activity, JSONObject _data){
        //Start Progress dialog
        dismissDialog();
        mProgressDialog = Util.showProgressDialog(_activity, null, getString(R.string.msg_registering_user), false);

        BaseModel<JSONObject> baseModel = new BaseModel<JSONObject>(_context) {
            @Override
            public void onSuccess(int statusCode, Map<String, String> headers, JSONObject response) {

                //TODO Handle res
                //CLose Progress dialog
                dismissDialog();
                try {
                    if (response.getInt("status") == 1) {
                        JSONArray products = response.getJSONArray ("products");


                    } else {
                        //Snackbar.make(mOuterWrapper, getString (R.string.msg_order_fail), Snackbar.LENGTH_SHORT).show();
                    }
                } catch (JSONException jsonEx) {
                    Snackbar.make(mOuterWrapper, jsonEx.getMessage (), Snackbar.LENGTH_SHORT).show();
                }catch (Exception ex){
                    Snackbar.make(mOuterWrapper, ex.getMessage (), Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(ErrorData error) {
                //Close Progress dialog
                dismissDialog();

                if (error != null) {
                    error.setErrorMessage("Error while fetching cart items: " + error.getErrorMessage());

                    switch (error.getErrorType()) {
                        case NETWORK_NOT_AVAILABLE:
                            Snackbar.make(mOuterWrapper, getResources().getString(R.string.msg_internet_unavailable), Snackbar.LENGTH_SHORT).show();
                            break;
                        case INTERNAL_SERVER_ERROR:
                            Snackbar.make(mOuterWrapper, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case CONNECTION_TIMEOUT:
                            Snackbar.make(mOuterWrapper, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case APPLICATION_ERROR:
                            Snackbar.make(mOuterWrapper, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case INVALID_INPUT_SUPPLIED:
                            Snackbar.make(mOuterWrapper, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case AUTHENTICATION_ERROR:
                            Snackbar.make(mOuterWrapper, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case UNAUTHORIZED_ERROR:
                            Snackbar.make(mOuterWrapper, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        default:
                            Snackbar.make(mOuterWrapper, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                    }
                }
            }
        };

        String url = QueryBuilder.getCartItemUrl ();

        if (_data != null) {
            baseModel.executePostJsonRequest(url, _data, TAG);
        } else {
            Snackbar.make(mOuterWrapper, getResources().getString(R.string.msg_reg_user_missing_input), Snackbar.LENGTH_SHORT).show();
        }
    }

    public void dismissDialog() {
        try {
            if (mProgressDialog != null && mProgressDialog.isShowing() && !isFinishing()) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
