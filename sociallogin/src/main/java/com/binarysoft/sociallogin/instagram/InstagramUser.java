package com.binarysoft.sociallogin.instagram;

/**
 * Created by Ashish Upadhyay on 5/13/17.
 */

public class InstagramUser {

    private String mAccessToken;
    private String mID;
    private String mUserName;
    private String mFullName;
    private String mProfilePicture;

    public InstagramUser(){

    }

    public String getAccessToken () {
        return mAccessToken;
    }

    public void setAccessToken (String accessToken) {
        mAccessToken = accessToken;
    }

    public String getID () {
        return mID;
    }

    public void setID (String ID) {
        mID = ID;
    }

    public String getUserName () {
        return mUserName;
    }

    public void setUserName (String userName) {
        mUserName = userName;
    }

    public String getFullName () {
        return mFullName;
    }

    public void setFullName (String fullName) {
        mFullName = fullName;
    }

    public String getProfilePicture () {
        return mProfilePicture;
    }

    public void setProfilePicture (String profilePicture) {
        mProfilePicture = profilePicture;
    }
}
