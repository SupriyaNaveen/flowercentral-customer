package com.flowercentral.flowercentralcustomer.ProductDetail;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flowercentral.flowercentralcustomer.BaseActivity;
import com.flowercentral.flowercentralcustomer.ProductDetail.adapter.SlidingImageAdapter;
import com.flowercentral.flowercentralcustomer.R;
import com.flowercentral.flowercentralcustomer.cart.CartActivity;
import com.flowercentral.flowercentralcustomer.common.model.Product;
import com.flowercentral.flowercentralcustomer.common.model.ShoppingCart;
import com.flowercentral.flowercentralcustomer.dao.LocalDAO;
import com.flowercentral.flowercentralcustomer.setting.AppConstant;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ProductDetailActivity extends BaseActivity implements View.OnClickListener {

    private RelativeLayout mOuterWrapper;
    private Toolbar mToolbar;
    private String mAction;
    private Product mProduct;
    private ViewPager mSlidingImagePager;
    private SlidingImageAdapter mSlidingImageAdapter;

    private static int currentPage = 0;
    private static int NUM_PAGES = 0;
    private ImageView mBtnCart;
    private ImageView mBtnBuyNow;
    private ImageView mBtnLike;
    private TextInputEditText mEdtUserMessage;

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
       /* MenuItem menuItem = menu.findItem (R.id.action_search);
        if(menuItem != null){
            menuItem.setVisible (false);
        }*/
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
        mOuterWrapper = (RelativeLayout) findViewById (R.id.rl_outer_wrapper);
        mToolbar = (Toolbar) findViewById (R.id.toolbar);
        mSlidingImagePager = (ViewPager) findViewById (R.id.pager);
        mEdtUserMessage = (TextInputEditText) findViewById(R.id.edit_text_message);

        //Action Buttons
        mBtnCart = (ImageView) findViewById (R.id.btn_add_cart);
        mBtnBuyNow = (ImageView) findViewById (R.id.btn_buy_now);
        mBtnLike = (ImageView) findViewById(R.id.image_view_product_like);

        TextView infoTextView = (TextView) findViewById(R.id.txt_product_info);
        infoTextView.setText(mProduct.getFlowerName ());

        TextView priceTextView = (TextView) findViewById(R.id.txt_product_price);
        priceTextView.setText(String.format("$%s", mProduct.getPrice()));

        if(mProduct.isLiked() == 1) {
            mBtnLike.setImageResource(R.drawable.ic_product_like_svg);
        } else {
            mBtnLike.setImageResource(R.drawable.ic_product_dislike_svg);
        }

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
        boolean status = false;
        int id = v.getId ();
        switch (id){
            case R.id.btn_add_cart:
                status = addToCart(mProduct);
                if(status == true){
                    Snackbar.make (mOuterWrapper, getString (R.string.msg_item_added_cart), Snackbar.LENGTH_SHORT).show ();
                }
                break;
            case R.id.btn_buy_now:
                //Display cart screen
                status = addToCart(mProduct);
                if(status == true){
                    showCart ();
                }
                break;
        }
    }

    private boolean addToCart (Product _product) {
        ArrayList<ShoppingCart> cartItem = new ArrayList<ShoppingCart> ();

        String userMessage = mEdtUserMessage.getText ().toString ();

        ShoppingCart shoppingCart = new ShoppingCart ();
        shoppingCart.setProductID (Integer.valueOf (_product.getProductID ()));
        shoppingCart.setProductName (_product.getFlowerName ());
        shoppingCart.setProductCategory (_product.getCategory ());
        shoppingCart.setProductImage (_product.getImage ());
        shoppingCart.setProductQuantity (_product.getQty ());
        shoppingCart.setProductPrice (_product.getPrice ());
        shoppingCart.setUserMessage (userMessage);
        shoppingCart.setShoppingCartQuantity (1);
        shoppingCart.setStatus (1);

        cartItem.add (shoppingCart);

        LocalDAO localDAO = new LocalDAO (mContext);
        boolean status = localDAO.addItemsToCart (cartItem);
        return status;
    }


    private void showCart(){
        Intent intent = new Intent (mContext, CartActivity.class);
        intent.setAction (AppConstant.ACTIONS.SHOW_CART.name ());
        intent.addFlags (Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity (intent);
    }

}
