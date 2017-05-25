package com.flowercentral.flowercentralcustomer.cart;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.flowercentral.flowercentralcustomer.BaseActivity;
import com.flowercentral.flowercentralcustomer.R;
import com.flowercentral.flowercentralcustomer.common.model.Product;
import com.flowercentral.flowercentralcustomer.setting.AppConstant;

import java.util.ArrayList;

public class CartActivity extends BaseActivity {

    private Toolbar mToolbar;
    private String mAction;
    private ArrayList<Product> mProductList;

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

        MenuItem menuItemSearch = menu.findItem (R.id.action_search);
        MenuItem menuItemCart = menu.findItem (R.id.action_search);

        if(menuItemSearch != null){
            menuItemSearch.setVisible (false);
        }
        if(menuItemCart != null){
            menuItemCart.setVisible (false);
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
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private void initializeView () {
        mToolbar = (Toolbar) findViewById (R.id.toolbar);
        //Setup toolbar
        setSupportActionBar (mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString (R.string.title_activity_cart));

    }

    // List of product for Temporary use
    private void preparingProductList(){

        ArrayList<String> relatedImages = new ArrayList<String> ();
        relatedImages.add ("https://i4.fnp.com/images/pr/l/enigmatic-8-red-roses_1.jpg");
        relatedImages.add ("https://i4.fnp.com/images/pr/l/asiatic-lilies-standard_1.jpg");
        relatedImages.add ("https://i4.fnp.com/images/pr/l/enigmatic-8-red-roses_1.jpg");
        relatedImages.add ("https://i4.fnp.com/images/pr/l/perfection_1.jpg");
        relatedImages.add ("https://i4.fnp.com/images/pr/l/asiatic-lilies-standard_1.jpg");
        relatedImages.add ("https://i4.fnp.com/images/pr/l/enigmatic-8-red-roses_1.jpg");
        relatedImages.add ("https://i4.fnp.com/images/pr/l/asiatic-lilies-standard_1.jpg");

        mProductList = new ArrayList<Product> ();

        Product p1 = new Product ();
        p1.setID (1);
        p1.setTitle ("Splendid Purple Orchids");
        p1.setDescription ("Add glamour and elegance to the day for someone by sending a bunch of tropical beauties from Ferns N Petals. Gift a bunch of 6 Purple Orchids wrapped beautifully in pink paper packaging and a pink ribbon bow. This is definitely a perfect gift for friends and family on the special occasions.");
        p1.setImage ("https://i4.fnp.com/images/pr/l/enigmatic-8-red-roses_1.jpg");
        p1.setPrice (700d);
        p1.setLiked (1);
        p1.setRelatedImages (relatedImages);

        mProductList.add (p1);

        Product p2 = new Product ();
        p2.setID (2);
        p2.setTitle ("Splendid Purple Orchids");
        p2.setDescription ("Add glamour and elegance to the day for someone by sending a bunch of tropical beauties from Ferns N Petals. Gift a bunch of 6 Purple Orchids wrapped beautifully in pink paper packaging and a pink ribbon bow. This is definitely a perfect gift for friends and family on the special occasions.");
        p2.setImage ("https://i4.fnp.com/images/pr/l/asiatic-lilies-standard_1.jpg");
        p2.setPrice (700d);
        p2.setLiked (0);
        p2.setRelatedImages (relatedImages);

        mProductList.add (p2);

        Product p3 = new Product ();
        p3.setID (3);
        p3.setTitle ("Splendid Purple Orchids");
        p3.setDescription ("Add glamour and elegance to the day for someone by sending a bunch of tropical beauties from Ferns N Petals. Gift a bunch of 6 Purple Orchids wrapped beautifully in pink paper packaging and a pink ribbon bow. This is definitely a perfect gift for friends and family on the special occasions.");
        p3.setImage ("https://i4.fnp.com/images/pr/l/enigmatic-8-red-roses_1.jpg");
        p3.setPrice (700d);
        p3.setLiked (1);
        p3.setRelatedImages (relatedImages);

        mProductList.add (p3);

        Product p4 = new Product ();
        p4.setID (4);
        p4.setTitle ("Splendid Purple Orchids");
        p4.setDescription ("Add glamour and elegance to the day for someone by sending a bunch of tropical beauties from Ferns N Petals. Gift a bunch of 6 Purple Orchids wrapped beautifully in pink paper packaging and a pink ribbon bow. This is definitely a perfect gift for friends and family on the special occasions.");
        p4.setImage ("https://i4.fnp.com/images/pr/l/perfection_1.jpg");
        p4.setPrice (700d);
        p4.setLiked (1);
        p4.setRelatedImages (relatedImages);

        mProductList.add (p4);

        Product p5 = new Product ();
        p5.setID (5);
        p5.setTitle ("Splendid Purple Orchids");
        p5.setDescription ("Add glamour and elegance to the day for someone by sending a bunch of tropical beauties from Ferns N Petals. Gift a bunch of 6 Purple Orchids wrapped beautifully in pink paper packaging and a pink ribbon bow. This is definitely a perfect gift for friends and family on the special occasions.");
        p5.setImage ("https://i4.fnp.com/images/pr/l/asiatic-lilies-standard_1.jpg");
        p5.setPrice (700d);
        p5.setLiked (0);
        p5.setRelatedImages (relatedImages);

        mProductList.add (p5);

        Product p6 = new Product ();
        p6.setID (6);
        p6.setTitle ("Splendid Purple Orchids");
        p6.setDescription ("Add glamour and elegance to the day for someone by sending a bunch of tropical beauties from Ferns N Petals. Gift a bunch of 6 Purple Orchids wrapped beautifully in pink paper packaging and a pink ribbon bow. This is definitely a perfect gift for friends and family on the special occasions.");
        p6.setImage ("https://i4.fnp.com/images/pr/l/enigmatic-8-red-roses_1.jpg");
        p6.setPrice (700d);
        p6.setLiked (1);
        p6.setRelatedImages (relatedImages);

        mProductList.add (p6);

        Product p7 = new Product ();
        p7.setID (7);
        p7.setTitle ("Splendid Purple Orchids");
        p7.setDescription ("Add glamour and elegance to the day for someone by sending a bunch of tropical beauties from Ferns N Petals. Gift a bunch of 6 Purple Orchids wrapped beautifully in pink paper packaging and a pink ribbon bow. This is definitely a perfect gift for friends and family on the special occasions.");
        p7.setImage ("https://i4.fnp.com/images/pr/l/perfection_1.jpg");
        p7.setPrice (700d);
        p7.setLiked (0);
        p7.setRelatedImages (relatedImages);
        mProductList.add (p7);

        Product p8 = new Product ();
        p8.setID (8);
        p8.setTitle ("Splendid Purple Orchids");
        p8.setDescription ("Add glamour and elegance to the day for someone by sending a bunch of tropical beauties from Ferns N Petals. Gift a bunch of 6 Purple Orchids wrapped beautifully in pink paper packaging and a pink ribbon bow. This is definitely a perfect gift for friends and family on the special occasions.");
        p8.setImage ("https://i4.fnp.com/images/pr/l/asiatic-lilies-standard_1.jpg");
        p8.setPrice (700d);
        p8.setLiked (0);
        p8.setRelatedImages (relatedImages);
        mProductList.add (p8);

    }

}
