package com.flowercentral.flowercentralcustomer.common.location.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import com.flowercentral.flowercentralcustomer.common.location.LocationApiConstants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FetchAddressIntentService extends IntentService {
    private static final String TAG = FetchAddressIntentService.class.getSimpleName ();
    private static Context mContext;
    private String mErrorMessage;
    private ResultReceiver mReceiver;

    public FetchAddressIntentService () {
        super ("FetchAddressIntentService");
    }

    @Override
    public void onCreate () {
        super.onCreate ();
        Log.i (TAG, "Oncreate called");
    }

    @Override
    protected void onHandleIntent (Intent intent) {
        if (intent != null){
            mReceiver = intent.getParcelableExtra(LocationApiConstants.RECEIVER);
            Geocoder geocoder = new Geocoder (this, Locale.getDefault ());
            List<Address> addresses = null;
            // Get the location passed to this service through an extra.
            Location location = intent.getParcelableExtra(LocationApiConstants.LOCATION_DATA_EXTRA);
            if(location != null){
                try {
                    // In this, get just a single address.
                    addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                } catch (IOException ioException) {
                    // Catch network or other I/O problems.
                    mErrorMessage = LocationApiConstants.geocoder_service_not_available;
                    Log.e(TAG, mErrorMessage, ioException);
                } catch (IllegalArgumentException illegalArgumentException) {
                    // Catch invalid latitude or longitude values.
                    mErrorMessage = LocationApiConstants.invalid_lat_long_used;
                    Log.e(TAG, mErrorMessage + ". " + "Latitude = " + location.getLatitude() +
                            ", Longitude = " + location.getLongitude(), illegalArgumentException);
                }

            }

            // Handle case where no address was found.
            if (addresses == null || addresses.size()  == 0) {
                if (mErrorMessage.isEmpty()) {
                    mErrorMessage = LocationApiConstants.no_address_found;
                    Log.e(TAG, mErrorMessage);
                }
                deliverResultToReceiver(LocationApiConstants.FAILURE_RESULT, 0d, 0d, mErrorMessage);
            } else {
                Address address = addresses.get(0);
                ArrayList<String> addressFragments = new ArrayList<String>();

                // Fetch the address lines using getAddressLine,
                // join them, and send them to the thread.
                for(int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                    addressFragments.add(address.getAddressLine(i));
                }
                Log.i(TAG, LocationApiConstants.address_found);
                deliverResultToReceiver(LocationApiConstants.SUCCESS_RESULT, location.getLatitude (), location.getLongitude (), TextUtils.join (",",addressFragments));
            }


        }
    }

    private void deliverResultToReceiver (int _resultCode, double _lat, double _lng, String _message) {
        try{
            Bundle bundle = new Bundle();
            bundle.putString(LocationApiConstants.RESULT_DATA_MSG_KEY, _message);
            bundle.putDouble (LocationApiConstants.RESULT_DATA_LAT_KEY, _lat);
            bundle.putDouble (LocationApiConstants.RESULT_DATA_LNG_KEY, _lng);
            if(mReceiver != null){
                mReceiver.send(_resultCode, bundle);
            }else{
                throw new Exception ("Result Receiver is not implemented");
            }

        }catch (Exception e){
            e.printStackTrace ();
        }
    }

}
