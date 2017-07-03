package com.flowercentral.flowercentralcustomer.util;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.flowercentral.flowercentralcustomer.BaseActivity;
import com.flowercentral.flowercentralcustomer.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MapActivity extends BaseActivity implements OnMapReadyCallback {

    private GoogleMap mGoogleMap;
    private double mLatitude;
    private double mLongitude;
    private String mAddress;
    private boolean mIsDraggable;

    @BindView(R.id.root_layout)
    RelativeLayout mRelativeLayoutRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);

        ButterKnife.bind(this);

        //Setup toolbar
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (null != getSupportActionBar())
            getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_activity_address));

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mLatitude = getIntent().getDoubleExtra(getString(R.string.key_latitude), 0.0);
        mLongitude = getIntent().getDoubleExtra(getString(R.string.key_longitude), 0.0);
        mAddress = getIntent().getStringExtra(getString(R.string.key_address));
        mIsDraggable = getIntent().getBooleanExtra(getString(R.string.key_is_draggable), false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        switch (id) {
            case android.R.id.home:
                if (getCallingActivity() != null) {
                    Intent data = new Intent();
                    data.putExtra(getString(R.string.key_latitude), mLatitude);
                    data.putExtra(getString(R.string.key_longitude), mLongitude);
                    setResult(RESULT_OK, data);
                }
                NavUtils.navigateUpFromSameTask(this);
                break;
        }
        return super.onOptionsItemSelected(item);

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

        if (mIsDraggable) {
            mGoogleMap.addMarker(new MarkerOptions()
                    .title(getCityName()).visible(true)
                    .draggable(true)
                    .position(location)).showInfoWindow();
        } else {
            mGoogleMap.addMarker(new MarkerOptions()
                    .title(getCityName()).visible(true)
                    .position(location)).showInfoWindow();
        }

        mGoogleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                LatLng location = marker.getPosition();
                mLatitude = location.latitude;
                mLongitude = location.longitude;
                marker.setTitle(getCityName());
                marker.showInfoWindow();
            }
        });
    }

    private String getCityName() {
        Geocoder geocoder = new Geocoder(MapActivity.this, Locale.getDefault());
        List<Address> addresses;
        String cityName = mAddress;
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
        Snackbar snackbar = Snackbar.make(mRelativeLayoutRoot, getResources().getString(R.string
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
        Snackbar snackbar = Snackbar.make(mRelativeLayoutRoot, getResources()
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

    @Override
    public void onBackPressed() {
        if (getCallingActivity() != null) {
            Intent data = new Intent();
            data.putExtra(getString(R.string.key_latitude), mLatitude);
            data.putExtra(getString(R.string.key_longitude), mLongitude);
            setResult(RESULT_OK, data);
        }
        super.onBackPressed();
    }
}


