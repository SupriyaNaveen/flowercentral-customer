package com.flowercentral.flowercentralcustomer.order;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.flowercentral.flowercentralcustomer.BaseActivity;
import com.flowercentral.flowercentralcustomer.R;
import com.flowercentral.flowercentralcustomer.common.interfaces.OnItemClickListener;
import com.flowercentral.flowercentralcustomer.common.model.Order;
import com.flowercentral.flowercentralcustomer.common.model.Product;
import com.flowercentral.flowercentralcustomer.common.model.ShoppingCart;
import com.flowercentral.flowercentralcustomer.dao.LocalDAO;
import com.flowercentral.flowercentralcustomer.order.adapter.OrderAdapter;
import com.flowercentral.flowercentralcustomer.preference.UserPreference;
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

public class OrderActivity extends BaseActivity implements OnItemClickListener {

    private final static String TAG = OrderActivity.class.getSimpleName();

    private Activity mCurrentActivity;
    private String mAction;
    private RelativeLayout mOrderOuterWapper;
    private LinearLayout mOrderWrapper;
    private RecyclerView mOrderRecyclerView;
    private Toolbar mToolbar;

    private ArrayList<Order> mMyOrders;
    private OnDataReceiveListener onDataReceiveListener;
    private OrderAdapter mMyOrderAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    private MaterialDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        mContext = this;
        mCurrentActivity = this;

        //Get data from Intent
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                mAction = bundle.getString("action");
            }
        }

        //Initialize view elements
        initializeView(mContext);

    }

    private void initializeView(final Context _context) {
        mOrderOuterWapper = (RelativeLayout) findViewById(R.id.rl_order_outer_wrapper);
        mOrderWrapper = (LinearLayout) findViewById(R.id.ll_order_wrapper);
        mOrderRecyclerView = (RecyclerView) findViewById(R.id.rv_order_list);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        //Setup toolbar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_activity_order));

        //Todo Fetch Orders from Server
        getMyOrdersFromServer(mContext, mCurrentActivity);

        //Get orders from Local Database
        LocalDAO localDAO = new LocalDAO(_context);
        mMyOrders = localDAO.getOrders(_context, null);

        onDataReceiveListener = new OnDataReceiveListener() {

            @Override
            public void onDataReceived() {
                LocalDAO localDAO = new LocalDAO(mContext);
                ArrayList<Order> orders = localDAO.getOrders(mContext, null);
                if (null == mMyOrders) {
                    mMyOrders = new ArrayList<>();
                }
                mMyOrders.addAll(orders);
                mMyOrderAdapter.addAll(orders);
                mMyOrderAdapter.notifyDataSetChanged();
            }
        };

        //Create adapter for recycler view
        mMyOrderAdapter = new OrderAdapter(_context, mMyOrders, this);
        mOrderRecyclerView.setHasFixedSize(true);
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        mOrderRecyclerView.setLayoutManager(mLinearLayoutManager);
        mOrderRecyclerView.setAdapter(mMyOrderAdapter);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dashboard, menu);
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

    @Override
    public void onItemClicked(String _type, int _position, Object _data) {

    }

    @Override
    public void onItemDeleted(int _position, Object _data) {

    }

    //Interface to update Order data for offline use and update recycler view
    interface OnDataReceiveListener {
        public void onDataReceived();
    }

    private void getMyOrdersFromServer(final Context _context, Activity _currentActivity) {
        //Start Progress dialog
        dismissDialog();

        mProgressDialog = Util.showProgressDialog(_currentActivity, null, getString(R.string.msg_registering_user), false);

        BaseModel<JSONArray> baseModel = new BaseModel<JSONArray>(_context) {
            @Override
            public void onSuccess(int _statusCode, Map<String, String> _headers, JSONArray _response) {
                //CLose Progress dialog
                dismissDialog();
                if (_response != null) {
                    try {
                        LocalDAO localDAO = new LocalDAO(_context);
                        boolean isAdded = localDAO.addOrder(_context, _response);
                        if (isAdded) {
                            if (onDataReceiveListener != null) {
                                onDataReceiveListener.onDataReceived();
                            }
                        }

                    } catch (Exception ex) {
                        Snackbar.make(mOrderOuterWapper, ex.getMessage(), Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    Snackbar.make(mOrderOuterWapper, "No response from server", Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(ErrorData error) {
                //Close Progress dialog
                dismissDialog();
                if (error != null) {

                    switch (error.getErrorType()) {
                        case NETWORK_NOT_AVAILABLE:
                            Snackbar.make(mOrderOuterWapper, getResources().getString(R.string.msg_internet_unavailable), Snackbar.LENGTH_SHORT).show();
                            break;
                        case INTERNAL_SERVER_ERROR:
                            Snackbar.make(mOrderOuterWapper, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case CONNECTION_TIMEOUT:
                            Snackbar.make(mOrderOuterWapper, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case APPLICATION_ERROR:
                            Snackbar.make(mOrderOuterWapper, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case INVALID_INPUT_SUPPLIED:
                            Snackbar.make(mOrderOuterWapper, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case AUTHENTICATION_ERROR:
                            Snackbar.make(mOrderOuterWapper, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case UNAUTHORIZED_ERROR:
                            Snackbar.make(mOrderOuterWapper, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                    }
                }
            }
        };

        String url = QueryBuilder.getPreviousOrderUrl();
        baseModel.executeGetJsonArrayRequest(url, TAG);

    }

    private void dismissDialog() {
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
