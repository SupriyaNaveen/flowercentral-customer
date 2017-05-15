package com.binarysoft.sociallogin.google;

/**
 * Created by Ashish Upadhyay on 5/13/17.
 */

public class GoogleUser {

    private String mID;
    private String mDisplayName;
    private String mFamilyName;
    private String mEmail;
    private String mGivenName;
    private String mIDToken;
    private String mPhotoUrl;

    public GoogleUser(){

    }

    public String getID () {
        return mID;
    }

    public void setID (String _ID) {
        mID = _ID;
    }

    public String getDisplayName () {
        return mDisplayName;
    }

    public void setDisplayName (String _displayName) {
        mDisplayName = _displayName;
    }

    public String getFamilyName () {
        return mFamilyName;
    }

    public void setFamilyName (String _familyName) {
        mFamilyName = _familyName;
    }

    public String getEmail () {
        return mEmail;
    }

    public void setEmail (String _email) {
        mEmail = _email;
    }

    public String getGivenName () {
        return mGivenName;
    }

    public void setGivenName (String _givenName) {
        mGivenName = _givenName;
    }

    public String getIDToken () {
        return mIDToken;
    }

    public void setIDToken (String _IDToken) {
        mIDToken = _IDToken;
    }

    public String getPhotoUrl () {
        return mPhotoUrl;
    }

    public void setPhotoUrl (String _photoUrl) {
        mPhotoUrl = _photoUrl;
    }

}
