package com.binarysoft.sociallogin.facebook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;

public class FacebookHelper {
    private FacebookListener mListener;
    private CallbackManager mCallBackManager;
    private AccessToken mAccessToken;
    private FacebookGraphListner mGraphListener;

    public FacebookHelper (@NonNull FacebookListener facebookListener) {
        mListener = facebookListener;
        mCallBackManager = CallbackManager.Factory.create ();
        FacebookCallback<LoginResult> mCallBack = new FacebookCallback<LoginResult> () {
            @Override
            public void onSuccess (LoginResult loginResult) {
                mAccessToken = loginResult.getAccessToken ();
                mListener.onFbSignInSuccess (mAccessToken.getToken (), loginResult.getAccessToken ().getUserId ());
            }

            @Override
            public void onCancel () {
                mListener.onFbSignInFail ("User cancelled operation");
            }

            @Override
            public void onError (FacebookException e) {
                mListener.onFbSignInFail (e.getMessage ());
            }
        };
        LoginManager.getInstance ().registerCallback (mCallBackManager, mCallBack);
    }

    @NonNull
    @CheckResult
    public CallbackManager getCallbackManager () {
        return mCallBackManager;
    }

    public void performSignIn (Activity activity) {
        LoginManager.getInstance ()
                .logInWithReadPermissions (activity,
                        Arrays.asList ("public_profile", "user_friends", "email"));
    }

    public void performSignIn (Fragment fragment) {
        LoginManager.getInstance ()
                .logInWithReadPermissions (fragment,
                        Arrays.asList ("public_profile", "user_friends", "email"));
    }

    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        mCallBackManager.onActivityResult (requestCode, resultCode, data);
    }

    public void performSignOut () {
        LoginManager.getInstance ().logOut ();
        mListener.onFBSignOut ();
    }

    public void registerProfileListener(FacebookGraphListner _graphListener){
        mGraphListener = _graphListener;
    }

    public void getProfile(){

        GraphRequest request = GraphRequest.newMeRequest (mAccessToken, new GraphRequest.GraphJSONObjectCallback () {
            @Override
            public void onCompleted (JSONObject object, GraphResponse response) {
                if(mGraphListener == null){
                    throw new RuntimeException ("Facebook Graph Listener is not implemented");
                }
                FacebookUser fbUser = getUser (object);
                mGraphListener.onProfileReceived (fbUser);
            }
        });

        //Basic profile with minimum permission
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name, first_name,last_name,email,gender,picture, cover, verified");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private FacebookUser getUser(JSONObject _object){
        FacebookUser user = null;
        if(_object != null){
            try{
                user = new FacebookUser ();
                user.setID (_object.getString ("id"));
                user.setName (_object.getString ("name"));
                user.setFirstName (_object.getString ("first_name"));
                user.setLastName (_object.getString ("last_name"));
                user.setEmail (_object.getString ("email"));
                user.setGender (_object.getString ("gender"));
                user.setVerified (_object.getBoolean ("verified"));

            }catch (JSONException jsonEx){
                jsonEx.printStackTrace ();
                user = null;
            }catch (Exception ex){
                ex.printStackTrace ();
                user = null;
            }
        }
        return user;
    }
}
