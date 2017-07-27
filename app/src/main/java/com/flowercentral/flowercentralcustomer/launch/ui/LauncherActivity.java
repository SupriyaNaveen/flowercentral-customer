package com.flowercentral.flowercentralcustomer.launch.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.andexert.library.RippleView;
import com.binarysoft.sociallogin.facebook.FacebookGraphListner;
import com.binarysoft.sociallogin.facebook.FacebookUser;
import com.binarysoft.sociallogin.google.GoogleUser;
import com.binarysoft.sociallogin.instagram.InstagramUser;
import com.flowercentral.flowercentralcustomer.BaseActivity;
import com.flowercentral.flowercentralcustomer.R;
import com.flowercentral.flowercentralcustomer.common.location.LocationApi;
import com.flowercentral.flowercentralcustomer.common.location.LocationApiConstants;
import com.flowercentral.flowercentralcustomer.common.model.Product;
import com.flowercentral.flowercentralcustomer.dao.LocalDAO;
import com.flowercentral.flowercentralcustomer.dashboard.Dashboard;
import com.flowercentral.flowercentralcustomer.preference.UserPreference;
import com.flowercentral.flowercentralcustomer.rest.BaseModel;
import com.flowercentral.flowercentralcustomer.rest.QueryBuilder;
import com.flowercentral.flowercentralcustomer.setting.AppConstant;
import com.flowercentral.flowercentralcustomer.util.Util;
import com.flowercentral.flowercentralcustomer.volley.ErrorData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class LauncherActivity extends BaseActivity implements RippleView.OnRippleCompleteListener {

    private final String TAG = LauncherActivity.class.getSimpleName();
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private boolean mVisible;

    //Bind UI View Controls
    private RippleView mBtnTryAgain;

    private FrameLayout mFlOuterWrapper;
    private FrameLayout mFlNoInternet;
    private LinearLayout mllLoginWrapper;

    private RippleView mBtnFacebook;
    private RippleView mBtnGoogle;
    private RippleView mBtnInstagram;

    private MaterialDialog mProgressDialog;
    private MaterialDialog mDialog;

    private Activity mCurrentActivity;
    private List<Product> mProductList;
    private boolean mDataDownloaded;
    private boolean mUserRegistered;
    private RelativeLayout mRlOuterLoginWrapper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        mContext = this;
        mCurrentActivity = this;

        mDataDownloaded = false;
        mUserRegistered = false;

        mVisible = true;

        mBtnTryAgain = (RippleView) findViewById(R.id.btn_try_again);
        mBtnFacebook = (RippleView) findViewById(R.id.btn_fb);
        mBtnGoogle = (RippleView) findViewById(R.id.btn_google);
        mBtnInstagram = (RippleView) findViewById(R.id.btn_instagram);

        mFlOuterWrapper = (FrameLayout) findViewById(R.id.outer_wrapper);
        mFlNoInternet = (FrameLayout) findViewById(R.id.fl_no_internet);
        mllLoginWrapper = (LinearLayout) findViewById(R.id.login_wrapper);
        mRlOuterLoginWrapper = (RelativeLayout) findViewById (R.id.rl_outer_login_wrapper);

        mBtnTryAgain.setOnRippleCompleteListener(this);
        mBtnFacebook.setOnRippleCompleteListener(this);
        mBtnGoogle.setOnRippleCompleteListener(this);
        mBtnInstagram.setOnRippleCompleteListener(this);

        initializeActivity(mContext);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    @Override
    public void onGoogleAuthSignIn(String authToken, GoogleUser _userAccount) {
        //String msg = String.format(Locale.US, "User id:%s\n\nAuthToken:%s", userId, authToken);

        //No internet connection then return
        if (!Util.checkInternet(mContext)) {
            Snackbar.make(mFlOuterWrapper, getResources().getString(R.string.msg_internet_unavailable), Snackbar.LENGTH_SHORT).show();
            return;
        }
        //Prepare json object by getting required user info from GoogleUser object
        JSONObject user = new JSONObject();

        try {

            user.put("email", _userAccount.getEmail());
            user.put("auth_token", _userAccount.getIDToken());
            user.put("auth_type", "google");
            user.put("imeiNum", Util.getIEMINumber(mContext));
            user.put("deviceid", Util.getDeviceId(mContext));

            String name = "";
            if (!TextUtils.isEmpty(_userAccount.getGivenName())) {
                name = _userAccount.getGivenName();
            } else if (!TextUtils.isEmpty(_userAccount.getDisplayName())) {
                name = _userAccount.getDisplayName();
            } else if (!TextUtils.isEmpty(_userAccount.getFamilyName())) {
                name = _userAccount.getFamilyName();
            } else {
                name = "";
            }

            user.put("name", name);
            user.put("age", "");
            if (!TextUtils.isEmpty(_userAccount.getPhotoUrl())) {
                user.put("profile_pic", _userAccount.getPhotoUrl());
            } else {
                user.put("profile_pic", "");
            }
            user.put("address", "");
            user.put("phone", "0000000000");

            registerUser(mContext, user);

        } catch (JSONException jsonEx) {
            Snackbar.make(mFlOuterWrapper, "JSON Parsing Error", Snackbar.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onGoogleAuthSignInFailed(String errorMessage) {
        Snackbar.make(mFlOuterWrapper, errorMessage, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onGoogleAuthSignOut() {
        UserPreference.deleteProfileInformation();
        Snackbar.make(mFlOuterWrapper, getString(R.string.msg_logout_success), Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onFbSignInFail(String errorMessage) {
        Snackbar.make(mFlOuterWrapper, errorMessage, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onFbSignInSuccess(final String authToken, String userId) {
        String msg = String.format(Locale.US, "User id:%s\n\nAuthToken:%s", userId, authToken);

        //Must implement this interface before calling get Profile
        mFacebookHelper.registerProfileListener(new FacebookGraphListner() {
            @Override
            public void onProfileReceived(FacebookUser _profile) {
                Log.i(TAG, _profile.toString());
                //No internet connection then return
                if (!Util.checkInternet(mContext)) {
                    Snackbar.make(mFlOuterWrapper, getResources().getString(R.string.msg_internet_unavailable), Snackbar.LENGTH_SHORT).show();
                    return;
                }
                //Prepare json object by getting required user info from GoogleUser object
                JSONObject user = new JSONObject();

                try {

                    user.put("email", _profile.getEmail());
                    user.put("auth_token", authToken);
                    user.put("auth_type", "facebook");
                    user.put("imeiNum", Util.getIEMINumber(mContext));
                    user.put("deviceid", Util.getDeviceId(mContext));

                    String name = _profile.getFirstName() + " " + _profile.getLastName();

                    if (TextUtils.isEmpty(name)) {
                        if (!TextUtils.isEmpty(_profile.getName())) {
                            name = _profile.getName();
                        } else {
                            name = "";
                        }
                    }

                    user.put("name", name);
                    user.put("age", "");

                    if (!TextUtils.isEmpty(_profile.getPicture())) {
                        user.put("profile_pic", _profile.getPicture());
                    } else {
                        user.put("profile_pic", "");
                    }

                    user.put("address", "");
                    user.put("phone", "0000000000");

                    registerUser(mContext, user);

                } catch (JSONException jsonEx) {
                    Snackbar.make(mFlOuterWrapper, "JSON Parsing Error", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        try {
            mFacebookHelper.getProfile();
        } catch (RuntimeException rEx) {
            rEx.printStackTrace();
        }

    }

    @Override
    public void onFBSignOut() {
        UserPreference.deleteProfileInformation();
        Snackbar.make(mFlOuterWrapper, getString(R.string.msg_logout_success), Snackbar.LENGTH_SHORT).show();

    }

    @Override
    public void onInstagramSignInFail(String _errorMessage) {
        Snackbar.make(mFlOuterWrapper, _errorMessage, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onInstagramSignInSuccess(String _authToken, InstagramUser _instagramUser) {

        //No internet connection then return
        if (!Util.checkInternet(mContext)) {
            Snackbar.make(mFlOuterWrapper, getResources().getString(R.string.msg_internet_unavailable), Snackbar.LENGTH_SHORT).show();
            return;
        }
        //Prepare json object by getting required user info from GoogleUser object
        JSONObject user = new JSONObject();

        try {

            user.put("email", _instagramUser.getUserName());
            user.put("auth_token", _authToken);
            user.put("auth_type", "instagram");
            user.put("imeiNum", Util.getIEMINumber(mContext));
            user.put("deviceid", Util.getDeviceId(mContext));

            String name = _instagramUser.getFullName();

            if (TextUtils.isEmpty(name)) {
                name = "";
            }

            user.put("name", name);
            user.put("age", "");

            if (!TextUtils.isEmpty(_instagramUser.getProfilePicture())) {
                user.put("profile_pic", _instagramUser.getProfilePicture());
            } else {
                user.put("profile_pic", "");
            }

            user.put("address", "");
            user.put("phone", "0000000000");

            registerUser(mContext, user);

        } catch (JSONException jsonEx) {
            Snackbar.make(mFlOuterWrapper, "JSON Parsing Error", Snackbar.LENGTH_SHORT).show();
        }

    }

    private void initializeActivity(Context _context) {

        //Check internet connectivity
        if (Util.checkInternet(mContext)) {
            mFlNoInternet.setVisibility(View.GONE);
            mllLoginWrapper.setVisibility(View.VISIBLE);

            //mProgressDialog = Util.showProgressDialog(mCurrentActivity, null, getString(R.string.msg_please_wait), false);

            //Initialize social plugins
            initSocialPlugins();

            //Todo Goto dashboard if user already logged in
            String accessToken = UserPreference.getAccessToken ();
            if(!TextUtils.isEmpty (accessToken)){
                mUserRegistered = true;
                mRlOuterLoginWrapper.setVisibility (View.GONE);
            }else{
                mUserRegistered = false;
                mRlOuterLoginWrapper.setVisibility (View.VISIBLE);
            }

            if(mUserRegistered == true && mDataDownloaded == true){
                //Goto dashboard
                showDashboard();
            }

            if(mUserRegistered) {
                //Get data from server
                getProductsFromServer(_context);
            }

        } else {
            mFlNoInternet.setVisibility(View.VISIBLE);
            mllLoginWrapper.setVisibility(View.GONE);
        }
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
    }

    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();

            if (actionBar != null) {
                actionBar.show();
            }
        }
    };

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */

    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    /**
     * Register User using Social Login
     */
    private void registerUser(Context _context, final JSONObject _user) {
        //Start Progress dialog
        dismissDialog();

        mProgressDialog = Util.showProgressDialog(mCurrentActivity, null, getString(R.string.msg_registering_user), false);

        BaseModel<JSONObject> baseModel = new BaseModel<JSONObject>(_context) {
            @Override
            public void onSuccess(int _statusCode, Map<String, String> _headers, JSONObject _response) {
                //CLose Progress dialog
                dismissDialog();
                if (_response != null) {
                    try {
                        if (_response.getString("status").equalsIgnoreCase("1")) {
                            UserPreference.setAccessToken(_response.getString("token"));
                            UserPreference.setApiToken(_response.getString("token"));
                            if (_user.getString("auth_type").equalsIgnoreCase("google")) {
                                UserPreference.setLoginMethod(AppConstant.LOGIN_TYPE.GOOGLE.ordinal());

                            } else if (_user.getString("auth_type").equalsIgnoreCase("facebook")) {
                                UserPreference.setLoginMethod(AppConstant.LOGIN_TYPE.FACEBOOK.ordinal());

                            } else if (_user.getString("auth_type").equalsIgnoreCase("instagram")) {
                                UserPreference.setLoginMethod(AppConstant.LOGIN_TYPE.INSTAGRAM.ordinal());
                            } else {
                                UserPreference.setLoginMethod (AppConstant.LOGIN_TYPE.CUSTOM.ordinal ());
                            }

                            JSONObject customer = _response.getJSONObject("customer_details");

                            if (customer != null) {
                                UserPreference.setUserEmail(customer.getString("email"));
                                UserPreference.setUserFirstName(customer.getString("first_name"));
                                UserPreference.setUserLastName(customer.getString("last_name"));
                                UserPreference.setUserAddress1(customer.getString("add1"));
                                UserPreference.setUserAddress2(customer.getString("add2"));
                                UserPreference.setUserCity(customer.getString("city"));
                                UserPreference.setUserState(customer.getString("state"));
                                UserPreference.setUserCountry(customer.getString("country"));
                                UserPreference.setUserPin(customer.getString("pin"));
                                UserPreference.setUserPhone(customer.getString("phone"));
                                UserPreference.setProfilePic(customer.getString("profile_image"));
                            }

                            mUserRegistered = true;
                            //Redirect to Dashboard
                            if (mUserRegistered == true && mDataDownloaded == true) {
                                showDashboard();
                            } else {
                                getProductsFromServer(mContext);
                            }


                        } else {
                            Snackbar.make(mFlOuterWrapper, getString(R.string.msg_user_not_registered), Snackbar.LENGTH_SHORT).show();
                        }

                    } catch (JSONException jsEx) {
                        Snackbar.make(mFlOuterWrapper, jsEx.getMessage(), Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    Snackbar.make(mFlOuterWrapper, "No response from server", Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(ErrorData error) {
                //Close Progress dialog
                dismissDialog();
                if (error != null) {

                    switch (error.getErrorType()) {
                        case NETWORK_NOT_AVAILABLE:
                            Snackbar.make(mFlOuterWrapper, getResources().getString(R.string.msg_internet_unavailable), Snackbar.LENGTH_SHORT).show();
                            break;
                        case INTERNAL_SERVER_ERROR:
                            Snackbar.make(mFlOuterWrapper, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case CONNECTION_TIMEOUT:
                            Snackbar.make(mFlOuterWrapper, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case APPLICATION_ERROR:
                            Snackbar.make(mFlOuterWrapper, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case INVALID_INPUT_SUPPLIED:
                            Snackbar.make(mFlOuterWrapper, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case AUTHENTICATION_ERROR:
                            Snackbar.make(mFlOuterWrapper, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case UNAUTHORIZED_ERROR:
                            Snackbar.make(mFlOuterWrapper, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                    }
                }
            }
        };

        String url = QueryBuilder.getLoginUrl();
        if (_user != null) {
            baseModel.executePostJsonRequest(url, _user, TAG);
        } else {
            Snackbar.make(mFlOuterWrapper, getResources().getString(R.string.msg_reg_user_missing_input), Snackbar.LENGTH_SHORT).show();
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

    private void showDashboard() {
        Intent intent = new Intent(mContext, Dashboard.class);
        intent.setAction(AppConstant.ACTIONS.HOME.name());
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    /*private MaterialDialog showDialog(Context _context, String _title, String _content, boolean _cancellable,
                                      boolean _autoDismiss, String _positiveText, String _negativeText, Drawable _icon) {

        MaterialDialog.Builder builder = new MaterialDialog.Builder(_context)
                .title(_title)
                .content(_content)
                .cancelable(_cancellable)
                .autoDismiss(_autoDismiss)
                .positiveText(_positiveText)
                .negativeText(_negativeText);

        if (_icon != null) {
            builder.icon(_icon);
        }

        mDialog = builder.build();
        return mDialog;
    }*/

    private void getProductsFromServer(final Context _context) {

        mProgressDialog = Util.showProgressDialog(mCurrentActivity, null, getString(R.string.msg_fetching_product), false);

        BaseModel<JSONArray> baseModel = new BaseModel<JSONArray>(_context) {

            @Override
            public void onSuccess(int statusCode, Map<String, String> headers, JSONArray _response) {

                dismissDialog();

                if (_response != null & _response.length() > 0) {
                    try {

                        LocalDAO localDAO = new LocalDAO(_context);
                        localDAO.addProducts(_response, false);
                        mDataDownloaded = true;

                        //CLose Progress dialog
                        if (mUserRegistered == true && mDataDownloaded == true) {
                            showDashboard ();
                        }

                    } catch (Exception ex) {
                        Snackbar.make(mFlOuterWrapper, ex.getMessage(), Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    Snackbar.make(mFlOuterWrapper, "No response from server", Snackbar.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onError(ErrorData error) {
                //Close Progress dialog
                dismissDialog();

                if (error != null) {

                    switch (error.getErrorType()) {
                        case NETWORK_NOT_AVAILABLE:
                            Snackbar.make(mFlOuterWrapper, getResources().getString(R.string.msg_internet_unavailable), Snackbar.LENGTH_SHORT).show();
                            break;
                        case INTERNAL_SERVER_ERROR:
                            Snackbar.make(mFlOuterWrapper, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case CONNECTION_TIMEOUT:
                            Snackbar.make(mFlOuterWrapper, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case APPLICATION_ERROR:
                            Snackbar.make(mFlOuterWrapper, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case INVALID_INPUT_SUPPLIED:
                            Snackbar.make(mFlOuterWrapper, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case AUTHENTICATION_ERROR:
                            Snackbar.make(mFlOuterWrapper, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case UNAUTHORIZED_ERROR:
                            Snackbar.make(mFlOuterWrapper, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                    }
                }
            }
        };

        String url = QueryBuilder.getProducts();
        baseModel.executeGetJsonArrayRequest(url, TAG);

    }

    @Override
    public void onComplete(RippleView rippleView) {
        //No internet connection then return
        if (!Util.checkInternet(mContext)) {
            Snackbar.make(mFlOuterWrapper, getResources().getString(R.string.msg_internet_unavailable), Snackbar.LENGTH_SHORT).show();
            return;
        }

        int id = rippleView.getId();
        switch (id) {
            case R.id.btn_try_again:
                initializeActivity(mContext);
                break;

            case R.id.btn_fb:
                if (UserPreference.getAccessToken() != null) {
                    UserPreference.deleteProfileInformation();
                    signout();
                }

                mLoginMethod = AppConstant.LOGIN_TYPE.FACEBOOK.ordinal();
//                mFacebookHelper.performSignIn(this);
                temporaryLogin();
                break;

            case R.id.btn_google:
                if (UserPreference.getAccessToken() != null) {
                    UserPreference.deleteProfileInformation();
                    signout();
                }

                mLoginMethod = AppConstant.LOGIN_TYPE.GOOGLE.ordinal();
                mGoogleHelper.performSignIn(this);

                break;

            case R.id.btn_instagram:
                if (UserPreference.getAccessToken() != null) {
                    UserPreference.deleteProfileInformation();
                    signout();
                }

                mLoginMethod = AppConstant.LOGIN_TYPE.INSTAGRAM.ordinal();
                mInstagramHelper.performSignIn();

                break;

        }
    }
    /**
     * TODO Remove
     */
    private void temporaryLogin() {
        //Prepare json object by getting required user info from GoogleUser object
        JSONObject user = new JSONObject();

        try {

            user.put("email", "divye@mail.com");
            user.put("auth_token", "token");
            user.put("auth_type", "google");
            user.put("imeiNum", Util.getIEMINumber(mContext));
            user.put("deviceid", Util.getDeviceId(mContext));

            String name = "Supriya Naveen";

            if (TextUtils.isEmpty(name)) {
                name = "";
            }

            user.put("name", name);
            user.put("age", "");
            user.put("profile_pic", "");
            user.put("address", "Bengaluru");
            user.put("phone", "0000000000");
            user.put("password", "123456");

            registerUser(mContext, user);

        } catch (JSONException jsonEx) {
            Snackbar.make(mFlOuterWrapper, "JSON Parsing Error", Snackbar.LENGTH_SHORT).show();
        }
    }
}
