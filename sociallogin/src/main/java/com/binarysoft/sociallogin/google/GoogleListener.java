package com.binarysoft.sociallogin.google;

import java.util.HashMap;

public interface GoogleListener {
    void onGoogleAuthSignIn (String _authToken, GoogleUser _userAccount);

    void onGoogleAuthSignInFailed (String _errorMessage);

    void onGoogleAuthSignOut ();
}
