package com.flowercentral.flowercentralcustomer.common.component;

import android.app.Activity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.Toolbar.LayoutParams;
import android.view.View;

import com.flowercentral.flowercentralcustomer.R;


/**
 * Created by Ashish Upadhyay on 5/17/17.
 */

public class NavigationDrawerToggle implements DrawerLayout.DrawerListener {

    private DrawerLayout mDrawerLayout;
    private DrawerArrowDrawable mArrowDrawable;
    private AppCompatImageButton mToggleButton;
    private String mOpenDrawerContentDesc;
    private String mCloseDrawerContentDesc;

    public NavigationDrawerToggle(Activity _activity, DrawerLayout _drawerLayout, Toolbar _toolbar,
                                  String _openDrawerContentDescRes, String _closeDrawerContentDescRes){

        this.mDrawerLayout = _drawerLayout;
        this.mOpenDrawerContentDesc = _openDrawerContentDescRes;
        this.mCloseDrawerContentDesc = _closeDrawerContentDescRes;

        mArrowDrawable = new DrawerArrowDrawable(_toolbar.getContext());
        mArrowDrawable.setDirection(DrawerArrowDrawable.ARROW_DIRECTION_END);

        mToggleButton = new AppCompatImageButton(_toolbar.getContext(), null,
                R.attr.toolbarNavigationButtonStyle);
        _toolbar.addView(mToggleButton, new LayoutParams (GravityCompat.END));
        mToggleButton.setImageDrawable(mArrowDrawable);
        mToggleButton.setOnClickListener(new View.OnClickListener () {
                @Override
                public void onClick(View v) {
                    toggle();
                }
            }
        );


    }

    public void syncState() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
            setPosition(1f);
        }
        else {
            setPosition(0f);
        }
    }

    public void toggle() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
            mDrawerLayout.closeDrawer(GravityCompat.END);
        }
        else {
            mDrawerLayout.openDrawer(GravityCompat.END);
        }
    }

    public void setPosition(float position) {
        if (position == 1f) {
            mArrowDrawable.setVerticalMirror(true);
            mToggleButton.setContentDescription(mCloseDrawerContentDesc);
        }
        else if (position == 0f) {
            mArrowDrawable.setVerticalMirror(false);
            mToggleButton.setContentDescription(mOpenDrawerContentDesc);
        }
        mArrowDrawable.setProgress(position);
    }


    @Override
    public void onDrawerSlide (View drawerView, float slideOffset) {
        setPosition(Math.min(1f, Math.max(0, slideOffset)));

    }

    @Override
    public void onDrawerOpened (View drawerView) {
        setPosition(1f);

    }

    @Override
    public void onDrawerClosed (View drawerView) {
        setPosition(0f);

    }

    @Override
    public void onDrawerStateChanged (int newState) {

    }


}
