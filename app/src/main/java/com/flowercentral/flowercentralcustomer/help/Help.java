package com.flowercentral.flowercentralcustomer.help;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.flowercentral.flowercentralcustomer.BaseActivity;
import com.flowercentral.flowercentralcustomer.R;
import com.flowercentral.flowercentralcustomer.rest.BaseModel;
import com.flowercentral.flowercentralcustomer.rest.QueryBuilder;
import com.flowercentral.flowercentralcustomer.volley.ErrorData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.util.List;
import java.util.Map;

public class Help extends BaseActivity {

    private static final String TAG = Help.class.getSimpleName();
    private RelativeLayout rootLayout;
    private ExpandableListView mHelpListView;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        mContext = this;

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
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initializeView() {
        rootLayout = (RelativeLayout) findViewById(R.id.ll_help_inner_wrapper);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mHelpListView = (ExpandableListView) findViewById(R.id.help_list);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar); 

        //Setup toolbar
        setSupportActionBar(mToolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.title_activity_help));
        }
        getHelpData();
    }

    private void getHelpData() {
        mProgressBar.setVisibility(View.VISIBLE);

        BaseModel<JSONArray> baseModel = new BaseModel<JSONArray>(this) {
            @Override
            public void onSuccess(int statusCode, Map<String, String> headers, JSONArray response) {
                List<HelpDetails> helpDetailsArrayList = new Gson().fromJson(String.valueOf(response),
                        new TypeToken<List<HelpDetails>>() {
                        }.getType());
                mProgressBar.setVisibility(View.GONE);

                ExpandableListAdapter expandableListAdapter = new ExpandableListAdapter(Help.this, helpDetailsArrayList);
                mHelpListView.setAdapter(expandableListAdapter);
            }

            @Override
            public void onError(ErrorData error) {
                mProgressBar.setVisibility(View.GONE);

                if (error != null) {
                    error.setErrorMessage("Help details fetch failed. Cause -> " + error.getErrorMessage());

                    switch (error.getErrorType()) {
                        case NETWORK_NOT_AVAILABLE:
                            Snackbar.make(rootLayout, getResources().getString(R.string.msg_internet_unavailable), Snackbar.LENGTH_SHORT).show();
                            break;
                        case INTERNAL_SERVER_ERROR:
                            Snackbar.make(rootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case CONNECTION_TIMEOUT:
                            Snackbar.make(rootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case APPLICATION_ERROR:
                            Snackbar.make(rootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case INVALID_INPUT_SUPPLIED:
                            Snackbar.make(rootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case AUTHENTICATION_ERROR:
                            Snackbar.make(rootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case UNAUTHORIZED_ERROR:
                            Snackbar.make(rootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        default:
                            Snackbar.make(rootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                    }
                }
            }
        };

        String url = QueryBuilder.getHelpDetailsUrl();
        baseModel.executeGetJsonArrayRequest(url, TAG);
    }
}
