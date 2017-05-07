package com.flowercentral.flowercentralcustomer.volley;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.flowercentral.flowercentralcustomer.setting.AppConstant;
import com.flowercentral.flowercentralcustomer.util.Logger;


/**
 * Created by Ashish Upadhyay on 7/18/16.
 */

public class RestUtil {

    private static String TAG = RestUtil.class.getSimpleName();

    public static boolean isNetworkAvailable(Context context) {
        boolean isNetworkAvailable = false;
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isConnected()) {
            Logger.log(TAG, "isNetworkAvailable","network type" + activeInfo.getTypeName(), AppConstant.LOG_LEVEL_INFO);
            isNetworkAvailable = true;
        }

        return isNetworkAvailable;
    }
}
