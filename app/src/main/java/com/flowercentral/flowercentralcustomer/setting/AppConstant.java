package com.flowercentral.flowercentralcustomer.setting;


import com.flowercentral.flowercentralcustomer.BuildConfig;

/**
 * Created by Ashish Upadhyay on 4/29/17.
 */

public final class AppConstant {

    public final static String APP_NAME = "FlowerCentral";
    public static final String LOCAL_DB_NAME = "flower_central.db";
    public static final int DB_VERSION = 1;
    public static final int REQUEST_CODE_LOCATION_PERMISSIONS = 100;

    public enum APP_MODE {DEVELOPMENT, TEST, PRODUCTION};

    public static final int VERSION_CODE = BuildConfig.VERSION_CODE;
    public static final String VERSION_NAME = BuildConfig.VERSION_NAME;
    public static final String BUILD_MODE = BuildConfig.BUILD_MODE;
    public static final String BUILD_TYPE = BuildConfig.BUILD_TYPE;

    // Log Level
    public static final int LOG_LEVEL_ERR = 1;
    public static final int LOG_LEVEL_DEBUG = 2;
    public static final int LOG_LEVEL_WARN = 3;
    public static final int LOG_LEVEL_INFO = 4;

    // Date Format
    public static final String DISPLAY_DATE_FORMAT = "dd-MMM-yyyy";
    public static final String DB_DATE_FORMAT = "yyyy-MM-dd";
    public static final String SHORT_DATE_FORMAT = "dd-MMM";

    //Static Actions/Request
    public enum  ACTIONS{LOGIN, REGISTER, FORGOT_PASSWORD, NO_INTERNET, HOME, EXIT,
        PRODUCT_DETAILS, SHOW_CART, SHOW_DELIVERY_ADDR, USER_PROFILE, BUY_NOW, ADD_TO_CART, LIKE, UNLIKE, SHOW_ORDER, DELIVERY_TIME, HELP};

    public enum  LOGIN_TYPE{FACEBOOK, GOOGLE, INSTAGRAM, CUSTOM};
    public enum VIEW_TYPE {LIST, GRID};
    public enum SORTING_OPTION {BY_CATEGORY, BY_PRICE};

}
