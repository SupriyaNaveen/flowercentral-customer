package com.flowercentral.flowercentralcustomer.ProductDetail;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.flowercentral.flowercentralcustomer.BaseActivity;
import com.flowercentral.flowercentralcustomer.ProductDetail.adapter.SlidingImageAdapter;
import com.flowercentral.flowercentralcustomer.R;
import com.flowercentral.flowercentralcustomer.cart.CartActivity;
import com.flowercentral.flowercentralcustomer.common.model.Product;
import com.flowercentral.flowercentralcustomer.setting.AppConstant;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ProductDetailActivity extends BaseActivity implements View.OnClickListener {

    private Toolbar mToolbar;
    private String mAction;
    private Product mProduct;
    private ViewPager mSlidingImagePager;
    private SlidingImageAdapter mSlidingImageAdapter;

    private static int currentPage = 0;
    private static int NUM_PAGES = 0;
    private Button mBtnCart;
    private Button mBtnBuyNow;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_product_detail);
        mContext = this;

        //Get data from Intent
        Intent intent = getIntent ();
        if(intent != null){
            Bundle bundle = intent.getExtras ();
            if(bundle != null){
                mAction = bundle.getString ("action");
                mProduct = (Product) bundle.getParcelable ("data");
            }
        }

        //Initialize view elements
        initializeView ();

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
        MenuItem menuItem = menu.findItem (R.id.action_search);
        if(menuItem != null){
            menuItem.setVisible (false);
        }
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
            case R.id.action_cart:
                //Display cart screen
                showCart ();
                break;

        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initializeView () {
        mToolbar = (Toolbar) findViewById (R.id.toolbar);
        mSlidingImagePager = (ViewPager) findViewById (R.id.pager);

        //Action Buttons
        mBtnCart = (Button) findViewById (R.id.btn_add_cart);
        mBtnBuyNow = (Button) findViewById (R.id.btn_buy_now);

        //Setup toolbar
        setSupportActionBar (mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString (R.string.title_activity_product_detail));

        //Add Action Listener
        mBtnCart.setOnClickListener (this);
        mBtnBuyNow.setOnClickListener (this);

        ArrayList<String> relatedImages = null;
        if(mProduct != null){
            relatedImages = mProduct.getRelatedImages ();
            if(relatedImages == null){
                relatedImages = new ArrayList<String> ();
            }
            relatedImages.add (0, mProduct.getImage ());
        }

        mSlidingImageAdapter = new SlidingImageAdapter (mContext, relatedImages);
        mSlidingImagePager.setAdapter (mSlidingImageAdapter);

        CirclePageIndicator indicator = (CirclePageIndicator) findViewById(R.id.indicator);

        indicator.setViewPager(mSlidingImagePager);

        final float density = getResources().getDisplayMetrics().density;

        //Set circle indicator radius
        indicator.setRadius(5 * density);

        NUM_PAGES =relatedImages.size ();

        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                mSlidingImagePager.setCurrentItem(currentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask () {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 3000, 3000);

        // Pager listener over indicator
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                currentPage = position;

            }

            @Override
            public void onPageScrolled(int pos, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int pos) {

            }
        });

    }

    @Override
    public void onClick (View v) {
        int id = v.getId ();
        switch (id){
            case R.id.btn_add_cart:

                break;
            case R.id.btn_buy_now:
                //Display cart screen
                showCart ();
                break;
        }
    }

    private void showCart(){
        Intent intent = new Intent (mContext, CartActivity.class);
        intent.setAction (AppConstant.ACTIONS.SHOW_CART.name ());
        intent.addFlags (Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity (intent);
    }

}
