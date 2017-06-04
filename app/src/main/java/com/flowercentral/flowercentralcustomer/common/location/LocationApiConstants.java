package com.flowercentral.flowercentralcustomer.common.location;

/**
 * Created by Ashish Upadhyay on 1/27/2016.
 */
public class LocationApiConstants {

    // The desired interval for location updates. Inexact. Updates may be more or less frequent.
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 2000;

    // The fastest rate for active location updates. Exact. Updates will never be more frequent than this value.
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    public static final int MAX_NUMBER_OF_UPDATES = 5;

    public static final float SMALLEST_DISPLACEMENT = 5.0f;

    public static final int TIME_TO_ESTIMATES_BEST_LOCATION = 1000 * 60 * 1;

    //Timeout in miliseconds
    public static final int TIME_OUT_UPDATE_LOCATION = 5000;

    //Specific to Fetch Location Address service
    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;
    //public static final String PACKAGE_NAME = "com.google.android.gms.location.sample.locationaddress";
    public static final String PACKAGE_NAME = "com.flowercentral.flowercentralcustomer";
    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
    public static final String RESULT_DATA_MSG_KEY = PACKAGE_NAME + ".RESULT_DATA_MSG_KEY";
    public static final String RESULT_DATA_LAT_KEY = PACKAGE_NAME + ".RESULT_DATA_LAT_KEY";
    public static final String RESULT_DATA_LNG_KEY = PACKAGE_NAME + ".RESULT_DATA_LNG_KEY";
    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_DATA_EXTRA";

    public static String geocoder_service_not_available = "No Geocoder service is available.";
    public static String invalid_lat_long_used = "Invalid latitude & longitude of the place.";
    public static String no_address_found = "No address found for the given location.";
    public static String address_found = "Address found.";

}

