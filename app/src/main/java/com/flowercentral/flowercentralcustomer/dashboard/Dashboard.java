package com.flowercentral.flowercentralcustomer.dashboard;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.flowercentral.flowercentralcustomer.R;

public class Dashboard extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar mToolbar;
    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle mToggle;
    private NavigationView mNavigationViewLeft;
    private NavigationView mNavigationViewRight;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_dashboard);

        mToolbar = (Toolbar) findViewById (R.id.toolbar);
        mDrawer = (DrawerLayout) findViewById (R.id.drawer_layout);
        mNavigationViewLeft = (NavigationView) findViewById (R.id.nav_view_left);
        mNavigationViewRight = (NavigationView) findViewById (R.id.nav_view_right);

        setSupportActionBar (mToolbar);

        mToggle = new ActionBarDrawerToggle (
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.setDrawerListener (mToggle);
        mToggle.syncState ();

        mNavigationViewLeft.setNavigationItemSelectedListener (this);
    }

    @Override
    public void onBackPressed () {
        DrawerLayout drawer = (DrawerLayout) findViewById (R.id.drawer_layout);
        if (drawer.isDrawerOpen (GravityCompat.START)) {
            drawer.closeDrawer (GravityCompat.START);
        } else {
            super.onBackPressed ();
        }
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater ().inflate (R.menu.dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId ();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected (item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected (MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId ();

        if (id == R.id.nav_camera1) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery1) {

        } else if (id == R.id.nav_slideshow1) {

        } else if (id == R.id.nav_manage1) {

        } else if (id == R.id.nav_share1) {

        } else if (id == R.id.nav_send1) {

        }

        mDrawer.closeDrawer (GravityCompat.START);
        return true;
    }
}
