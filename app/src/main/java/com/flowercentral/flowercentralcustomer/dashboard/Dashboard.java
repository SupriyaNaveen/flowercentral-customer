package com.flowercentral.flowercentralcustomer.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.os.OperationCanceledException;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.flowercentral.flowercentralcustomer.BaseActivity;
import com.flowercentral.flowercentralcustomer.ProductDetail.ProductDetailActivity;
import com.flowercentral.flowercentralcustomer.R;
import com.flowercentral.flowercentralcustomer.cart.CartActivity;
import com.flowercentral.flowercentralcustomer.common.component.NavigationDrawerToggle;
import com.flowercentral.flowercentralcustomer.common.interfaces.OnFragmentInteractionListener;
import com.flowercentral.flowercentralcustomer.common.model.Product;
import com.flowercentral.flowercentralcustomer.dashboard.fragment.ProductGridFragment;
import com.flowercentral.flowercentralcustomer.dashboard.fragment.ProductListFragment;
import com.flowercentral.flowercentralcustomer.profile.ProfileActivity;
import com.flowercentral.flowercentralcustomer.setting.AppConstant;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Dashboard extends BaseActivity
    implements NavigationView.OnNavigationItemSelectedListener,
    View.OnClickListener,
    FragmentManager.OnBackStackChangedListener,
    OnFragmentInteractionListener{

    private ArrayList<Product> mProductList;

    private enum OPTIONS {DEFAULT, PROFILE, DELIVERY_TIME, ORDER, HELP};


    private Toolbar mToolbar;
    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle mToggle;
    private NavigationView mNavigationViewLeft;
    private NavigationView mNavigationViewRight;
    private NavigationDrawerToggle mDrawerToggle;
    private ImageView mSelView;
    private ImageView mFilter;

    private String mAction;
    private int mSelectedOption;
    private int mSelectedView;

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_dashboard);
        mContext = this;
        //Initialize view elements
        initializeView ();

        setSupportActionBar (mToolbar);

        mToggle = new ActionBarDrawerToggle (
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.setDrawerListener (mToggle);
        mToggle.syncState ();

        //Todo Get intent
        Intent intent = getIntent();
        if(intent!=null){
            mAction = intent.getAction();
            if(mAction == null || mAction.isEmpty()){
                mAction = AppConstant.ACTIONS.HOME.name ();

            }else if(mAction.equalsIgnoreCase(AppConstant.ACTIONS.EXIT.name ())){
                finish();
            }else{

            }
        }

        preparingProductList ();

        //Set the default options and view
        mSelectedOption = OPTIONS.DEFAULT.ordinal ();
        mSelectedView = AppConstant.VIEW_TYPE.GRID.ordinal ();

        //Load default home fragment
        loadFragment(mSelectedOption, null);

        mNavigationViewLeft.setNavigationItemSelectedListener (this);
        mNavigationViewRight.setNavigationItemSelectedListener (this);

        mSelView.setOnClickListener (this);
        mFilter.setOnClickListener (this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }

    @Override
    public void onBackPressed () {

        if (mDrawer.isDrawerOpen (GravityCompat.START)) {
            mDrawer.closeDrawer (GravityCompat.START);
        } else if(mDrawer.isDrawerOpen (GravityCompat.END)){
            mDrawer.closeDrawer (GravityCompat.END);
        }else{
            super.onBackPressed ();
        }
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater ().inflate (R.menu.dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId ();
        switch (id){
            case R.id.action_cart:
            showCart ();
            break;
            case R.id.action_search:
                break;
        }

        return super.onOptionsItemSelected (item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected (MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId ();

        if (id == R.id.nav_orders) {
            // Handle the camera action
        } else if (id == R.id.nav_profile) {
            showProfile ();

        } else if (id == R.id.nav_delivery_time) {

        } else if (id == R.id.nav_logout) {
            signout ();

        } else if (id == R.id.nav_help) {

        }

        mDrawer.closeDrawer (GravityCompat.START);
        mDrawer.closeDrawer (GravityCompat.END);

        return true;
    }

    @Override
    public void onClick (View v) {
        int id = v.getId ();
        switch(id){
            case R.id.img_grid_view:

                mSelectedOption = OPTIONS.DEFAULT.ordinal ();
                if(mSelectedView == AppConstant.VIEW_TYPE.GRID.ordinal ()){
                    mSelectedView = AppConstant.VIEW_TYPE.LIST.ordinal ();
                }else if(mSelectedView == AppConstant.VIEW_TYPE.LIST.ordinal ()){
                    mSelectedView = AppConstant.VIEW_TYPE.GRID.ordinal ();
                }
                loadFragment (mSelectedOption, null);

                break;
            case R.id.img_filter:

                break;
        }

    }

    @Override
    public void onBackStackChanged () {

    }

    @Override
    public void onFragmentInteraction (Bundle _bundle) {
        if(_bundle != null){
            int action = _bundle.getInt ("action");
            Product product = (Product) _bundle.getParcelable ("data");
            displayProductDetails(AppConstant.ACTIONS.PRODUCT_DETAILS.name (), product);

        }
    }

    private void displayProductDetails (String _action , Product _product) {
        Intent intent = new Intent (mContext, ProductDetailActivity.class);
        intent.setAction (_action);
        intent.addFlags (Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("data", _product);
        startActivity (intent);
    }

    private void initializeView () {
        mToolbar = (Toolbar) findViewById (R.id.toolbar);
        mDrawer = (DrawerLayout) findViewById (R.id.drawer_layout);
        mNavigationViewLeft = (NavigationView) findViewById (R.id.nav_view_left);
        mNavigationViewRight = (NavigationView) findViewById (R.id.nav_view_right);
        mSelView = (ImageView) findViewById (R.id.img_grid_view);
        mFilter = (ImageView) findViewById (R.id.img_filter);
    }

    private void loadFragment(int _selectedOptions, String _data){
        if(fragmentManager == null){
            fragmentManager = getSupportFragmentManager();
            fragmentManager.addOnBackStackChangedListener (this);
        }

        if(_selectedOptions == OPTIONS.DEFAULT.ordinal ()){

            if(mSelectedView == AppConstant.VIEW_TYPE.GRID.ordinal ()){
                Bundle args = new Bundle();
                args.putInt ("selectedView", mSelectedView);
                args.putParcelableArrayList ("products", mProductList);

                ProductGridFragment productGridFragment = ProductGridFragment.newInstance(args);
                fragmentManager.beginTransaction().replace (R.id.fragment_container,productGridFragment,"product_grid").commitAllowingStateLoss ();

            }else{
                Bundle args = new Bundle();
                args.putInt ("selectedView", mSelectedView);
                args.putParcelableArrayList ("products", mProductList);

                ProductGridFragment productGridFragment = ProductGridFragment.newInstance(args);
                fragmentManager.beginTransaction().replace (R.id.fragment_container,productGridFragment,"product_grid").commitAllowingStateLoss ();

            }

        }

    }

    private void showCart(){
        Intent intent = new Intent (mContext, CartActivity.class);
        intent.setAction (AppConstant.ACTIONS.SHOW_CART.name ());
        intent.addFlags (Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity (intent);
    }


    private void showProfile(){
        Intent intent = new Intent (mContext, ProfileActivity.class);
        intent.setAction (AppConstant.ACTIONS.USER_PROFILE.name ());
        intent.addFlags (Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity (intent);
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
