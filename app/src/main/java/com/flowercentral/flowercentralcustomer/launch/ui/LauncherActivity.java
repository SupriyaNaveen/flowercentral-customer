package com.flowercentral.flowercentralcustomer.launch.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.binarysoft.sociallogin.facebook.FacebookGraphListner;
import com.binarysoft.sociallogin.facebook.FacebookUser;
import com.binarysoft.sociallogin.google.GoogleUser;
import com.binarysoft.sociallogin.instagram.InstagramUser;
import com.flowercentral.flowercentralcustomer.BaseActivity;
import com.flowercentral.flowercentralcustomer.R;
import com.flowercentral.flowercentralcustomer.preference.UserPreference;
import com.flowercentral.flowercentralcustomer.setting.AppConstant;
import com.flowercentral.flowercentralcustomer.util.Util;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class LauncherActivity extends BaseActivity implements View.OnClickListener{

    private final String TAG = LauncherActivity.class.getSimpleName ();
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
    private final Handler mHideHandler = new Handler ();
    private boolean mVisible;

    //Bind UI View Controls
    private Button mBtnTryAgain;

    private FrameLayout mFlNoInternet;

    private TextView mBtnFacebook;
    private TextView mBtnGoogle;
    private TextView mBtnInstagram;

    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate (savedInstanceState);

        setContentView (R.layout.activity_launcher);
        mContext = this;
        mVisible = true;

        mBtnTryAgain = (Button) findViewById (R.id.btn_try_again);
        mBtnFacebook = (TextView) findViewById (R.id.btn_fb);
        mBtnGoogle = (TextView) findViewById (R.id.btn_google);
        mBtnInstagram = (TextView) findViewById (R.id.btn_instagram);

        mFlNoInternet = (FrameLayout) findViewById (R.id.fl_no_internet);

        mBtnTryAgain.setOnClickListener (this);
        mBtnFacebook.setOnClickListener (this);
        mBtnGoogle.setOnClickListener (this);
        mBtnInstagram.setOnClickListener (this);

        initializeActivity(mContext);

    }

    @Override
    protected void onPostCreate (Bundle savedInstanceState) {
        super.onPostCreate (savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide (100);
    }

    @Override
    public void onClick (View v) {
        int id = v.getId ();
        switch (id){
            case R.id.btn_try_again:
                initializeActivity (mContext);
                break;
            case R.id.btn_fb:
                if(UserPreference.getAccessToken () != null){
                    UserPreference.deleteProfileInformation ();
                }

                mLoginMethod = AppConstant.LOGIN_TYPE.FACEBOOK.ordinal ();
                mFacebookHelper.performSignIn(this);

                break;

            case R.id.btn_google:
                if(UserPreference.getAccessToken () != null){
                    UserPreference.deleteProfileInformation ();
                }

                mLoginMethod = AppConstant.LOGIN_TYPE.GOOGLE.ordinal ();
                mGoogleHelper.performSignIn (this);

                break;

            case R.id.btn_instagram:
                if(UserPreference.getAccessToken () != null){
                    UserPreference.deleteProfileInformation ();
                }

                mLoginMethod = AppConstant.LOGIN_TYPE.INSTAGRAM.ordinal ();
                mInstagramHelper.performSignIn ();

                break;

        }
    }

    @Override
    public void onGoogleAuthSignIn (String authToken, GoogleUser _userAccount) {
        //String msg = String.format(Locale.US, "User id:%s\n\nAuthToken:%s", userId, authToken);
        //Toast.makeText (mContext, msg, Toast.LENGTH_SHORT).show ();
    }

    @Override
    public void onGoogleAuthSignInFailed (String errorMessage) {
        Toast.makeText (mContext, errorMessage, Toast.LENGTH_SHORT).show ();
    }

    @Override
    public void onGoogleAuthSignOut () {
        Toast.makeText (mContext, "Signed out of Google Plus", Toast.LENGTH_SHORT).show ();
    }

    @Override
    public void onFbSignInFail (String errorMessage) {
        Toast.makeText (mContext, errorMessage, Toast.LENGTH_SHORT).show ();
    }

    @Override
    public void onFbSignInSuccess (String authToken, String userId) {
        String msg = String.format(Locale.US, "User id:%s\n\nAuthToken:%s", userId, authToken);

        //Must implement this interface before calling get Profile
        mFacebookHelper.registerProfileListener (new FacebookGraphListner () {
            @Override
            public void onProfileReceived (FacebookUser _profile) {
                Log.i(TAG, _profile.toString ());
            }
        });

        try{
            mFacebookHelper.getProfile ();

        }catch (RuntimeException rEx){
            rEx.printStackTrace ();
        }

    }

    @Override
    public void onFBSignOut () {
        UserPreference.deleteProfileInformation ();
        Toast.makeText (mContext, "Signed out of Facebook", Toast.LENGTH_SHORT).show ();
    }

    @Override
    public void onInstagramSignInFail (String _errorMessage) {

    }

    @Override
    public void onInstagramSignInSuccess (String _authToken, InstagramUser _instagramUser) {

    }

    private void initializeActivity (Context _context) {

        //Check internet connectivity
        if(Util.checkInternet (mContext)){
            mFlNoInternet.setVisibility (View.GONE);
            //Initialize social plugins
            initSocialPlugins();

        }else{
            mFlNoInternet.setVisibility (View.VISIBLE);
        }
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide (int delayMillis) {
        mHideHandler.removeCallbacks (mHideRunnable);
        mHideHandler.postDelayed (mHideRunnable, delayMillis);
    }

    private final Runnable mHideRunnable = new Runnable () {
        @Override
        public void run () {
            hide ();
        }
    };

    private void hide () {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar ();
        if (actionBar != null) {
            actionBar.hide ();
        }
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks (mShowPart2Runnable);
    }

    private final Runnable mShowPart2Runnable = new Runnable () {
        @Override
        public void run () {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar ();

            if (actionBar != null) {
                actionBar.show ();
            }
        }
    };

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */

    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener () {
        @Override
        public boolean onTouch (View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide (AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

}
