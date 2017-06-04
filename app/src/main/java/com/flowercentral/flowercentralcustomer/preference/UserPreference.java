package com.flowercentral.flowercentralcustomer.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import com.flowercentral.flowercentralcustomer.common.model.User;

import java.util.HashMap;

/**
 * Created by Ashish Upadhyay on 4/29/17.
 */

public class UserPreference extends PreferenceActivity {

    private final String TAG = UserPreference.class.getSimpleName ();
    private static Context mContext;

    private final static String API_TOKEN_KEY = "api_token_key";
    private final static String LOGIN_ACCESS_TOKEN = "login_access_token";
    private final static String LOGGED_IN_USER_ID = "logged_in_user_id";
    private final static String LOGGED_IN_USER_EMAIL = "logged_in_user_id";
    private final static String LOGGED_IN_USER_FNAME = "logged_in_user_fname";
    private final static String LOGGED_IN_USER_LNAME = "logged_in_user_lname";
    private final static String LOGGED_IN_USER_PIC = "logged_in_user_pic";
    private final static String LOG_IN_METHOD = "login_method";

    private final static String CURR_LATITUDE = "latitude";
    private final static String CURR_LONGITUDE = "longitude";
    private final static String CURR_ADDRESS = "address";


    @Override
    public void onCreate(Bundle _savedInstanceState){
        super.onCreate(_savedInstanceState);
    }

    public static void init(Context _context){
        mContext = _context;
    }

    public static void registerSharedPreferenceChangedListner(Context mContext, SharedPreferences.OnSharedPreferenceChangeListener _listner)
    {
        if(_listner!=null)
        {
            PreferenceManager.getDefaultSharedPreferences(mContext)
                    .registerOnSharedPreferenceChangeListener(_listner);
        }
    }

    public static void unregisterSharedPreferenceChangedListner(Context mContext,SharedPreferences.OnSharedPreferenceChangeListener _listner)
    {
        if(_listner!=null)
        {
            PreferenceManager.getDefaultSharedPreferences(mContext)
                    .unregisterOnSharedPreferenceChangeListener(_listner);
        }
    }

    private static String readString(Context mContext, final String _key,String _defaultValue) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
        return pref.getString(_key,_defaultValue);
    }

    private static void writeString(Context mContext, final String _key, final String _value) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(_key, _value);
        editor.commit();
    }

    private static boolean readBoolean(Context mContext, final String _key, final boolean _defaultValue) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mContext);
        return settings.getBoolean(_key, _defaultValue);
    }

    private static void writeBoolean(Context mContext, final String _key, final boolean _value) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(_key, _value);
        editor.commit();
    }

    private static float readFloat(Context mContext, final String _key, final Float _defaultValue) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mContext);
        return settings.getFloat(_key, _defaultValue);
    }

    private static void writeFloat(Context mContext, final String _key, final Float _value) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = settings.edit();
        editor.putFloat(_key, _value);
        editor.commit();
    }

    private static int readInt(Context mContext, final String _key, final int _defaultValue) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mContext);
        return settings.getInt(_key, _defaultValue);
    }

    private static void writeInt(Context mContext, final String _key, final int _value) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(_key, _value);
        editor.commit();
    }

    private static long readLong(Context mContext, final String _key, final long _defaultValue) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mContext);
        return settings.getLong (_key, _defaultValue);
    }

    private static void writeLong(Context mContext, final String _key, final long _value) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(_key, _value);
        editor.commit();
    }

    public static void setApiToken(String _token){
        writeString (mContext, API_TOKEN_KEY, _token);
    }

    public static String getApiToken(){
        return readString (mContext, API_TOKEN_KEY, null);
    }

    public static void setAccessToken(String _token){
        writeString (mContext, LOGIN_ACCESS_TOKEN, _token);
    }

    public static String getAccessToken(){
        return readString (mContext, LOGIN_ACCESS_TOKEN, null);
    }

    public static void setUserID(String _id){
        writeString (mContext, LOGGED_IN_USER_ID, _id);
    }

    public static String getUserID(){
        return readString (mContext, LOGGED_IN_USER_ID, null);
    }

    public static void setUserEmail(String _email){
        writeString (mContext, LOGGED_IN_USER_EMAIL, _email);
    }

    public static String getUserEmail(){
        return readString (mContext, LOGGED_IN_USER_EMAIL, null);
    }

    public static void setUserFirstName(String _fname){
        writeString (mContext, LOGGED_IN_USER_FNAME, _fname);
    }

    public static String getUserFirstName(){
        return readString (mContext, LOGGED_IN_USER_FNAME, null);
    }

    public static void setUserLastName(String _lname){
        writeString (mContext, LOGGED_IN_USER_LNAME, _lname);
    }

    public static String getUserLastName(){
        return readString (mContext, LOGGED_IN_USER_LNAME, null);
    }

    public static void setProfilePic(String _lname){
        writeString (mContext, LOGGED_IN_USER_LNAME, _lname);
    }

    public static String getProfilePic(){
        return readString (mContext, LOGGED_IN_USER_LNAME, null);
    }

    public static void setLoginMethod(int _loginType){
        writeInt (mContext, LOG_IN_METHOD, _loginType);
    }

    public static int getLoginMethod(){
        return readInt (mContext, LOG_IN_METHOD, -1);
    }

    public static User getProfileInformation(){
        User user = new User ();
        user.setAccessToken (readString (mContext, LOGIN_ACCESS_TOKEN, null));
        user.setUserID (readString (mContext, LOGGED_IN_USER_ID, null));
        user.setUserEmail (readString (mContext, LOGGED_IN_USER_EMAIL, null));
        user.setUserFName (readString (mContext, LOGGED_IN_USER_FNAME, null));
        user.setUserLName (readString (mContext, LOGGED_IN_USER_LNAME, null));
        user.setProfilePic (readString (mContext, LOGGED_IN_USER_PIC, null));
        user.setLoginMethod (readInt (mContext, LOG_IN_METHOD, -1));

        return user;
    }

    public static void deleteProfileInformation(){
        setAccessToken (null);
        setUserID (null);
        setUserEmail (null);
        setUserFirstName (null);
        setUserLastName (null);
        setProfilePic (null);
        setLoginMethod (-1);
    }

    public static void setLatitude(double _latitude){
        writeFloat (mContext, CURR_LATITUDE, (float)_latitude);
    }

    public static double getLatitude(){
        double latitude = (double) readFloat (mContext, CURR_LATITUDE, 0f);
        return latitude;
    }

    public static void setLongitude(double _longitude){
        writeFloat (mContext, CURR_LONGITUDE, (float)_longitude);
    }

    public static double getLongitude(){
        double longitude = (double) readFloat (mContext, CURR_LONGITUDE, 0f);
        return longitude;
    }

    public static void setAddress(String _address){
        writeString (mContext, CURR_ADDRESS, _address);
    }

    public static String getAddress(){
        String address = readString (mContext, CURR_ADDRESS, null);
        return address;
    }

    public static HashMap<String, String> getCurrentLocation(){
        HashMap<String, String> location = new HashMap<String, String> ();
        location.put ("latitude", String.valueOf (getLatitude ()));
        location.put ("longitude", String.valueOf (getLongitude ()));
        location.put ("address", getAddress ());
        return location;
    }


}
