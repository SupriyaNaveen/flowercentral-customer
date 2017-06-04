package com.flowercentral.flowercentralcustomer.delivery;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.NavUtils;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.flowercentral.flowercentralcustomer.BaseActivity;
import com.flowercentral.flowercentralcustomer.R;

public class AddressActivity extends BaseActivity implements View.OnClickListener{

    private String mAction;
    private Toolbar mToolbar;
    private TextInputEditText mCustomerName;
    private TextInputEditText mCustomerAddress;
    private TextInputEditText mCustomerCity;
    private TextInputEditText mCustomerState;
    private TextInputEditText mCustomerZip;
    private TextInputEditText mCustomerPhone;

    private TextView mErrCustomerName;
    private TextView mErrCustomerAddress;
    private TextView mErrCustomerCity;
    private TextView mErrCustomerState;
    private TextView mErrCustomerZip;
    private TextView mErrCustomerPhone;

    private ImageView mPickLocation;
    private Button mRegister;

    private boolean boolFieldHasError;



    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_address);

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
    public void onBackPressed () {
        super.onBackPressed ();
        finish ();
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
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
    protected void onDestroy () {
        super.onDestroy ();
    }

    private void initializeView () {

        boolFieldHasError = true;

        mToolbar = (Toolbar) findViewById (R.id.toolbar);

        mCustomerName = (TextInputEditText) findViewById (R.id.edt_customer_name);
        mCustomerAddress = (TextInputEditText) findViewById (R.id.edt_customer_address);
        mCustomerCity = (TextInputEditText) findViewById (R.id.edt_city);
        mCustomerState = (TextInputEditText) findViewById (R.id.edt_state);
        mCustomerZip = (TextInputEditText) findViewById (R.id.edt_zip);
        mCustomerPhone = (TextInputEditText) findViewById (R.id.edt_phone);

        mErrCustomerName = (TextView) findViewById (R.id.txt_err_customer_name);
        mErrCustomerAddress = (TextView) findViewById (R.id.txt_err_customer_addr);
        mErrCustomerCity = (TextView) findViewById (R.id.txt_err_city);
        mErrCustomerState = (TextView) findViewById (R.id.txt_err_state);
        mErrCustomerZip = (TextView) findViewById (R.id.txt_err_zip);
        mErrCustomerPhone = (TextView) findViewById (R.id.txt_err_phone);

        mPickLocation = (ImageView) findViewById (R.id.img_view_locate);
        mRegister = (Button) findViewById (R.id.btn_register);

        //Setup toolbar
        setSupportActionBar (mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString (R.string.title_activity_address));

        //Setup click listener
        mPickLocation.setOnClickListener (this);
        mRegister.setOnClickListener (this);

        //Setup Text watcher for validation
        mCustomerName.addTextChangedListener (customerNameChange);
        mCustomerAddress.addTextChangedListener (customerAddressChange);
        mCustomerCity.addTextChangedListener (customerCityChange);
        mCustomerState.addTextChangedListener (customerStateChange);
        mCustomerZip.addTextChangedListener (customerZipChange);
        mCustomerPhone.addTextChangedListener (customerPhoneChange);

    }


    @Override
    public void onClick (View v) {
        int id = v.getId ();
        switch (id){
            case R.id.btn_register:
                /**Todo
                 * Submit order details to the server
                 * Get order id
                 * Initiate Payment process
                 */


                break;
            case R.id.img_view_locate:

                break;
        }
    }

    private TextWatcher customerNameChange = new TextWatcher(){

        @Override
        public void beforeTextChanged (CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged (CharSequence s, int start, int before, int count) {
            mErrCustomerName.setText(null);
        }

        @Override
        public void afterTextChanged (Editable s) {
            if(TextUtils.isEmpty (s)){
                mErrCustomerName.setText (getString (R.string.err_empty_text));
                boolFieldHasError = true;
            }else{
                mErrCustomerName.setText(null);
                boolFieldHasError = false;
            }
        }
    };

    private TextWatcher customerAddressChange = new TextWatcher(){

        @Override
        public void beforeTextChanged (CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged (CharSequence s, int start, int before, int count) {
            mErrCustomerAddress.setText(null);

        }

        @Override
        public void afterTextChanged (Editable s) {
            if(TextUtils.isEmpty (s)){
                mErrCustomerAddress.setText (getString (R.string.err_empty_text));
                boolFieldHasError = true;
            }else{
                mErrCustomerAddress.setText(null);
                boolFieldHasError = false;
            }
        }
    };

    private TextWatcher customerCityChange = new TextWatcher(){

        @Override
        public void beforeTextChanged (CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged (CharSequence s, int start, int before, int count) {
            mErrCustomerCity.setText(null);
        }

        @Override
        public void afterTextChanged (Editable s) {
            if(TextUtils.isEmpty (s)){
                mErrCustomerCity.setText (getString (R.string.err_empty_text));
                boolFieldHasError = true;
            }else{
                mErrCustomerCity.setText(null);
                boolFieldHasError = false;
            }
        }
    };

    private TextWatcher customerStateChange = new TextWatcher(){

        @Override
        public void beforeTextChanged (CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged (CharSequence s, int start, int before, int count) {
            mErrCustomerState.setText(null);
        }

        @Override
        public void afterTextChanged (Editable s) {
            if(TextUtils.isEmpty (s)){
                mErrCustomerState.setText (getString (R.string.err_empty_text));
                boolFieldHasError = true;
            }else{
                mErrCustomerState.setText(null);
                boolFieldHasError = false;
            }
        }
    };

    private TextWatcher customerZipChange = new TextWatcher(){

        @Override
        public void beforeTextChanged (CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged (CharSequence s, int start, int before, int count) {
            mErrCustomerZip.setText(null);
        }

        @Override
        public void afterTextChanged (Editable s) {
            if(TextUtils.isEmpty (s)){
                mErrCustomerZip.setText (getString (R.string.err_empty_text));
                boolFieldHasError = true;
            }else{
                mErrCustomerZip.setText(null);
                boolFieldHasError = false;
            }
        }
    };

    private TextWatcher customerPhoneChange = new TextWatcher(){

        @Override
        public void beforeTextChanged (CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged (CharSequence s, int start, int before, int count) {
            mErrCustomerPhone.setText(null);
        }

        @Override
        public void afterTextChanged (Editable s) {
            if(TextUtils.isEmpty (s)){
                mErrCustomerPhone.setText (getString (R.string.err_empty_text));
                boolFieldHasError = true;
            }else{
                mErrCustomerPhone.setText(null);
                boolFieldHasError = false;
            }
        }
    };


}
