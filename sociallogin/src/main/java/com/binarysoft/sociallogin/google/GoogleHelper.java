package com.binarysoft.sociallogin.google;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;

import java.io.IOException;
import java.util.HashMap;

public class GoogleHelper implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {
    private final String SCOPES = "oauth2:profile email";
    private final int RC_SIGN_IN = 100;
    private FragmentActivity mContext;
    private GoogleListener mListener;
    private GoogleApiClient mGoogleApiClient;

    public GoogleHelper (@NonNull GoogleListener listener, FragmentActivity context,
                         @Nullable String serverClientId) {
        mContext = context;
        mListener = listener;
        buildGoogleApiClient (buildSignInOptions (serverClientId));
    }

    private GoogleSignInOptions buildSignInOptions (@Nullable String serverClientId) {
        GoogleSignInOptions.Builder gso =
                new GoogleSignInOptions.Builder (GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail ()
                        .requestScopes (new Scope (Scopes.PLUS_LOGIN))
                        .requestScopes (new Scope (Scopes.PLUS_ME));
        if (serverClientId != null) gso.requestIdToken (serverClientId);
        return gso.build ();
    }

    private void buildGoogleApiClient (@NonNull GoogleSignInOptions gso) {
        mGoogleApiClient = new GoogleApiClient.Builder (mContext).enableAutoManage (mContext, this)
                .addApi (Auth.GOOGLE_SIGN_IN_API, gso)
                .build ();
        mGoogleApiClient.connect ();
    }

    public void performSignIn (Activity activity) {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent (mGoogleApiClient);
        activity.startActivityForResult (signInIntent, RC_SIGN_IN);
    }

    public void performSignIn (Fragment activity) {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent (mGoogleApiClient);
        activity.startActivityForResult (signInIntent, RC_SIGN_IN);
    }

    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            final GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent (data);
            if (result.isSuccess ()) {
                AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String> () {
                    @Override
                    protected String doInBackground (Void... params) {
                        String token = null;
                        try {
                            token =
                                    GoogleAuthUtil.getToken (mContext, result.getSignInAccount ().getAccount (), SCOPES);
                        } catch (IOException | GoogleAuthException e) {
                            e.printStackTrace ();
                        }
                        return token;
                    }

                    @Override
                    protected void onPostExecute (String token) {
                        GoogleUser googleUser = null;
                        GoogleSignInAccount acct = result.getSignInAccount ();

                        if(acct != null){
                            googleUser = new GoogleUser ();
                            googleUser.setID (acct.getId ());
                            googleUser.setDisplayName (acct.getDisplayName ());
                            googleUser.setFamilyName (acct.getFamilyName ());
                            googleUser.setEmail (acct.getEmail ());
                            googleUser.setGivenName (acct.getGivenName ());
                            googleUser.setIDToken (acct.getIdToken ());
                            googleUser.setPhotoUrl (acct.getPhotoUrl ().toString ());

                        }

                        mListener.onGoogleAuthSignIn (token, googleUser);
                    }
                };
                task.execute ();
            } else {
                mListener.onGoogleAuthSignInFailed (result.getStatus ().getStatusMessage ());
            }
        }
    }

    @Override
    public void onConnectionFailed (@NonNull ConnectionResult connectionResult) {
        mListener.onGoogleAuthSignInFailed (connectionResult.getErrorMessage ());
    }

    public void performSignOut () {
        if(mGoogleApiClient != null && !mGoogleApiClient.isConnected ()){
            mGoogleApiClient.connect ();
        }
        try{
            Auth.GoogleSignInApi.signOut (mGoogleApiClient).setResultCallback (new ResultCallback<Status> () {
                @Override
                public void onResult (@NonNull Status status) {
                    mListener.onGoogleAuthSignOut ();
                }
            });
        }catch(Exception ex){
            ex.printStackTrace ();
        }

    }

    @Override
    public void onConnected (@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended (int i) {

    }

}