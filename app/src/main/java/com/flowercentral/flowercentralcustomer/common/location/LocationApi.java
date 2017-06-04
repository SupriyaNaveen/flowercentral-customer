package com.flowercentral.flowercentralcustomer.common.location;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.flowercentral.flowercentralcustomer.common.location.service.FetchAddressIntentService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Ashish Upadhyay on 1/28/2016.
 */
public class LocationApi implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = LocationApi.class.getSimpleName();

    // If GPS is enabled. Use minimal connected satellites count.
    private static final int min_gps_sat_count = 5;

    //Iteration step time.
    private static final int iteration_timeout_step = 500;

    private static final int GPS_LOCATION_UPDATES = 0;
    private static final int NETWORK_LOCATION_UPDATES = 1;
    private static final int DEFAULT_LOCATION_UPDATES = 2;  // let the device choose best location provider
    private final int TIME_OUT = 15;
    private final int GPS_TIME_OUT = 12;

    private Context mContext;

    private MyLocationDataListener locationResult;
    private Location bestLocation = null;
    private Handler handler = null;
    private LocationManager myLocationManager;
    private GoogleApiClient mGoogleApiClient;
    private List<String> activeProviders = null;

    private boolean isActive = false;
    private boolean gps_provider_enabled = false;
    private boolean network_provider_enabled = false;
    private int counts = 0;
    private int sat_count = 0;

    private boolean gpsStopped = false;
    private boolean findLocality;
    private boolean isCanceled;
    private Location mLocation;
    private ProgressDialog progressBar;


    @Override
    public void onConnected(Bundle bundle) {
        try{
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                Log.d(TAG, "onConnected :: last location from google api is found");
                locationFound(mLastLocation);
                mGoogleApiClient.disconnect();

            }else{
                if(locationResult!=null){
                    if(!isCanceled){
                        locationResult.locationNotAvailable();
                    }
                }
            }
        }catch (SecurityException sqEx){
            Log.e (TAG, sqEx.getMessage ());
            if(locationResult != null){
                locationResult.onPermissionRequired ();
            }

        }catch (Exception ex){
            ex.printStackTrace ();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG,"onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG,"onConnectionFailed");
        if(locationResult!=null){
            if(!isCanceled){
                locationResult.locationNotAvailable();
            }
        }
    }

    public LocationApi (Context _ctx) {
        mContext = _ctx;
    }

    /**
     * Initialize starting values and starting best location listeners
     *
     */
    public boolean init(Context ctx, boolean isSearchLocality, MyLocationDataListener result) {
        boolean initialized = false;
        gpsStopped = false;
        isActive = false;
        findLocality = isSearchLocality;
        mContext = ctx;
        locationResult = result;

        myLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        gps_provider_enabled = (Boolean) myLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        network_provider_enabled = (Boolean) myLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        bestLocation = null;
        counts = 0;

        activeProviders = myLocationManager.getProviders(true);
        activeProviders.remove("passive"); // as we do not want to use passive provider

        if (activeProviders == null || activeProviders.size() < 1) {
            initialized = false;
        } else {
            initialized = true;
        }

        // turning on location updates
        // myLocationManager.requestloc
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            myLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, networkLocationListener);
            myLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, gpsLocationListener);
            myLocationManager.addGpsStatusListener(gpsStatusListener);
            initialized = true;
        } else {
            initialized = false;
            throw new SecurityException();
        }

        // starting best location finder loop
        if (handler == null) {
            handler = new Handler();
        }
        handler.postDelayed(findBestLocation, iteration_timeout_step);
        showProgressbar (mContext, null, false, true);
        return initialized;
    }

    protected boolean isGPSProviderEnabled() {
        if(myLocationManager == null){
            myLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        }

        gps_provider_enabled = (Boolean) myLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return gps_provider_enabled;
    }

    protected boolean isNetworkProviderEnabled() {
        if(myLocationManager == null){
            myLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        }

        network_provider_enabled = (Boolean) myLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        return network_provider_enabled;
    }

    /**
     * GpsStatus listener. OnChainged counts connected satellites count.
     */
    public final GpsStatus.Listener gpsStatusListener = new GpsStatus.Listener() {
        public void onGpsStatusChanged(int event) {
        if (event == GpsStatus.GPS_EVENT_SATELLITE_STATUS) {
            try {
                // Check number of satellites in list to determine fix state
                GpsStatus status = myLocationManager.getGpsStatus(null);
                Iterable<GpsSatellite> satellites = status.getSatellites();

                sat_count = 0;

                Iterator<GpsSatellite> satI = satellites.iterator();
                while (satI.hasNext()) {
                    GpsSatellite satellite = satI.next();
                    sat_count++;
                }
            }catch (SecurityException sqEx){
                sqEx.printStackTrace ();
                if(locationResult != null){
                    locationResult.onPermissionRequired ();
                }

            } catch (Exception e) {
                e.printStackTrace();
                sat_count = min_gps_sat_count + 1;
            }

        }
        }
    };


    /**
     * Gps location listener.
     */
    public final LocationListener gpsLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            isActive = true;
            Log.d(TAG, "gpsLocationListener :: GPS Location refreshed");
        }

        public void onProviderDisabled(String provider) {

        }

        public void onProviderEnabled(String provider) {

        }

        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    };

    /**
     * Network location listener.
     */
    public final LocationListener networkLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            isActive = true;
            Log.d(TAG, "networkLocationListener :: Network Location refreshed");
        }

        public void onProviderDisabled(String provider) {

        }

        public void onProviderEnabled(String provider) {

        }

        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    };

    private Runnable findBestLocation = new Runnable() {
        @Override
        public void run() {
            counts++;

            //if time out 5 second, stop considering GPS
            if (counts > GPS_TIME_OUT && !gpsStopped) {
                //update last best location
                Log.d(TAG, "GPS time out..stoping gps updates");
                if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    throw new SecurityException();
                }
                myLocationManager.removeUpdates(gpsLocationListener);
                Log.d(TAG, "Stopping GPS, Current status, iStopped =  " + gpsStopped);
                gpsStopped = true;
            }

            if (!isCanceled) {
                if (isActive && counts < TIME_OUT) {
                    Log.d(TAG, "Location Refreshed and GPS Stopped = " + gpsStopped);
                    //update last best location
                    if (!gpsStopped) {
                        Log.d(TAG, "fetch location from gps");
                        bestLocation = getLocation(mContext, GPS_LOCATION_UPDATES);
                    } else {
                        bestLocation = getLocation(mContext, NETWORK_LOCATION_UPDATES);
                    }

                    if (bestLocation == null) {
                        Log.d(TAG, "BestLocation not ready, continue to wait");
                        handler.postDelayed(this, iteration_timeout_step);
                    } else {
                        Log.d(TAG, "Location found before time out");
                        // removing all updates and listeners
                        isActive = false;
                        myLocationManager.removeUpdates(gpsLocationListener);
                        myLocationManager.removeUpdates(networkLocationListener);
                        myLocationManager.removeGpsStatusListener(gpsStatusListener);
                        sat_count = 0;
                        // send best location to locationResult
                        locationFound(bestLocation);
                    }

                } else {
                    // time out
                    if (counts > TIME_OUT) {
                        Log.d(TAG, "Location time out");

                        // removing all updates and listeners
                        isActive = false;
                        myLocationManager.removeUpdates(gpsLocationListener);
                        myLocationManager.removeUpdates(networkLocationListener);
                        myLocationManager.removeGpsStatusListener(gpsStatusListener);

                        buildGoogleApiClient();

                    } else {
                        handler.postDelayed(this, iteration_timeout_step);
                    }
                }
            } else {
                myLocationManager.removeUpdates(gpsLocationListener);
                myLocationManager.removeUpdates(networkLocationListener);
                myLocationManager.removeGpsStatusListener(gpsStatusListener);
            }
        }
    };

    /**
     * Determine if continue to try to find best location
     */
    protected boolean needToStop() {
        if (gps_provider_enabled) {
            if (counts <= 4) {
                return false;
            }
            if (sat_count < min_gps_sat_count) {
                //if 20-25 sec and 3 satellites found then stop
                if (counts >= 40 && sat_count >= 3) {
                    return true;
                }
                return false;
            }
        }
        return true;
    }

    public void cancel()
    {
        isCanceled = true;
        Log.d(TAG,"Cancel :: MyLocation canceled");
    }

    /**
     * Returns best location using LocationManager.getBestProvider()
     *
     * @param context
     * @return Location|null
     */
    public static Location getLocation(Context context, int mode) {
        Log.d(TAG, "fetch last known location and update it");

        // fetch last known location and update it
        try {
            LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            criteria.setAltitudeRequired(false);
            criteria.setBearingRequired(false);
            criteria.setCostAllowed(true);
            String strLocationProvider = null;
            switch (mode) {
                case GPS_LOCATION_UPDATES:
                    strLocationProvider = "gps";
                    break;
                case NETWORK_LOCATION_UPDATES:
                    strLocationProvider = "network";
                    break;
                case DEFAULT_LOCATION_UPDATES:
                    strLocationProvider = lm.getBestProvider(criteria, true);
                    break;
                default:
                    strLocationProvider = "network";
                    break;
            }

            Log.d(TAG, "getLocation() : strLocationProvider" + strLocationProvider);

            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                throw new SecurityException();
            }

            Location location = lm.getLastKnownLocation(strLocationProvider);
            if(location != null){
                Log.d(TAG, "Location found using location manager");
                return location;
            }

            return null;

        } catch (Exception e) {
            Log.d(TAG, "getLocation()"+ e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private void locationFound(Location location){
        if(locationResult != null && !isCanceled) {
            mLocation = location;
            locationResult.gotLocation(location);
            if (findLocality) {

                AddressResultReceiver resultReceiver = new AddressResultReceiver(null);

                Intent intent = new Intent (mContext, FetchAddressIntentService.class);
                intent.putExtra (LocationApiConstants.LOCATION_DATA_EXTRA, location);
                intent.putExtra (LocationApiConstants.RECEIVER, resultReceiver);
                mContext.startService (intent);

            }else{
                stopProgressbar ();
            }
        }
    }

    protected void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build();
        mGoogleApiClient.connect();
    }

    // Interface
    public interface MyLocationDataListener{
        public void gotLocation (Location location);
        public void gotLocation (Location location, Bundle _addresses);
        public void locationNotAvailable ();
        public void onPermissionRequired();
    }

    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string
            // or an error message sent from the intent service.
            String addressOutput = resultData.getString(LocationApiConstants.RESULT_DATA_MSG_KEY);
            // Show a toast message if an address was found.
            if (resultCode == LocationApiConstants.SUCCESS_RESULT) {
                if(locationResult != null){
                    locationResult.gotLocation (mLocation, resultData);
                }
            }else{
                Log.e (TAG, "Location not received");
            }
            stopProgressbar();
        }
    }

    private void showProgressbar(Context _context, String _message, boolean _cancellable, boolean _indeterminate){
        progressBar = new ProgressDialog(_context);
        progressBar.setCancelable(_cancellable);
        progressBar.setMessage(_message);
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.setProgress(0);
        progressBar.setMax(100);
        progressBar.setIndeterminate (_indeterminate);
        progressBar.show();
    }

    private void stopProgressbar(){
        if(progressBar != null){
            progressBar.dismiss ();
            progressBar = null;
        }
    }

}
