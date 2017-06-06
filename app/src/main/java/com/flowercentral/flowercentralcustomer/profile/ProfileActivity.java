package com.flowercentral.flowercentralcustomer.profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.flowercentral.flowercentralcustomer.BaseActivity;
import com.flowercentral.flowercentralcustomer.R;

public class ProfileActivity extends BaseActivity {

    private String mAction;
    private Toolbar mToolbar;
    private CollapsingToolbarLayout mCollapsingToolbar;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_profile);

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
        //getSupportActionBar().setTitle(getString (R.string.title_activity_cart));

        initCollapsingToolbar();

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
                    mCollapsingToolbar.setTitle("Ashish Upadhyay");
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

    private void setToolbarTextAppearence(){
        mCollapsingToolbar.setCollapsedTitleTextAppearance (R.style.CollapsedAppBar);
        mCollapsingToolbar.setExpandedTitleTextAppearance (R.style.ExpandedAppBar);
    }

    private void dynamicToolbar(){
        Bitmap bitmap = BitmapFactory.decodeResource (getResources (), R.drawable.background_pattern_03);
        Palette.from (bitmap).generate (new Palette.PaletteAsyncListener () {
            @Override
            public void onGenerated (Palette palette) {
                //mCollapsingToolbar.setContentScrimColor (palette.getMutedColor (getResources ().getColor (R.color.colorPrimary)));
                //mCollapsingToolbar.setStatusBarScrimColor (palette.getMutedColor (getResources ().getColor (R.color.colorPrimaryDark)));

                mCollapsingToolbar.setContentScrimColor (getResources ().getColor (R.color.colorPrimary));
                mCollapsingToolbar.setStatusBarScrimColor (getResources ().getColor (R.color.colorPrimaryDark));

            }
        });
    }
}
