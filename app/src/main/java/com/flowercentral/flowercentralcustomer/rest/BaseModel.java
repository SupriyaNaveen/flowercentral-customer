package com.flowercentral.flowercentralcustomer.rest;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.flowercentral.flowercentralcustomer.R;
import com.flowercentral.flowercentralcustomer.preference.UserPreference;
import com.flowercentral.flowercentralcustomer.setting.AppConstant;
import com.flowercentral.flowercentralcustomer.util.Logger;
import com.flowercentral.flowercentralcustomer.util.Util;
import com.flowercentral.flowercentralcustomer.volley.AsyncHttpClient;
import com.flowercentral.flowercentralcustomer.volley.CustomJsonArrayObjectRequest;
import com.flowercentral.flowercentralcustomer.volley.CustomJsonObjectRequest;
import com.flowercentral.flowercentralcustomer.volley.ErrorData;
import com.flowercentral.flowercentralcustomer.volley.HttpResponseListener;
import com.flowercentral.flowercentralcustomer.volley.RestUtil;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by Ashish Upadhyay on 7/18/16.
 */
public abstract class BaseModel<T> implements Response.ErrorListener,HttpResponseListener<T> {

    public static final int INVALID_INPUT_SUPPLIED = 400;
    public static final int AUTHENTICATION_ERROR = 401;
    public static final int UNAUTHORIZED_ERROR = 403;
    public static final int CONNECTION_TIMEOUT = 408;
    public static final int INTERNAL_SERVER_ERROR = 500;

    private String TAG = BaseModel.class.getSimpleName();
    private static final int MY_SOCKET_TIMEOUT_MS = 10000;
    public Context mContext;

    public BaseModel (Context context)
    {
        this.mContext = context;
    }

    private Response.Listener<T> listener = new Response.Listener<T>() {
        @Override
        public void onResponse(T response) {

        }
    } ;

