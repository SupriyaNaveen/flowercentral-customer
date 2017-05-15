package com.binarysoft.sociallogin.instagram;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;
import org.json.JSONTokener;

public class InstagramHelper {
    private static final String TAG = InstagramHelper.class.getSimpleName ();

    private InstagramDialog mDialog;
    private InstagramListener mListener;

    private ProgressDialog mProgress;
    private String mAccessToken;
    private String mUserId;
    private String mClientId;
    private String mClientSecret;
    private InstagramUser mInstagramUser;

    private static int WHAT_ERROR = 1;
    private static int WHAT_FETCH_INFO = 2;

    public static String mCallbackUrl = "";
    private static final String AUTH_URL = "https://api.instagram.com/oauth/authorize/";
    private static final String TOKEN_URL = "https://api.instagram.com/oauth/access_token";
    private static final String API_URL = "https://api.instagram.com/v1";


    public InstagramHelper (@NonNull InstagramListener listeners, @NonNull Context context,
                            @NonNull String clientId, @NonNull String clientSecretKey, @NonNull String callbackUrl) {
        mListener = listeners;
        mClientId = clientId;
        mClientSecret = clientSecretKey;
        mCallbackUrl = callbackUrl;
        String authUrl = AUTH_URL
                + "?client_id="
                + mClientId
                + "&redirect_uri="
                + mCallbackUrl
                + "&response_type=code&display=touch&scope=likes+comments+relationships";

        mDialog = new InstagramDialog (context, authUrl, new InstagramDialog.OAuthDialogListener () {
            @Override
            public void onComplete (String code) {
                getAccessToken (code);
            }

            @Override
            public void onError (String error) {
                mListener.onInstagramSignInFail ("Authorization failed");
            }
        });

        mProgress = new ProgressDialog (context);
        mProgress.setCancelable (false);
    }

    public void performSignIn () {
        if (mAccessToken != null) {
            mAccessToken = null;
        } else {
            mDialog.show ();
        }
    }

    private void getAccessToken (final String response) {
        mProgress.setMessage ("Logging in...");
        mProgress.show ();

        new Thread () {
            @Override
            public void run () {
                Log.i (TAG, "Getting access token");
                int what = WHAT_FETCH_INFO;
                try {
                    URL url = new URL (TOKEN_URL);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection ();
                    urlConnection.setRequestMethod ("POST");
                    urlConnection.setDoInput (true);
                    urlConnection.setDoOutput (true);

                    OutputStreamWriter writer = new OutputStreamWriter (urlConnection.getOutputStream ());
                    writer.write ("client_id="
                            + mClientId
                            + "&client_secret="
                            + mClientSecret
                            + "&grant_type=authorization_code"
                            + "&redirect_uri="
                            + mCallbackUrl
                            + "&code="
                            + response);
                    writer.flush ();

                    String response = streamToString (urlConnection.getInputStream ());
                    Log.i (TAG, "response " + response);

                    JSONObject jsonObj = (JSONObject) new JSONTokener (response).nextValue ();

                    mAccessToken = jsonObj.getString ("access_token");
                    mUserId = jsonObj.getJSONObject ("user").getString ("id");

                    mInstagramUser = new InstagramUser ();
                    mInstagramUser.setID (mUserId);
                    mInstagramUser.setAccessToken (mAccessToken);
                    mInstagramUser.setFullName (jsonObj.getJSONObject ("user").getString ("full_name"));
                    mInstagramUser.setUserName (jsonObj.getJSONObject ("user").getString ("username"));
                    mInstagramUser.setProfilePicture (jsonObj.getJSONObject ("user").getString ("profile_picture"));

                } catch (Exception ex) {
                    what = WHAT_ERROR;
                    ex.printStackTrace ();
                }
                mHandler.sendMessage (mHandler.obtainMessage (what, 1, 0));
            }
        }.start ();
    }

    private Handler mHandler = new Handler () {
        @Override
        public void handleMessage (Message msg) {
            mProgress.dismiss ();

            if (msg.what == WHAT_ERROR) {
                if (msg.arg1 == 1) {
                    mListener.onInstagramSignInFail ("Failed to get access token");
                } else if (msg.arg1 == 2) {
                    mListener.onInstagramSignInFail ("Failed to get user information");
                }
            } else {
                mListener.onInstagramSignInSuccess (mAccessToken, mInstagramUser);
            }
        }
    };

    private String streamToString (InputStream is) throws IOException {
        String str = "";
        if (is != null) {
            StringBuilder sb = new StringBuilder ();
            String line;
            BufferedReader reader = null;
            try {
                reader = new BufferedReader (new InputStreamReader (is));
                while ((line = reader.readLine ()) != null) {
                    sb.append (line);
                }
            } catch (Exception e) {
                e.printStackTrace ();
            } finally {
                try {
                    is.close ();
                    if (reader != null) reader.close ();
                } catch (Exception e) {
                    e.printStackTrace ();
                }
            }
            str = sb.toString ();
        }
        return str;
    }
}