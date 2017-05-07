package com.flowercentral.flowercentralcustomer.volley;

import java.util.Map;

/**
 * Created by Ashish Upadhyay on 7/18/16.
 */
public interface HttpResponseListener<T> {

    void onSuccess (int statusCode, Map<String, String> headers, T response);
    void onError (ErrorData error);
}
