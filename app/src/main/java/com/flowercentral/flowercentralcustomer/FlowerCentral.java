package com.flowercentral.flowercentralcustomer;

import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;

import com.flowercentral.flowercentralcustomer.preference.UserPreference;
import com.flowercentral.flowercentralcustomer.util.Logger;

import java.util.List;

/**
 * Created by Ashish Upadhyay on 4/29/17.
 */

public class FlowerCentral extends Application {

    private final static String TAG = FlowerCentral.class.getSimpleName ();

    private static Application mAppInstance;
    private Context mContext;

    @Override
    public void onCreate () {
        super.onCreate ();
        mContext = this;
        mAppInstance = this;

        // Enable verbose logging and strict mode in debug builds
        if (BuildConfig.DEBUG) {
            Logger.print_log_to_file = true;
            Logger.print_log_to_file = true;

            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .penaltyLog()
                .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .build());
        }else
        {

            Logger.print_log_to_file = false;
            Logger.print_log_to_file = false;
        }

        UserPreference.init(mContext);

    }

    @Override
    protected void attachBaseContext (Context base) {
        super.attachBaseContext (base);
    }

    public static Application getInstance() {
        return mAppInstance;
    }

    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }
        return isInBackground;
    }

}
