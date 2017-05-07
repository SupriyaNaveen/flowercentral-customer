package com.flowercentral.flowercentralcustomer.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

/**
 * Created by Ashish Upadhyay on 4/29/17.
 */

public class UserPreference extends PreferenceActivity {

    private final String TAG = UserPreference.class.getSimpleName ();
    private static Context mContext;

    private final static String API_TOKEN_KEY = "api_token_key";

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
}
