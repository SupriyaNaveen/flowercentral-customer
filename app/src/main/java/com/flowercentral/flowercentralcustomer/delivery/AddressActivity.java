package com.flowercentral.flowercentralcustomer.delivery;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import com.andexert.library.RippleView;

import com.flowercentral.flowercentralcustomer.BaseActivity;
import com.flowercentral.flowercentralcustomer.BuildConfig;
import com.flowercentral.flowercentralcustomer.R;
import com.flowercentral.flowercentralcustomer.common.model.ShoppingCart;
import com.flowercentral.flowercentralcustomer.dao.LocalDAO;
import com.flowercentral.flowercentralcustomer.preference.UserPreference;
import com.flowercentral.flowercentralcustomer.rest.BaseModel;
import com.flowercentral.flowercentralcustomer.rest.QueryBuilder;
import com.flowercentral.flowercentralcustomer.setting.AppConstant;
import com.flowercentral.flowercentralcustomer.util.Logger;
import com.flowercentral.flowercentralcustomer.util.MapActivity;
import com.flowercentral.flowercentralcustomer.util.Util;
import com.flowercentral.flowercentralcustomer.volley.ErrorData;

import com.instamojo.android.activities.PaymentDetailsActivity;
import com.instamojo.android.callbacks.OrderRequestCallBack;
import com.instamojo.android.helpers.Constants;
import com.instamojo.android.models.Errors;
import com.instamojo.android.models.Order;
import com.instamojo.android.network.Request;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddressActivity extends BaseActivity implements RippleView.OnRippleCompleteListener {

    private static final int REQUEST_CODE_MAP = 1000;
    private static final String TAG = AddressActivity.class.getSimpleName();
    private static final Object BLANK_SPACE = " ";
    private Activity mCurrentActivity;

    private TextInputEditText mCustomerName;
    private TextInputEditText mCustomerAddress;
    private TextInputEditText mCustomerCity;
    private TextInputEditText mCustomerState;
    private TextInputEditText mCustomerZip;
    private TextInputEditText mCustomerPrimaryPhone;
    private TextInputEditText mCustomerAlternatePhone;
    private ProgressBar mProgressBar;

    private double mLatitude;
    private double mLongitude;

    private CoordinatorLayout mRootLayout;
    private MaterialDialog mProgressDialog;
    private ImageView mMapImageView;
    private int mImgHtInPx;
    private int mImgWidthInPx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        mContext = this;
        mCurrentActivity = this;

        //Get data from Intent
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                String mAction = bundle.getString("action");
            }
        }

        //Initialize view elements
        initializeView();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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

    private void initializeView() {

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);

        mRootLayout = (CoordinatorLayout) findViewById(R.id.root_layout);

        mCustomerName = (TextInputEditText) findViewById(R.id.edt_customer_name);
        mCustomerAddress = (TextInputEditText) findViewById(R.id.edt_customer_address);
        mCustomerCity = (TextInputEditText) findViewById(R.id.edt_city);
        mCustomerState = (TextInputEditText) findViewById(R.id.edt_state);
        mCustomerZip = (TextInputEditText) findViewById(R.id.edt_zip);
        mCustomerPrimaryPhone = (TextInputEditText) findViewById(R.id.edt_phone1);
        mCustomerAlternatePhone = (TextInputEditText) findViewById(R.id.edt_phone2);

        RippleView mPickLocation = (RippleView) findViewById(R.id.img_view_locate);
        RippleView mBtnContinue = (RippleView) findViewById(R.id.btn_continue);
        RippleView mBtnCancel = (RippleView) findViewById(R.id.btn_cancel);
        RippleView mCheckOnDeliveryAddress = (RippleView) findViewById(R.id.tv_check_on_delivery_address);

        //Setup toolbar
        setSupportActionBar(mToolbar);
        if (null != getSupportActionBar())
            getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_activity_address));

        //Setup click listener
        mPickLocation.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView v) {
                //Check the address, if there then locate the map based on that.
                // Or else, locate the current address.
                // On activity result change the map in this activity based on location.
                if (mCustomerAddress.getText().length() > 0) {
                    locateAddress(mCustomerAddress.getText().toString());
                }
                Intent mapIntent = new Intent(AddressActivity.this, MapActivity.class);
                mapIntent.putExtra(getString(R.string.key_latitude), mLatitude);
                mapIntent.putExtra(getString(R.string.key_longitude), mLongitude);
                mapIntent.putExtra(getString(R.string.key_address), mCustomerAddress.getText());
                mapIntent.putExtra(getString(R.string.key_is_draggable), true);
                startActivityForResult(mapIntent, REQUEST_CODE_MAP);
            }
        });
        mBtnContinue.setOnRippleCompleteListener(this);
        mBtnCancel.setOnRippleCompleteListener(this);
        mCheckOnDeliveryAddress.setOnRippleCompleteListener(this);

        mCustomerName.setText(UserPreference.getUserFirstName());
        mCustomerAddress.setText(UserPreference.getAddress());
        mCustomerCity.setText(UserPreference.getUserCity());
        mCustomerState.setText(UserPreference.getUserState());
        mCustomerZip.setText(UserPreference.getUserPin());
        mCustomerPrimaryPhone.setText(UserPreference.getUserPhone());

        mLatitude = UserPreference.getLatitude();
        mLongitude = UserPreference.getLongitude();

        mMapImageView = (ImageView) findViewById(R.id.map);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

        ViewTreeObserver vto = mMapImageView.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                mMapImageView.getViewTreeObserver().removeOnPreDrawListener(this);
                mImgHtInPx = pxToDp(mMapImageView.getMeasuredHeight());
                mImgWidthInPx = pxToDp(mMapImageView.getMeasuredWidth());
                setMap();
                return true;
            }
        });

    }

    private void setMap() {

        String url;
        mProgressBar.setVisibility(View.VISIBLE);
        if (mLatitude == 0f && mLongitude == 0f) {
            url = "https://maps.googleapis.com/maps/api/staticmap?center=0,0&zoom=1" +
                    "&size=" + mImgWidthInPx + "x" + mImgHtInPx;
        } else {
            url = "https://maps.googleapis.com/maps/api/staticmap?" +
                    "center=" + mLatitude + "," + mLongitude +
                    "&zoom=15" +
                    "&size=" + mImgWidthInPx + "x" + mImgHtInPx +
                    "&maptype=roadmap" +
                    "&markers=color:red%7C" + mLatitude + "," + mLongitude;
        }
        Picasso.
                with(AddressActivity.this).
                load(url).
                fit().
                into(mMapImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        mProgressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        mProgressBar.setVisibility(View.GONE);
                    }
                });

    }

    public int pxToDp(int px) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    @Override
    public void onComplete(RippleView v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_continue:
                /* Todo
                 * Submit order details to the server
                 * Get order id
                 * Initiate Payment process
                 */
                boolean isValidInput = isValidInput();
                if (isValidInput) {

                    try {
                        //Get Order details
                        JSONObject order = new JSONObject();

                        order.put("order_date", Util.getCurrentDateTimeStamp("MM-dd-YYYY"));
                        order.put("order_status", "New");

                        //Get customer Details
                        JSONObject user = new JSONObject();
                        if (TextUtils.isEmpty(UserPreference.getAccessToken())) {
                            Logger.log(TAG, "onClick - btnContinue: ", "Access token is null", AppConstant.LOG_LEVEL_ERR);
                            //Todo Should redirect user to login page

                            return;

                        }
                        user.put("access_token", UserPreference.getAccessToken());
                        user.putOpt("userID", UserPreference.getUserID());
                        user.putOpt("email", UserPreference.getUserEmail());

                        order.put("user", user);

                        //Get Delivery Address
                        JSONObject deliveryAddress = getDeliveryAddress();
                        if (deliveryAddress == null) {
                            Snackbar.make(mRootLayout, getString(R.string.msg_err_delivery_address), Snackbar.LENGTH_SHORT).show();
                            return;
                        }
                        order.put("delivery_address", deliveryAddress);
                        order.put("client_id",BuildConfig.INSTA_MOJO_CLIENT_ID);
                        order.put("client_secret",BuildConfig.INSTA_MOJO_CLIENT_SECRET);

                        //Get Cart Information
                        JSONArray products = getCartItems(mContext);
                        if (products == null) {
                            Snackbar.make(mRootLayout, getString(R.string.msg_err_cart_items), Snackbar.LENGTH_SHORT).show();
                            return;
                        }
                        order.put("products", products);

                        submitOrder(mContext, mCurrentActivity, order);

                    } catch (JSONException jsonEx) {
                        Logger.log(TAG, "onClick - btnContinue: ", jsonEx.getMessage(), AppConstant.LOG_LEVEL_ERR);

                    } catch (Exception ex) {
                        Logger.log(TAG, "onClick - btnContinue: ", ex.getMessage(), AppConstant.LOG_LEVEL_ERR);

                    }
                }

                break;

            case R.id.btn_cancel:
                finish();
                break;

            case R.id.tv_check_on_delivery_address:
                if (isValidInput()) {
                    checkDeliveryAddress();
                }
                break;
        }
    }

    private void checkDeliveryAddress() {
        //Start Progress dialog
        dismissDialog();

        mProgressDialog = Util.showProgressDialog(this, null, getString(R.string.msg_check_address), false);

        BaseModel<JSONObject> baseModel = new BaseModel<JSONObject>(this) {
            @Override
            public void onSuccess(int statusCode, Map<String, String> headers, JSONObject response) {

                //CLose Progress dialog
                dismissDialog();
                try {
                    if (response.getInt(getString(R.string.api_res_status)) == 1) {
                        Snackbar.make(mRootLayout, "Success", Snackbar.LENGTH_SHORT).show();
                        locateAddress(mCustomerAddress.getText().toString() + BLANK_SPACE +
                                mCustomerCity.getText() + BLANK_SPACE +
                                mCustomerState.getText());
                        setMap();
                    } else {
                        Snackbar.make(mRootLayout, "Fail", Snackbar.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Snackbar.make(mRootLayout, "Fail", Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(ErrorData error) {
                //Close Progress dialog
                dismissDialog();

                if (error != null) {
                    error.setErrorMessage("Delivery address failed. Cause -> " + error.getErrorMessage());

                    switch (error.getErrorType()) {
                        case NETWORK_NOT_AVAILABLE:
                            showMessage (mRootLayout, getResources().getString(R.string.msg_internet_unavailable));
                            break;
                        case INTERNAL_SERVER_ERROR:
                            showMessage(mRootLayout, error.getErrorMessage());
                            break;
                        case CONNECTION_TIMEOUT:
                            showMessage(mRootLayout, error.getErrorMessage());
                            break;
                        case APPLICATION_ERROR:
                            showMessage(mRootLayout, error.getErrorMessage());
                            break;
                        case INVALID_INPUT_SUPPLIED:
                            showMessage(mRootLayout, error.getErrorMessage());
                            break;
                        case AUTHENTICATION_ERROR:
                            showMessage(mRootLayout, error.getErrorMessage());
                            break;
                        case UNAUTHORIZED_ERROR:
                            showMessage(mRootLayout, error.getErrorMessage());
                            break;
                        default:
                            showMessage(mRootLayout, error.getErrorMessage());
                            break;
                    }
                }
            }
        };

        String url = QueryBuilder.getCheckDeliveryAddressUrl();
        JSONObject _user = constructJsonObject();
        if (_user != null) {
            baseModel.executePostJsonRequest(url, _user, TAG);
        } else {
            Snackbar.make(mRootLayout, getResources().getString(R.string.msg_reg_user_missing_input), Snackbar.LENGTH_SHORT).show();
        }
    }

    private JSONObject constructJsonObject() {
        try {
            JSONObject user = new JSONObject();
            user.put(getString(R.string.api_key_name), mCustomerName.getText());
            user.put(getString(R.string.api_key_address), mCustomerAddress.getText());
            user.put(getString(R.string.api_key_city), mCustomerCity.getText());
            user.put(getString(R.string.api_key_city), mCustomerCity.getText());
            user.put(getString(R.string.api_key_state), mCustomerState.getText());
            user.put(getString(R.string.api_key_pin), mCustomerZip.getText());
            user.put(getString(R.string.api_key_phone1), mCustomerPrimaryPhone.getText());
            return user;
        } catch (JSONException e) {
            Snackbar.make(mRootLayout, getResources().getString(R.string.msg_reg_user_missing_input), Snackbar.LENGTH_SHORT).show();
        }
        return null;
    }

    private void submitOrder(Context _context, final Activity _activity, JSONObject _data) {
        //Start Progress dialog
        dismissDialog();

        mProgressDialog = Util.showProgressDialog(_activity, null, getString(R.string.msg_registering_order), false);

        BaseModel<JSONObject> baseModel = new BaseModel<JSONObject>(_context) {
            @Override
            public void onSuccess(int statusCode, Map<String, String> headers, JSONObject response) {

                //TODO Handle res
                //CLose Progress dialog
                dismissDialog();
                try {
                    if (response != null) {
                        // Retrieve transaction_id, order_id, access_token and other order related data from the response.
                        String instamojo_access_token = response.getString ("access_token");
                        String transaction_id = response.getString ("transaction_id");
                        String order_id = response.getString ("order_id");

                        // Fetch order by using order_id and access_token
                        fetchOrder(_activity, instamojo_access_token, order_id);

                    } else {
                        Snackbar.make(mRootLayout, getString(R.string.msg_order_fail), Snackbar.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Snackbar.make(mRootLayout, "Fail", Snackbar.LENGTH_SHORT).show();
                }catch (Exception ex){

                }
            }

            @Override
            public void onError(ErrorData error) {
                //Close Progress dialog
                dismissDialog();

                if (error != null) {
                    error.setErrorMessage("Delivery address failed. Cause -> " + error.getErrorMessage());

                    switch (error.getErrorType()) {
                        case NETWORK_NOT_AVAILABLE:
                            showMessage (mRootLayout, getResources().getString(R.string.msg_internet_unavailable));
                            break;
                        case INTERNAL_SERVER_ERROR:
                            showMessage(mRootLayout, error.getErrorMessage());
                            break;
                        case CONNECTION_TIMEOUT:
                            showMessage(mRootLayout, error.getErrorMessage());
                            break;
                        case APPLICATION_ERROR:
                            showMessage(mRootLayout, error.getErrorMessage());
                            break;
                        case INVALID_INPUT_SUPPLIED:
                            showMessage(mRootLayout, error.getErrorMessage());
                            break;
                        case AUTHENTICATION_ERROR:
                            showMessage(mRootLayout, error.getErrorMessage());
                            break;
                        case UNAUTHORIZED_ERROR:
                            showMessage(mRootLayout, error.getErrorMessage());
                            break;
                        default:
                            showMessage(mRootLayout, error.getErrorMessage());
                            break;
                    }
                }
            }
        };

        String url = QueryBuilder.getSubmitOrderUrl();

        if (_data != null) {
            baseModel.executePostJsonRequest(url, _data, TAG);
        } else {
            Snackbar.make(mRootLayout, getResources().getString(R.string.msg_reg_user_missing_input), Snackbar.LENGTH_SHORT).show();
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

    /**
     * Validator class
     *
     * @return is valid or not
     */
    private boolean isValidInput() {
        boolean isValid = true;

        if (mCustomerName.getText().toString().isEmpty()) {
            mCustomerName.setError(getString(R.string.err_empty_text));
            isValid = false;
        } else {
            mCustomerName.setError(null);
        }

        if (mCustomerAddress.getText().toString().isEmpty()) {
            mCustomerAddress.setError(getString(R.string.err_empty_text));
            isValid = false;
        } else {
            mCustomerAddress.setError(null);
        }

        if (mCustomerCity.getText().toString().isEmpty()) {
            mCustomerCity.setError(getString(R.string.err_empty_text));
            isValid = false;
        } else {
            mCustomerCity.setError(null);
        }

        if (mCustomerState.getText().toString().isEmpty()) {
            mCustomerState.setError(getString(R.string.err_empty_text));
            isValid = false;
        } else {
            mCustomerState.setError(null);
        }

        if (mCustomerZip.getText().toString().isEmpty()) {
            mCustomerZip.setError(getString(R.string.err_empty_text));
            isValid = false;
        } else {
            mCustomerZip.setError(null);
        }

        if (mCustomerPrimaryPhone.getText().toString().isEmpty()) {
            mCustomerPrimaryPhone.setError(getString(R.string.err_empty_text));
            isValid = false;
        } else {
            mCustomerPrimaryPhone.setError(null);
        }

        return isValid;
    }

    private void locateAddress(String strAddress) {

        Geocoder coder = new Geocoder(this);
        List<Address> address;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                Toast.makeText(this, getString(R.string.map_error_unable_locate_address), Toast.LENGTH_LONG).show();
                return;
            }

            Address location = address.get(0);
            mLongitude = location.getLongitude();
            mLatitude = location.getLatitude();
        } catch (IOException e) {
            Toast.makeText(this, getString(R.string.map_error_unable_locate_address), Toast.LENGTH_LONG).show();
        } catch (IndexOutOfBoundsException e) {
            Toast.makeText(this, getString(R.string.map_error_unable_locate_address), Toast.LENGTH_LONG).show();
        }
    }

    public JSONObject getDeliveryAddress() {
        JSONObject deliveryAddress = new JSONObject();
        try {
            deliveryAddress.put("customer_name", mCustomerName.getText().toString());
            deliveryAddress.put("address", mCustomerAddress.getText().toString());
            deliveryAddress.put("city", mCustomerCity.getText().toString());
            deliveryAddress.put("state", mCustomerState.getText().toString());
            deliveryAddress.put("zip", mCustomerZip.getText().toString());
            deliveryAddress.put("primary_phone", mCustomerPrimaryPhone.getText().toString());
            deliveryAddress.putOpt("alternate_phone", mCustomerAlternatePhone.getText().toString());

        } catch (JSONException jsonEx) {
            deliveryAddress = null;
            Logger.log(TAG, "onClick - btnContinue: ", jsonEx.getMessage(), AppConstant.LOG_LEVEL_ERR);

        } catch (Exception ex) {
            deliveryAddress = null;
            Logger.log(TAG, "onClick - btnContinue: ", ex.getMessage(), AppConstant.LOG_LEVEL_ERR);
        }

        return deliveryAddress;
    }

    private JSONArray getCartItems(Context _context) {
        JSONArray products = null;

        LocalDAO localDAO = new LocalDAO(_context);
        ArrayList<ShoppingCart> cartItems = localDAO.getCartItems();
        try {
            if (cartItems != null && cartItems.size() > 0) {
                products = new JSONArray();
                for (ShoppingCart item : cartItems) {
                    JSONObject product = new JSONObject();
                    product.put("product_id", item.getProductID());
                    product.put("product_qty", item.getProductQuantity());
                    product.putOpt("user_message", item.getUserMessage());

                    products.put(product);

                }

            } else {
                products = null;
            }
        } catch (JSONException jsonEx) {
            products = null;

        } catch (Exception ex) {
            products = null;

        }

        return products;
    }

    private void fetchOrder (Activity _activity, String _instamojo_access_token, String _order_id) {
        // Good time to show dialog
        //Start Progress dialog
        dismissDialog();
        mProgressDialog = Util.showProgressDialog(_activity, null, getString(R.string.msg_registering_order), false);

        Request request = new Request(_instamojo_access_token, _order_id, new OrderRequestCallBack () {
            @Override
            public void onFinish(final Order order, final Exception error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissDialog();
                        if (error != null) {
                            if (error instanceof Errors.ConnectionError) {
                                showMessage(mRootLayout, "No internet connection");
                            } else if (error instanceof Errors.ServerError) {
                                showMessage(mRootLayout, "Server Error. Try again");
                            } else if (error instanceof Errors.AuthenticationError) {
                                showMessage(mRootLayout, "Access token is invalid or expired. Please Update the token!!");
                            } else {
                                showMessage(mRootLayout, error.toString());
                            }
                            return;
                        }

                        startPreCreatedUI(order);
                    }
                });

            }
        });
        request.execute();
    }

    private void startPreCreatedUI (Order _order) {
        //Using Pre created UI
        Intent intent = new Intent(getBaseContext(), PaymentDetailsActivity.class);
        intent.putExtra(AppConstant.ORDER_DATA_KEY, _order);
        startActivityForResult(intent, Constants.REQUEST_CODE);
    }

    private void showMessage (View _view, String _msg) {
        Snackbar.make(_view, _msg, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_MAP:
                mLatitude = data.getDoubleExtra(getString(R.string.key_latitude), 0.0);
                mLongitude = data.getDoubleExtra(getString(R.string.key_longitude), 0.0);
                setMap();
                break;

            case AppConstant.PAYMENT_REQUEST_CODE:
                if (data != null) {
                    String orderID = data.getStringExtra(Constants.ORDER_ID);
                    String transactionID = data.getStringExtra(Constants.TRANSACTION_ID);
                    String paymentID = data.getStringExtra(Constants.PAYMENT_ID);

                    // Check transactionID, orderID, and orderID for null before using them to
                    // check the Payment status.
                    if (orderID != null && transactionID != null && paymentID != null) {
                        //Check for Payment status with Order ID or Transaction ID
                        HashMap<String, String> payload = new HashMap<String, String> ();
                        payload.put ("order_id", orderID);
                        payload.put ("transaction_id", transactionID);
                        payload.put ("payment_id", paymentID);
                        checkPaymentStatus(mCurrentActivity, mContext, payload);

                    } else {
                        //Oops!! Payment was cancelled
                    }
                }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void checkPaymentStatus (Activity _activity, Context _context, HashMap<String, String> _payload) {
        //Start Progress dialog
        dismissDialog();

        mProgressDialog = Util.showProgressDialog(_activity, null, getString(R.string.msg_checking_payment_status), false);

        BaseModel<JSONObject> baseModel = new BaseModel<JSONObject>(_context) {
            @Override
            public void onSuccess(int statusCode, Map<String, String> headers, JSONObject response) {

                //TODO Handle res
                //CLose Progress dialog
                dismissDialog();
                try {
                    if (response != null) {
                        //Display message and redirect to home page on success
                        String status = response.getString ("status");


                    } else {
                        showMessage (mRootLayout, getString (R.string.updating_payment_status_failed));
                    }
                } catch (JSONException e) {
                    Snackbar.make(mRootLayout, "Fail", Snackbar.LENGTH_SHORT).show();
                }catch (Exception ex){

                }
            }

            @Override
            public void onError(ErrorData error) {
                //Close Progress dialog
                dismissDialog();

                if (error != null) {
                    error.setErrorMessage("Delivery address failed. Cause -> " + error.getErrorMessage());

                    switch (error.getErrorType()) {
                        case NETWORK_NOT_AVAILABLE:
                            showMessage (mRootLayout, getResources().getString(R.string.msg_internet_unavailable));
                            break;
                        case INTERNAL_SERVER_ERROR:
                            showMessage(mRootLayout, error.getErrorMessage());
                            break;
                        case CONNECTION_TIMEOUT:
                            showMessage(mRootLayout, error.getErrorMessage());
                            break;
                        case APPLICATION_ERROR:
                            showMessage(mRootLayout, error.getErrorMessage());
                            break;
                        case INVALID_INPUT_SUPPLIED:
                            showMessage(mRootLayout, error.getErrorMessage());
                            break;
                        case AUTHENTICATION_ERROR:
                            showMessage(mRootLayout, error.getErrorMessage());
                            break;
                        case UNAUTHORIZED_ERROR:
                            showMessage(mRootLayout, error.getErrorMessage());
                            break;
                        default:
                            showMessage(mRootLayout, error.getErrorMessage());
                            break;
                    }
                }
            }
        };

        String url = QueryBuilder.getSubmitOrderUrl();

        if (_payload != null) {
            JSONObject payload = new JSONObject (_payload);
            baseModel.executePostJsonRequest(url, payload, TAG);
        } else {
            Snackbar.make(mRootLayout, getResources().getString(R.string.msg_reg_user_missing_input), Snackbar.LENGTH_SHORT).show();
        }
    }

}
