package com.binarysoft.sociallogin.twitter;

public interface TwitterListener {
  void onTwitterError (String _errorMessage);

  void onTwitterSignIn (String _authToken, String _secret, long _userId);
}
