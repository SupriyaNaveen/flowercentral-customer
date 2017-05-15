package com.flowercentral.flowercentralcustomer.common.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Ashish Upadhyay on 5/10/17.
 */

public class User implements Parcelable {

    private String mAccessToken;
    private String mUserID;
    private String mUserEmail;
    private String mUserFName;
    private String mUserLName;
    private String mProfilePic;
    private int mLoginMethod;

    public User () {

    }

    protected User (Parcel in) {
        mAccessToken = in.readString ();
        mUserID = in.readString ();
        mUserEmail = in.readString ();
        mUserFName = in.readString ();
        mUserLName = in.readString ();
        mProfilePic = in.readString ();
        mLoginMethod = in.readInt ();
    }

    @Override
    public void writeToParcel (Parcel dest, int flags) {
        dest.writeString (mAccessToken);
        dest.writeString (mUserID);
        dest.writeString (mUserEmail);
        dest.writeString (mUserFName);
        dest.writeString (mUserLName);
        dest.writeString (mProfilePic);
        dest.writeInt (mLoginMethod);
    }

    @Override
    public int describeContents () {
        return 0;
    }

    public static final Creator<User> CREATOR = new Creator<User> () {
        @Override
        public User createFromParcel (Parcel in) {
            return new User (in);
        }

        @Override
        public User[] newArray (int size) {
            return new User[size];
        }
    };

    public String getAccessToken () {
        return mAccessToken;
    }

    public void setAccessToken (String _accessToken) {
        mAccessToken = _accessToken;
    }

    public String getUserID () {
        return mUserID;
    }

    public void setUserID (String _userID) {
        mUserID = _userID;
    }

    public String getUserEmail () {
        return mUserEmail;
    }

    public void setUserEmail (String _userEmail) {
        mUserEmail = _userEmail;
    }

    public String getUserFName () {
        return mUserFName;
    }

    public void setUserFName (String _userFName) {
        mUserFName = _userFName;
    }

    public String getUserLName () {
        return mUserLName;
    }

    public void setUserLName (String _userLName) {
        mUserLName = _userLName;
    }

    public String getProfilePic () {
        return mProfilePic;
    }

    public void setProfilePic (String _profilePic) {
        mProfilePic = _profilePic;
    }

    public int getLoginMethod () {
        return mLoginMethod;
    }

    public void setLoginMethod (int _loginMethod) {
        mLoginMethod = _loginMethod;
    }
}
