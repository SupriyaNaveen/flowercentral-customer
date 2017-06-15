package com.flowercentral.flowercentralcustomer.util;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;


public class PermissionUtil {

    // Initialize the request code for each permission
    public static final int REQUEST_CODE_READ_PHONE_STATE = 103;
    public static final int REQUEST_CODE_LOCATION = 101;
    public static final int REQUEST_CODE_READ_EXTERNAL_STORAGE = 102;

    /**
     * Check whether the permission is already granted or not
     *
     * @param activity   Activity
     * @param permission Permission to be checked
     * @return Boolean whether permission is granted or not
     */
    public static boolean hasPermission(@NonNull Activity activity, @NonNull String permission) {
        return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Checks whether to display the description of permission to user or not
     *
     * @param activity   Activity
     * @param permission Permission to be checked
     * @return Boolean whether to show the description to user or not
     */
    public static boolean showRationale(@NonNull Activity activity, @NonNull String permission) {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
    }

    /**
     * Request for the permissions
     *
     * @param activity    Activity
     * @param permission  Array of permissions to be requested
     * @param requestCode Int Request code for the permission
     */
    public static void requestPermission(@NonNull Activity activity, @NonNull String permission[], int requestCode) {
        ActivityCompat.requestPermissions(activity, permission, requestCode);
    }
}
