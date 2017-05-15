package com.binarysoft.sociallogin.facebook;

import org.json.JSONObject;

/**
 * Created by Ashish Upadhyay on 5/13/17.
 */

public interface FacebookGraphListner {
    public void onProfileReceived(FacebookUser _profile);
}