    @Override
    public void onErrorResponse(VolleyError error) {


        // write logic here for handling error and preparing custom error;
        ErrorData errorData = new ErrorData ();

        if(error!=null&&error.networkResponse!=null) {

            errorData.setErrorCode(error.networkResponse.statusCode);

            byte[] dataU = error.networkResponse.data;
            if(dataU!=null) {
                String s = new String(dataU);
                try {
                    JSONObject obj = new JSONObject(s);
                    if (obj != null) {
                        if (!obj.isNull("errorCode")) {
                            String code = obj.getString("errorCode");
                            errorData.setErrorCodeOfResponseData(code);
                            errorData.setErrorMessage(obj.getString("errorMessage"));
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if(!TextUtils.isEmpty(error.getMessage()))
            {
                errorData.setErrorMessage(error.getMessage());
            }


            switch (error.networkResponse.statusCode) {

                case UNAUTHORIZED_ERROR:
                    errorData.setErrorMessage("Unauthorized: Access denied.");
                    errorData.setErrorType (ErrorData.ERROR_TYPE.UNAUTHORIZED_ERROR);

                case AUTHENTICATION_ERROR:
                    /*if(!(mContext instanceof LoginActivity)) {

                        if (mContext != null && mContext instanceof BaseActivity) {
                            ((BaseActivity) mContext).dismissDialog(0);
                            ((BaseActivity) mContext).finish();
                        }

                        //Todo: reset all authentication related data

                        Intent intent = new Intent(mContext, SplashActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        mContext.startActivity(intent);
                        AsyncHttpClient.getInstance(mContext).cancelAllRequests();
                    }else
                    {
                        errorData.setErrorMessage(mContext.getString (R.string.err_inavlid_username_password));
                        errorData.setErrorType (ErrorData.ERROR_TYPE.AUTHENTICATION_ERROR);
                    }*/
                    break;
                case CONNECTION_TIMEOUT:
                    errorData.setErrorType(ErrorData.ERROR_TYPE.CONNECTION_TIMEOUT);
                    errorData.setErrorMessage ("Can not connect to server. Connection timeout");
                case INTERNAL_SERVER_ERROR:
                    errorData.setErrorType(ErrorData.ERROR_TYPE.INTERNAL_SERVER_ERROR);
                    errorData.setErrorMessage ("Internal server error. Please contact to system administrator.");
                case INVALID_INPUT_SUPPLIED:
                    byte[] data = error.networkResponse.data;
                    String s = new String(data);
                    try {
                        JSONObject obj = new JSONObject(s);
                        if(obj!=null){
                            if(!obj.isNull("errorCode")){
                                String code =   obj.getString("errorCode");
                                errorData.setErrorCodeOfResponseData(code);
                                errorData.setErrorType (ErrorData.ERROR_TYPE.INVALID_INPUT_SUPPLIED);
                                errorData.setErrorMessage ("Invalid input supplied, please provide required and valid parameters.");
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                default:
                    errorData.setErrorType(ErrorData.ERROR_TYPE.INTERNAL_SERVER_ERROR);

            }
        }else
        {
            errorData.setErrorType(ErrorData.ERROR_TYPE.APPLICATION_ERROR);
            if(error!=null&&error.getMessage()!=null)
            {

                errorData.setErrorMessage(error.getMessage());
                errorData.setNetworkTimems(error.getNetworkTimeMs());
            }else if(error instanceof TimeoutError)
            {
                errorData.setErrorType(ErrorData.ERROR_TYPE.CONNECTION_TIMEOUT);
                if(mContext!=null)
                {
                    errorData.setErrorMessage(mContext.getString(R.string.msg_connection_time_out));

                }else {
                    errorData.setErrorMessage(mContext.getString(R.string.msg_connection_time_out));
                }
            }else
            {
                errorData.setErrorMessage("Error response data is null");
            }
        }
        if(!TextUtils.isEmpty(errorData.getErrorMessage())&&errorData.getErrorMessage().contains("java.io.IOException: No authentication challenges found"))
        {
            /*if(!(mContext instanceof LoginActivity)) {

                if (mContext != null && mContext instanceof BaseActivity) {
                    ((BaseActivity) mContext).dismissDialog(0);
                    ((BaseActivity) mContext).finish();
                }
                *//*UserPreferences.setUser(null);
                UserPreferences.setSecurityPin("");
                UserPreferences.setAppApiToken(null);*//*

                Intent intent = new Intent(mContext, SplashActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mContext.startActivity(intent);
                AsyncHttpClient.getInstance(mContext).cancelAllRequests();
                errorData.setErrorMessage("You have been logged out. Please login again");
            }*/
        }

        onError(errorData);
    }

    @Override
    public abstract void onSuccess(int statusCode, Map<String, String> headers, T response);


    @Override
    public abstract void onError(ErrorData error);

    public void executeGetJsonRequest(String url,@Nullable String tag)
    {
        if(RestUtil.isNetworkAvailable(mContext)) {
            JSONObject params = appendCommonParams(mContext, new JSONObject());
            CustomJsonObjectRequest request = new CustomJsonObjectRequest(Request.Method.GET, url, params, listener, this);
            request.setRetryPolicy(new DefaultRetryPolicy (MY_SOCKET_TIMEOUT_MS,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            request.setCustomResponseListener(this);
            request.appendHeaderValues(getCommonAuthorizationHeader());
            // addCommonHeaderParams(request);
            AsyncHttpClient.getInstance(mContext).addToRequestQueue(request, tag);
        }else
        {
            ErrorData errorData = new ErrorData();
            errorData.setErrorType(ErrorData.ERROR_TYPE.NETWORK_NOT_AVAILABLE);
            errorData.setErrorMessage(mContext.getResources().getString(R.string.msg_internet_unavailable));
            onError(errorData);
        }
    }

    public void executePostJsonRequest(String url,JSONObject jsonObjectData,@Nullable String tag)
    {

        if(RestUtil.isNetworkAvailable(mContext)) {
            JSONObject params = appendCommonParams(mContext, jsonObjectData);
            CustomJsonObjectRequest request = new CustomJsonObjectRequest(Request.Method.POST, url, params, listener, this);
            request.setRetryPolicy(new DefaultRetryPolicy (MY_SOCKET_TIMEOUT_MS,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            request.setCustomResponseListener(this);
            request.appendHeaderValues(getCommonAuthorizationHeader());
            //addCommonHeaderParams(request);
            AsyncHttpClient.getInstance(mContext).addToRequestQueue(request, tag);
        }else
        {
            ErrorData errorData = new ErrorData();
            errorData.setErrorType(ErrorData.ERROR_TYPE.NETWORK_NOT_AVAILABLE);
            errorData.setErrorMessage(mContext.getResources().getString(R.string.msg_internet_unavailable));
            onError(errorData);
        }
    }

    public void executeGetJsonArrayRequest(String url,@Nullable String tag)
    {
        if(RestUtil.isNetworkAvailable(mContext)) {
            JSONObject params = appendCommonParams(mContext, new JSONObject());
            CustomJsonArrayObjectRequest request = new CustomJsonArrayObjectRequest(Request.Method.GET, url, null, listener, this);
            request.setRetryPolicy(new DefaultRetryPolicy (MY_SOCKET_TIMEOUT_MS,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            request.setCustomResponseListener(this);
            request.appendHeaderValues(getCommonAuthorizationHeader());
            // addCommonHeaderParams(request);
            AsyncHttpClient.getInstance(mContext).addToRequestQueue(request, tag);
        }else
        {
            ErrorData errorData = new ErrorData();
            errorData.setErrorType(ErrorData.ERROR_TYPE.NETWORK_NOT_AVAILABLE);
            errorData.setErrorMessage(mContext.getResources().getString(R.string.msg_internet_unavailable));
            onError(errorData);
        }
    }

    private void addCommonHeaderParams(Request request)
    {
        try {
            if(request.getHeaders()!=null){
                request.getHeaders().putAll(getCommonAuthorizationHeader());
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private JSONObject appendCommonParams(Context context,JSONObject jsonObject)
    {
        if(jsonObject==null)
        {
            jsonObject = new JSONObject();
        }
        try {
            jsonObject.put("build_number", String.valueOf(getVersionCode(context)));
            jsonObject.put("version_number", getVersionName(context));
            jsonObject.put ("tutorly-device-id", Util.getDeviceId (context));
            jsonObject.put ("tutorly-device-type", "android");


        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private  Map<String, String> getCommonAuthorizationHeader() {
        Map<String, String> headerValues = new HashMap<String, String>();
        try {

           /* Logger.logInfo(TAG, "getCommonAuthorizationHeader","Access token " + StorageManager.getAccessToken());
            Logger.logInfo(TAG,"getCommonAuthorizationHeader", "Refresh token " + StorageManager.getRefreshToken());
            Logger.logInfo(TAG,"getCommonAuthorizationHeader", "TimeStamp " + StorageManager.getTimestamp());

            if(!TextUtils.isEmpty(StorageManager.getAccessToken())) {
                String encodedString = Base64.encodeToString(StorageManager.getAccessToken().getBytes(), Base64.NO_WRAP);
                headerValues.put("Authorization", "Basic " + encodedString);
                headerValues.put("Content-Type", "application/json");
                headerValues.put("Accept-Encoding", "gzip");
            }*/

            headerValues.put("Content-Type", "application/json");
            headerValues.put("Accept", "application/json");
            headerValues.put("tutorly-device-id", Util.getDeviceId (mContext));
            headerValues.put("tutorly-device-type", "android");
            String apiToken = UserPreference.getApiToken ();
            if(apiToken != null && !apiToken.isEmpty() ){
                headerValues.put("Authorization", "Bearer "+apiToken);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        Logger.log(TAG,"getCommonAuthorizationHeader", "Header values " + headerValues, AppConstant.LOG_LEVEL_INFO);
        return headerValues;
    }


    public static String getVersionName(Context mContext) {
        String versionName = null;
        try {
            PackageInfo pInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
            if (pInfo != null) {
                versionName = pInfo.versionName;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return versionName;
    }

    public static int getVersionCode(Context mContext) {
        int versionCode = 0;
        try {
            PackageInfo pInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
            if (pInfo != null) {
                versionCode = pInfo.versionCode;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    public void cancelRequests(){
        AsyncHttpClient.getInstance(mContext).cancelAllRequests ();
    }


    public void cancelRequestByTag(String _tag){
        AsyncHttpClient.getInstance(mContext).cancelAllRequestsByTag (_tag);
    }

}
