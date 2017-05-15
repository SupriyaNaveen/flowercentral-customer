package com.binarysoft.sociallogin.facebook;

/**
 * Created by Ashish Upadhyay on 5/13/17.
 */

public class FacebookUser {

    private String mID;
    private String mName;
    private String mFirstName;
    private String mLastName;
    private String mEmail;
    private String mGender;
    private String mPicture;
    private String mCover;
    private boolean mVerified;

    public FacebookUser(){

    }

    public String getID () {
        return mID;
    }

    public void setID (String _ID) {
        mID = _ID;
    }

    public String getName () {
        return mName;
    }

    public void setName (String _name) {
        mName = _name;
    }

    public String getFirstName () {
        return mFirstName;
    }

    public void setFirstName (String _firstName) {
        mFirstName = _firstName;
    }

    public String getLastName () {
        return mLastName;
    }

    public void setLastName (String _lastName) {
        mLastName = _lastName;
    }

    public String getEmail () {
        return mEmail;
    }

    public void setEmail (String _email) {
        mEmail = _email;
    }

    public String getGender () {
        return mGender;
    }

    public void setGender (String _gender) {
        mGender = _gender;
    }

    public String getPicture () {
        return mPicture;
    }

    public void setPicture (String _picture) {
        mPicture = _picture;
    }

    public String getCover () {
        return mCover;
    }

    public void setCover (String _cover) {
        mCover = _cover;
    }

    public boolean getVerified () {
        return mVerified;
    }

    public void setVerified (boolean _verified) {
        mVerified = _verified;
    }

}
