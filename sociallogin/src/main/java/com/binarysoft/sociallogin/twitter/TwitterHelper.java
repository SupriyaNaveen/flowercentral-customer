package com.binarysoft.sociallogin.twitter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import io.fabric.sdk.android.Fabric;

public class TwitterHelper {
  private TwitterAuthClient mAuthClient;

  @NonNull private final Activity mActivity;

  @NonNull private final TwitterListener mListener;
  @NonNull private final String mTwitterApiKey;
  @NonNull private final String mTwitterSecreteKey;

  public TwitterHelper(@NonNull TwitterListener response, @NonNull Activity context,
      @NonNull String twitterApiKey, @NonNull String twitterSecreteKey) {
    mActivity = context;
    mListener = response;
    mTwitterApiKey = twitterApiKey;
    mTwitterSecreteKey = twitterSecreteKey;
    mAuthClient = new TwitterAuthClient();
  }

  private Callback<TwitterSession> mCallback = new Callback<TwitterSession>() {
    @Override public void success(Result<TwitterSession> result) {
      TwitterSession session = result.data;
      mListener.onTwitterSignIn(session.getAuthToken().token, session.getAuthToken().secret,
          session.getUserId());
    }

    @Override public void failure(TwitterException exception) {
      mListener.onTwitterError(exception.getMessage());
    }
  };

  public void performSignIn(Context context) {
    TwitterAuthConfig authConfig = new TwitterAuthConfig(mTwitterApiKey, mTwitterSecreteKey);
    Fabric.with(context, new Twitter(authConfig));
    mAuthClient.authorize(mActivity, mCallback);
  }

  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (mAuthClient != null) mAuthClient.onActivityResult(requestCode, resultCode, data);
  }

}