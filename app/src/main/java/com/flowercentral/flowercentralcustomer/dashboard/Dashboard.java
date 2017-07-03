package com.flowercentral.flowercentralcustomer.dashboard;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.flowercentral.flowercentralcustomer.BaseActivity;
import com.flowercentral.flowercentralcustomer.ProductDetail.ProductDetailActivity;
import com.flowercentral.flowercentralcustomer.R;
import com.flowercentral.flowercentralcustomer.cart.CartActivity;
import com.flowercentral.flowercentralcustomer.common.component.NavigationDrawerToggle;
import com.flowercentral.flowercentralcustomer.common.interfaces.OnFragmentInteractionListener;
import com.flowercentral.flowercentralcustomer.common.model.Product;
import com.flowercentral.flowercentralcustomer.common.model.ShoppingCart;
import com.flowercentral.flowercentralcustomer.dao.LocalDAO;
import com.flowercentral.flowercentralcustomer.dashboard.fragment.ProductGridFragment;
import com.flowercentral.flowercentralcustomer.launch.ui.LauncherActivity;
import com.flowercentral.flowercentralcustomer.order.OrderActivity;
import com.flowercentral.flowercentralcustomer.preference.UserPreference;
import com.flowercentral.flowercentralcustomer.profile.ProfileActivity;
import com.flowercentral.flowercentralcustomer.setting.AppConstant;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Dashboard extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener,
        FragmentManager.OnBackStackChangedListener,
        OnFragmentInteractionListener{

    private static final String TAG = Dashboard.class.getSimpleName ();

    private ArrayList<Product> mProductList;
    private Activity mCurrentActivity;

    private MaterialDialog mProgressDialog;
    private MaterialDialog mSingleChoiceListDialog;

    private CircleImageView mUserProfilePic;
    private TextView mUserName;
    private TextView mUserEmail;
    private SearchView mProductSearch;

    private enum OPTIONS {DEFAULT, PROFILE, DELIVERY_TIME, ORDER, HELP};

    private RelativeLayout mContentWrapper;
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
    private int mSelectedSortingOption;

    private FragmentManager fragmentManager;
    private ProductGridFragment productGridFragment;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_dashboard);
        mContext = this;
        mCurrentActivity = this;

        //Initialize view elements
        initializeView ();

        setSupportActionBar (mToolbar);

        mToggle = new ActionBarDrawerToggle (
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.setDrawerListener (mToggle);
        mToggle.syncState ();

        //Todo Get intent
        Intent intent = getIntent ();
        if (intent != null) {
            mAction = intent.getAction ();
            if (mAction == null || mAction.isEmpty ()) {
                mAction = AppConstant.ACTIONS.HOME.name ();

            } else if (mAction.equalsIgnoreCase (AppConstant.ACTIONS.EXIT.name ())) {
                finish ();
            } else {

            }
        }

        //Populate existing product (if any)
        getProductStoredLocally (mContext);

        //Set the default options and view
        mSelectedOption = OPTIONS.DEFAULT.ordinal ();
        mSelectedView = AppConstant.VIEW_TYPE.GRID.ordinal ();

        //Load default home fragment
        loadFragment (mSelectedOption, null);

        mNavigationViewLeft.setNavigationItemSelectedListener (this);
        mNavigationViewRight.setNavigationItemSelectedListener (this);

        mSelView.setOnClickListener (this);
        mFilter.setOnClickListener (this);

    }

    @Override
    public void onBackPressed () {

        if (mDrawer.isDrawerOpen (GravityCompat.START)) {
            mDrawer.closeDrawer (GravityCompat.START);
        } else if (mDrawer.isDrawerOpen (GravityCompat.END)) {
            mDrawer.closeDrawer (GravityCompat.END);
        } else {
            super.onBackPressed ();
        }
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater ().inflate (R.menu.dashboard, menu);

        /*MenuItem search = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        search(searchView);*/

        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId ();
        switch (id) {
            case R.id.action_cart:
                showCart ();
                break;
            /*case R.id.action_search:

                break;*/
        }

        return super.onOptionsItemSelected (item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected (MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId ();

        if (id == R.id.nav_orders) {
            showOrder();

        } else if (id == R.id.nav_profile) {
            showProfile ();

        } else if (id == R.id.nav_delivery_time) {

        } else if (id == R.id.nav_logout) {
            logout (mContext);

        } else if (id == R.id.nav_help) {

        } else if (id == R.id.nav_small) {
            if (productGridFragment != null) {
                productGridFragment.showProductByCategory ("S");
            }

        } else if (id == R.id.nav_medium) {
            productGridFragment.showProductByCategory ("M");

        } else if (id == R.id.nav_large) {
            productGridFragment.showProductByCategory ("L");

        } else if (id == R.id.nav_extra_large) {
            productGridFragment.showProductByCategory ("XL");
        } else if (id == R.id.nav_extra_large_plus) {

        } else if (id == R.id.nav_all) {
            productGridFragment.showProductByCategory (null);

        } else if (id == R.id.nav_customize) {
            Toast.makeText (mContext, "Customize", Toast.LENGTH_SHORT).show ();
        } else {
            productGridFragment.showProductByCategory (null);
        }

        mDrawer.closeDrawer (GravityCompat.START);
        mDrawer.closeDrawer (GravityCompat.END);

        return true;
    }

    @Override
    public void onClick (View v) {
        int id = v.getId ();
        switch (id) {
            case R.id.img_grid_view:

                mSelectedOption = OPTIONS.DEFAULT.ordinal ();
                if (mSelectedView == AppConstant.VIEW_TYPE.GRID.ordinal ()) {
                    mSelectedView = AppConstant.VIEW_TYPE.LIST.ordinal ();
                } else if (mSelectedView == AppConstant.VIEW_TYPE.LIST.ordinal ()) {
                    mSelectedView = AppConstant.VIEW_TYPE.GRID.ordinal ();
                }
                loadFragment (mSelectedOption, null);

                break;
            case R.id.img_filter:
                showSingleChoiceListDialog();

                break;
        }

    }

    @Override
    public void onBackStackChanged () {

    }

    @Override
    public void onFragmentInteraction (Bundle _bundle) {
        if (_bundle != null) {
            int action = _bundle.getInt ("action");
            boolean status = false;
            Product product = (Product) _bundle.getParcelable ("data");
            if(action == AppConstant.ACTIONS.PRODUCT_DETAILS.ordinal ()){
                displayProductDetails (AppConstant.ACTIONS.PRODUCT_DETAILS.name (), product);

            }else if(action == AppConstant.ACTIONS.BUY_NOW.ordinal ()){
                status = addToCart (product);
                if(status == true){
                    showCart ();
                }

            }else if(action == AppConstant.ACTIONS.ADD_TO_CART.ordinal ()){
                status = addToCart (product);
                if(status == true){
                    Snackbar.make (mContentWrapper, getString (R.string.msg_item_added_cart), Snackbar.LENGTH_SHORT).show ();
                }

            }else if(action == AppConstant.ACTIONS.LIKE.ordinal ()){
                Toast.makeText (mContext,"LIKE", Toast.LENGTH_SHORT).show ();

            }else if(action == AppConstant.ACTIONS.UNLIKE.ordinal ()){
                Toast.makeText (mContext,"UNLIKE", Toast.LENGTH_SHORT).show ();

            }

        }
    }

    private boolean addToCart (Product _product) {
        ArrayList<ShoppingCart> cartItem = new ArrayList<ShoppingCart> ();
        ShoppingCart shoppingCart = new ShoppingCart ();
        shoppingCart.setProductID (Integer.valueOf (_product.getProductID ()));
        shoppingCart.setProductName (_product.getFlowerName ());
        shoppingCart.setProductCategory (_product.getCategory ());
        shoppingCart.setProductImage (_product.getImage ());
        shoppingCart.setProductQuantity (_product.getQty ());
        shoppingCart.setProductPrice (_product.getPrice ());
        shoppingCart.setUserMessage ("");
        shoppingCart.setShoppingCartQuantity (1);
        shoppingCart.setStatus (1);

        cartItem.add (shoppingCart);

        LocalDAO localDAO = new LocalDAO (mContext);
        boolean status = localDAO.addItemsToCart (cartItem);
        return status;
    }

    private void displayProductDetails (String _action, Product _product) {
        Intent intent = new Intent (mContext, ProductDetailActivity.class);
        intent.setAction (_action);
        intent.addFlags (Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra ("data", _product);
        startActivity (intent);
    }

    private void initializeView () {
        mContentWrapper = (RelativeLayout) findViewById (R.id.content_wrapper);
        mToolbar = (Toolbar) findViewById (R.id.toolbar);
        mDrawer = (DrawerLayout) findViewById (R.id.drawer_layout);
        mNavigationViewLeft = (NavigationView) findViewById (R.id.nav_view_left);
        mNavigationViewRight = (NavigationView) findViewById (R.id.nav_view_right);
        mSelView = (ImageView) findViewById (R.id.img_grid_view);
        mFilter = (ImageView) findViewById (R.id.img_filter);

        View header = mNavigationViewLeft.getHeaderView (0);
        mUserProfilePic = (CircleImageView) header.findViewById (R.id.user_profile_pic);
        mUserName = (TextView) header.findViewById (R.id.user_name);
        mUserEmail = (TextView) header.findViewById (R.id.user_email);
        mProductSearch = (SearchView) findViewById (R.id.sv_productSearchInput);

        //Adding a name, email and profile pic to the left navigation drawer
        String profilePic = UserPreference.getProfilePic ();
        if(!TextUtils.isEmpty (profilePic)){
            Picasso.with(mContext).load(profilePic).into(mUserProfilePic);
        }

        String userName = "";
        if(!TextUtils.isEmpty (UserPreference.getUserFirstName ())){
            userName = UserPreference.getUserFirstName ();
        }

        if(!TextUtils.isEmpty (UserPreference.getUserLastName ())){
            if(TextUtils.isEmpty (userName)){
                userName = UserPreference.getUserLastName ();
            }else{
                userName = userName + " " + UserPreference.getUserLastName ();
            }
        }
        mUserName.setText (userName);

        if(!TextUtils.isEmpty (UserPreference.getUserEmail ())){
            mUserEmail.setText (UserPreference.getUserEmail ());
        }

        //UI Search Implementation
        search (mProductSearch);

    }

    private void loadFragment (int _selectedOptions, String _data) {
        if (fragmentManager == null) {
            fragmentManager = getSupportFragmentManager ();
            fragmentManager.addOnBackStackChangedListener (this);
        }

        productGridFragment = null;

        if (_selectedOptions == OPTIONS.DEFAULT.ordinal ()) {

            if (mSelectedView == AppConstant.VIEW_TYPE.GRID.ordinal ()) {
                Bundle args = new Bundle ();
                args.putInt ("selectedView", mSelectedView);
                args.putParcelableArrayList ("products", mProductList);

                productGridFragment = ProductGridFragment.newInstance (args);
                fragmentManager.beginTransaction ().replace (R.id.fragment_container, productGridFragment, "product_grid").commitAllowingStateLoss ();

            } else {
                Bundle args = new Bundle ();
                args.putInt ("selectedView", mSelectedView);
                args.putParcelableArrayList ("products", mProductList);

                productGridFragment = ProductGridFragment.newInstance (args);
                fragmentManager.beginTransaction ().replace (R.id.fragment_container, productGridFragment, "product_grid").commitAllowingStateLoss ();

            }

        }

    }

    private void showCart () {
        Intent intent = new Intent (mContext, CartActivity.class);
        intent.setAction (AppConstant.ACTIONS.SHOW_CART.name ());
        intent.addFlags (Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity (intent);
    }


    private void showProfile () {
        Intent intent = new Intent (mContext, ProfileActivity.class);
        intent.setAction (AppConstant.ACTIONS.USER_PROFILE.name ());
        intent.addFlags (Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity (intent);
    }

    private void showOrder () {
        Intent intent = new Intent (mContext, OrderActivity.class);
        intent.setAction (AppConstant.ACTIONS.SHOW_ORDER.name ());
        intent.addFlags (Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity (intent);
    }


    private void getProductStoredLocally (Context _context) {
        //Fetch products saved locally (if any), and display
        LocalDAO localDAO = new LocalDAO (_context);
        JSONArray jsonArray = localDAO.getProducts ();

        if (jsonArray != null) {

            Gson gson = new Gson ();

            Type listType = new TypeToken<ArrayList<Product>> () {
            }.getType ();

            mProductList = null;
            mProductList = gson.fromJson (jsonArray.toString (), listType);

        }
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

    private void search(SearchView _searchview){
        _searchview.setOnQueryTextListener (new SearchView.OnQueryTextListener () {
            @Override
            public boolean onQueryTextSubmit (String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange (String newText) {
                if(productGridFragment != null){
                    productGridFragment.searchItem (newText);
                }
                return true;
            }
        });
    }

    //Single choice list dialog
    private void showSingleChoiceListDialog(){
        mSingleChoiceListDialog  = new MaterialDialog.Builder(this)
            .title(R.string.title_filter_by)
            .items(R.array.filter_product)
            .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                @Override
                public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                    if(text.toString ().equalsIgnoreCase ("price")){
                        mSelectedSortingOption = AppConstant.SORTING_OPTION.BY_PRICE.ordinal ();
                    }else{
                        mSelectedSortingOption = AppConstant.SORTING_OPTION.BY_CATEGORY.ordinal ();
                    }

                    if (productGridFragment != null) {
                        productGridFragment.sort (mSelectedSortingOption);
                    }
                    return true;
                }
            })
            .positiveText (R.string.btn_choose)
            .show();
    }

    private void logout(Context _context){
        signout ();
        Intent intent = new Intent (_context, LauncherActivity.class);
        intent.setAction (AppConstant.ACTIONS.LOGIN.name ());
        intent.addFlags (Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity (intent);
        finish ();
    }

}