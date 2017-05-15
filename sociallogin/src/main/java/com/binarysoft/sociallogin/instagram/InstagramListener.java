package com.binarysoft.sociallogin.instagram;

public interface InstagramListener {

  void onInstagramSignInFail (String _errorMessage);

  void onInstagramSignInSuccess (String _authToken, InstagramUser _instagramUser);
}
