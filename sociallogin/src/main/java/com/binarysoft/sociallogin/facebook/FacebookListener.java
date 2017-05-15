package com.binarysoft.sociallogin.facebook;

public interface FacebookListener {
  void onFbSignInFail (String _errorMessage);

  void onFbSignInSuccess (String _authToken, String _userId);

  void onFBSignOut ();

}
