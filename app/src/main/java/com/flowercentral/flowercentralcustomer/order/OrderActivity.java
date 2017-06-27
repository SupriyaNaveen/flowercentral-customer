package com.flowercentral.flowercentralcustomer.order;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.flowercentral.flowercentralcustomer.BaseActivity;
import com.flowercentral.flowercentralcustomer.R;
import com.flowercentral.flowercentralcustomer.common.model.Order;
import com.flowercentral.flowercentralcustomer.dao.LocalDAO;

import java.util.ArrayList;

public class OrderActivity extends BaseActivity {

    private final static String TAG = OrderActivity.class.getSimpleName ();

    private Activity mCurrentActivity;
    private String mAction;
    private RelativeLayout mOrderOuterWapper;
    private LinearLayout mOrderWrapper;
    private RecyclerView mOrderRecyclerView;
    private Toolbar mToolbar;

    private ArrayList<Order> mMyOrders;
    private OnDataReceiveListener onDataReceiveListener;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_order_detail);

        mContext = this;
        mCurrentActivity = this;

        //Get data from Intent
        Intent intent = getIntent ();
        if(intent != null){
            Bundle bundle = intent.getExtras ();
            if(bundle != null){
                mAction = bundle.getString ("action");
            }
        }

        //Initialize view elements
        initializeView(mContext);

    }

    private void initializeView (Context _context) {
        mOrderOuterWapper = (RelativeLayout) findViewById (R.id.rl_order_outer_wrapper);
        mOrderWrapper = (LinearLayout) findViewById (R.id.ll_order_wrapper);
        mOrderRecyclerView = (RecyclerView) findViewById (R.id.rv_order_list);
        mToolbar = (Toolbar) findViewById (R.id.toolbar);

        //Setup toolbar
        setSupportActionBar (mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString (R.string.title_activity_order));

        //Todo Fetch Orders from Server
        getMyOrdersFromServer(mContext, mCurrentActivity);

        //Get orders from Local Database
        LocalDAO localDAO = new LocalDAO (_context);
        mMyOrders = localDAO.getAllOrders(_context);

        onDataReceiveListener = new OnDataReceiveListener () {

            @Override
            public void onDataReceived (ArrayList<Order> _orders) {

            }
        };

        //Create adapter for recycler view


    }

    @Override
    public void onBackPressed () {
        super.onBackPressed ();
        finish ();
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater ().inflate (R.menu.dashboard, menu);
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


    private void getMyOrdersFromServer (Context _context, Activity _currentActivity) {

    }

    //Interface to update Order data for offline use and update recycler view
    interface OnDataReceiveListener{
        public void onDataReceived(ArrayList<Order> _orders);
    }
}
