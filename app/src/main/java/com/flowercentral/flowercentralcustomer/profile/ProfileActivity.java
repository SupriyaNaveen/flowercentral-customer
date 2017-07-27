package com.flowercentral.flowercentralcustomer.profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.NavUtils;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.andexert.library.RippleView;
import com.flowercentral.flowercentralcustomer.BaseActivity;
import com.flowercentral.flowercentralcustomer.R;
import com.flowercentral.flowercentralcustomer.preference.UserPreference;
import com.flowercentral.flowercentralcustomer.rest.BaseModel;
import com.flowercentral.flowercentralcustomer.rest.QueryBuilder;
import com.flowercentral.flowercentralcustomer.util.Util;
import com.flowercentral.flowercentralcustomer.volley.ErrorData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class ProfileActivity extends BaseActivity {

    private static final String TAG = ProfileActivity.class.getSimpleName();
    private String mAction;
    private Toolbar mToolbar;
    private CollapsingToolbarLayout mCollapsingToolbar;
    private TextInputEditText mEditTextFirstName;
    private TextInputEditText mEditTextLastName;
    private TextInputEditText mEditTextMobile;
    private RadioGroup mRadioGroupGender;
    private RadioButton mRadioBtnFemale;
    private RadioButton mRadioBtnMale;
    private RippleView mBtnUpdate;
    private RippleView mBtnCancel;
    private MaterialDialog mProgressDialog;
    private CoordinatorLayout mRootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mContext = this;

        //Get data from Intent
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                mAction = bundle.getString("action");
            }
        }

        //Initialize view elements
        initializeView();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
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

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        //Setup toolbar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setFlower(getString (R.string.title_activity_cart));

        initCollapsingToolbar();
        initProfileDate();

    }

    private void initProfileDate() {

        TextView titleUserName = (TextView) findViewById(R.id.title_user_name);

        titleUserName.setText(UserPreference.getUserFirstName());

        TextView titleEmail = (TextView) findViewById(R.id.title_email);
        titleEmail.setText(UserPreference.getUserEmail());

        TextView email = (TextView) findViewById(R.id.txt_user_email);
        email.setText(UserPreference.getUserEmail());

        mRootLayout = (CoordinatorLayout) findViewById(R.id.main_content);

        mEditTextFirstName = (TextInputEditText) findViewById(R.id.edt_user_first_name);
        mEditTextLastName = (TextInputEditText) findViewById(R.id.edt_user_last_name);
        mEditTextMobile = (TextInputEditText) findViewById(R.id.edt_user_mobile);

        mRadioGroupGender = (RadioGroup) findViewById(R.id.gender_radio_group);
        mRadioBtnMale = (RadioButton) findViewById(R.id.male_gender_btn);
        mRadioBtnFemale = (RadioButton) findViewById(R.id.female_gender_button);

        mBtnUpdate = (RippleView) findViewById(R.id.btn_update);
        mBtnCancel = (RippleView) findViewById(R.id.btn_cancel);

        mBtnUpdate.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView v) {
                profileUpdate();
            }
        });

        mBtnCancel.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView v) {

            }
        });

        mRadioGroupGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                mRadioBtnFemale.setError(null);
            }
        });

        mEditTextFirstName.setText(UserPreference.getUserFirstName());
        mEditTextLastName.setText(UserPreference.getUserLastName());
        mEditTextMobile.setText(UserPreference.getUserPhone());
        switch (UserPreference.getLoggedInUserGender()) {
            case M:
                mRadioBtnMale.setChecked(true);
                break;
            case F:
                mRadioBtnFemale.setChecked(true);
                break;
            case O:
                break;
        }
    }

    private void profileUpdate() {
        if (isValidInput()) {
            JSONObject user = new JSONObject();
            try {
                user.put(getString(R.string.api_key_firstname), mEditTextFirstName.getText());
                user.put(getString(R.string.api_key_lastname), mEditTextLastName.getText());

                switch (mRadioGroupGender.getCheckedRadioButtonId()) {
                    case R.id.male_gender_btn:
                        user.put(getString(R.string.api_key_gender), UserPreference.GENDER.M);
                        break;
                    case R.id.female_gender_button:
                        user.put(getString(R.string.api_key_gender), UserPreference.GENDER.F);
                        break;
                }
            } catch (JSONException e) {

            }
            //Start Progress dialog
            dismissDialog();

            mProgressDialog = Util.showProgressDialog(this, null, getString(R.string.msg_registering_user), false);

            BaseModel<JSONObject> baseModel = new BaseModel<JSONObject>(this) {
                @Override
                public void onSuccess(int statusCode, Map<String, String> headers, JSONObject response) {
                    ProfileData profileData = new Gson().fromJson(String.valueOf(response),
                            new TypeToken<ProfileData>() {
                            }.getType());
                    UserPreference.setProfileInformation(profileData);
                    //CLose Progress dialog
                    dismissDialog();
                }

                @Override
                public void onError(ErrorData error) {
                    //Close Progress dialog
                    dismissDialog();

                    if (error != null) {
                        error.setErrorMessage("Profile update failed. Cause -> " + error.getErrorMessage());

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

            String url = QueryBuilder.getProfileUpdateUrl();
            if (user != null) {
                baseModel.executePostJsonRequest(url, user, TAG);
            } else {
                Snackbar.make(mRootLayout, getResources().getString(R.string.msg_reg_user_missing_input), Snackbar.LENGTH_SHORT).show();
            }
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

        if (mEditTextFirstName.getText().toString().isEmpty()) {
            mEditTextFirstName.setError(getString(R.string.err_empty_text));
            isValid = false;
        } else {
            mEditTextFirstName.setError(null);
        }

        if (mEditTextLastName.getText().toString().isEmpty()) {
            mEditTextLastName.setError(getString(R.string.err_empty_text));
            isValid = false;
        } else {
            mEditTextLastName.setError(null);
        }

        if (mEditTextMobile.getText().toString().isEmpty()) {
            mEditTextMobile.setError(getString(R.string.err_empty_text));
            isValid = false;
        } else {
            mEditTextMobile.setError(null);
        }

        if (mRadioGroupGender.getCheckedRadioButtonId() == -1) {
            mRadioBtnFemale.setError(getString(R.string.err_empty_text));
            isValid = false;
        } else {
            mRadioBtnFemale.setError(null);
        }

        return isValid;
    }

    /**
     * Initializing collapsing toolbar
     * Will show and hide the toolbar title on scroll
     */
    private void initCollapsingToolbar() {
        mCollapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        mCollapsingToolbar.setTitle(" ");
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    mCollapsingToolbar.setTitle(UserPreference.getUserFirstName());
                    isShow = true;
                } else if (isShow) {
                    mCollapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });

        dynamicToolbar();
        setToolbarTextAppearence();
    }

    private void setToolbarTextAppearence() {
        mCollapsingToolbar.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);
        mCollapsingToolbar.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
    }

    private void dynamicToolbar() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.background_pattern_03);
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                //mCollapsingToolbar.setContentScrimColor (palette.getMutedColor (getResources ().getColor (R.color.colorPrimary)));
                //mCollapsingToolbar.setStatusBarScrimColor (palette.getMutedColor (getResources ().getColor (R.color.colorPrimaryDark)));

                mCollapsingToolbar.setContentScrimColor(getResources().getColor(R.color.colorPrimary));
                mCollapsingToolbar.setStatusBarScrimColor(getResources().getColor(R.color.colorPrimaryDark));

            }
        });
    }
}
