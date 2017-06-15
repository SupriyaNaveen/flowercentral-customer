package com.flowercentral.flowercentralcustomer.delivery;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.flowercentral.flowercentralcustomer.BaseActivity;
import com.flowercentral.flowercentralcustomer.R;
import com.flowercentral.flowercentralcustomer.preference.UserPreference;
import com.flowercentral.flowercentralcustomer.rest.BaseModel;
import com.flowercentral.flowercentralcustomer.rest.QueryBuilder;
import com.flowercentral.flowercentralcustomer.util.MapActivity;
import com.flowercentral.flowercentralcustomer.util.PermissionUtil;
import com.flowercentral.flowercentralcustomer.util.Util;
import com.flowercentral.flowercentralcustomer.volley.ErrorData;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddressActivity extends BaseActivity implements View.OnClickListener, OnMapReadyCallback {

    private static final int REQUEST_CODE_MAP = 1000;
    private static final String TAG = AddressActivity.class.getSimpleName();
    private TextInputEditText mCustomerName;
    private TextInputEditText mCustomerAddress;
    private TextInputEditText mCustomerCity;
    private TextInputEditText mCustomerState;
    private TextInputEditText mCustomerZip;
    private TextInputEditText mCustomerPhone;

    private double mLatitude;
    private double mLongitude;
    private GoogleMap mGoogleMap;

    private CoordinatorLayout mRootLayout;
    private Marker mMarker;
    private MaterialDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        mContext = this;
        //Get data from Intent
//        Intent intent = getIntent();
//        if (intent != null) {
//            Bundle bundle = intent.getExtras();
//            if (bundle != null) {
//                String mAction = bundle.getString("action");
//            }
//        }

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
        mCustomerPhone = (TextInputEditText) findViewById(R.id.edt_phone1);

        ImageView mPickLocation = (ImageView) findViewById(R.id.img_view_locate);
        Button mBtnContinue = (Button) findViewById(R.id.btn_continue);
        Button mBtnCancel = (Button) findViewById(R.id.btn_cancel);
        TextView mCheckOnDeliveryAddress = (TextView) findViewById(R.id.tv_check_on_delivery_address);

        //Setup toolbar
        setSupportActionBar(mToolbar);
        if (null != getSupportActionBar())
            getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_activity_address));

        //Setup click listener
        mPickLocation.setOnClickListener(this);
        mBtnContinue.setOnClickListener(this);
        mBtnCancel.setOnClickListener(this);
        mCheckOnDeliveryAddress.setOnClickListener(this);

        mLatitude = UserPreference.getLatitude();
        mLongitude = UserPreference.getLongitude();

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onClick(View v) {
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
                    submitOrderDetailsToServer();
                }

                break;
            case R.id.img_view_locate:
                //Check the address, if there then locate the map based on that.
                // Or else, locate the current address.
                // On activity result change the map in this activity based on location.
                if (mCustomerAddress.getText().length() > 0) {
                    locateAddress(mCustomerAddress.getText().toString());
                }
                Intent mapIntent = new Intent(this, MapActivity.class);
                mapIntent.putExtra(getString(R.string.key_latitude), mLatitude);
                mapIntent.putExtra(getString(R.string.key_longitude), mLongitude);
                mapIntent.putExtra(getString(R.string.key_address), mCustomerAddress.getText());
                mapIntent.putExtra(getString(R.string.key_is_draggable), true);
                startActivityForResult(mapIntent, REQUEST_CODE_MAP);
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
                        locateAddress(mCustomerAddress.getText().toString()
                                + mCustomerCity.getText()
                                + mCustomerState.getText());
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
                            Snackbar.make(mRootLayout, getResources().getString(R.string.msg_internet_unavailable), Snackbar.LENGTH_SHORT).show();
                            break;
                        case INTERNAL_SERVER_ERROR:
                            Snackbar.make(mRootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case CONNECTION_TIMEOUT:
                            Snackbar.make(mRootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case APPLICATION_ERROR:
                            Snackbar.make(mRootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case INVALID_INPUT_SUPPLIED:
                            Snackbar.make(mRootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case AUTHENTICATION_ERROR:
                            Snackbar.make(mRootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case UNAUTHORIZED_ERROR:
                            Snackbar.make(mRootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        default:
                            Snackbar.make(mRootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
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
            user.put(getString(R.string.api_key_phone1), mCustomerPhone.getText());
            return user;
        } catch (JSONException e) {
            Snackbar.make(mRootLayout, getResources().getString(R.string.msg_reg_user_missing_input), Snackbar.LENGTH_SHORT).show();
        }
        return null;
    }

    private void submitOrderDetailsToServer() {
        registerAddress(this, constructJsonObject());
    }

    private void registerAddress(Activity _context, JSONObject _user) {
        //Start Progress dialog
        dismissDialog();

        mProgressDialog = Util.showProgressDialog(_context, null, getString(R.string.msg_registering_user), false);

        BaseModel<JSONObject> baseModel = new BaseModel<JSONObject>(_context) {
            @Override
            public void onSuccess(int statusCode, Map<String, String> headers, JSONObject response) {

                //TODO Handle res
                //CLose Progress dialog
                dismissDialog();
                try {
                    if (response.getInt(getString(R.string.api_res_status)) == 1) {
                        Snackbar.make(mRootLayout, "Success", Snackbar.LENGTH_SHORT).show();
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
                            Snackbar.make(mRootLayout, getResources().getString(R.string.msg_internet_unavailable), Snackbar.LENGTH_SHORT).show();
                            break;
                        case INTERNAL_SERVER_ERROR:
                            Snackbar.make(mRootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case CONNECTION_TIMEOUT:
                            Snackbar.make(mRootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case APPLICATION_ERROR:
                            Snackbar.make(mRootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case INVALID_INPUT_SUPPLIED:
                            Snackbar.make(mRootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case AUTHENTICATION_ERROR:
                            Snackbar.make(mRootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case UNAUTHORIZED_ERROR:
                            Snackbar.make(mRootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        default:
                            Snackbar.make(mRootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                    }
                }
            }
        };

        String url = QueryBuilder.getDeliveryAddressUrl();
        if (_user != null) {
            baseModel.executePostJsonRequest(url, _user, TAG);
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

        if (mCustomerPhone.getText().toString().isEmpty()) {
            mCustomerPhone.setError(getString(R.string.err_empty_text));
            isValid = false;
        } else {
            mCustomerPhone.setError(null);
        }

        return isValid;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_MAP:
                mLatitude = data.getDoubleExtra(getString(R.string.key_latitude), 0.0);
                mLongitude = data.getDoubleExtra(getString(R.string.key_longitude), 0.0);
                setMap();
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
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
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mGoogleMap = map;
        requestPermission();
    }

    /**
     * Request for the runtime permission (SDK >= Marshmallow devices)
     */
    private void requestPermission() {
        if (PermissionUtil.hasPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            setMap();
        } else {
            PermissionUtil.requestPermission(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PermissionUtil.REQUEST_CODE_LOCATION);
        }
    }

    private void setMap() {
        try {
            mGoogleMap.setMyLocationEnabled(true);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        LatLng location = new LatLng(mLatitude, mLongitude);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16));

        if (null != mMarker) {
            mMarker.remove();
        }
        mMarker = mGoogleMap.addMarker(new MarkerOptions()
                .title(getCityName()).visible(true)
                .position(location));
        mMarker.showInfoWindow();
    }

    private String getCityName() {
        Geocoder geocoder = new Geocoder(AddressActivity.this, Locale.getDefault());
        List<Address> addresses;
        String cityName = mCustomerAddress.getText().toString();
        try {
            addresses = geocoder.getFromLocation(mLatitude, mLongitude, 1);
            cityName = addresses.get(0).getAddressLine(0);
        } catch (IOException | IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return cityName;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PermissionUtil.REQUEST_CODE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setMap();
                } else {
                    if (PermissionUtil.showRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                        snackBarRequestPermission();
                    } else {
                        snackBarRedirectToSettings();
                    }
                }
                break;
        }
    }

    /**
     * Displays the snack bar to request the permission from user
     */
    private void snackBarRequestPermission() {
        Snackbar snackbar = Snackbar.make(mRootLayout, getResources().getString(R.string
                .s_required_permission_location), Snackbar.LENGTH_INDEFINITE).setAction(getResources().getString(R.string
                .s_action_request_again), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermission();
            }
        });
        snackbar.show();
    }

    /**
     * If the user checked "Never ask again" option and deny the permission then request dialog
     * cannot be invoked. So display SnackBar to redirect to Settings to grant the permissions
     */
    private void snackBarRedirectToSettings() {
        Snackbar snackbar = Snackbar.make(mRootLayout, getResources()
                .getString(R.string.s_required_permission_settings), Snackbar.LENGTH_INDEFINITE)
                .setAction(getResources().getString(R.string.s_action_redirect_settings), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Navigate to app details settings
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, PermissionUtil.REQUEST_CODE_LOCATION);
                    }
                });
        snackbar.show();
    }
}
