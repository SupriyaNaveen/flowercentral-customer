package com.flowercentral.flowercentralcustomer.rest;


import com.flowercentral.flowercentralcustomer.volley.ErrorData;

/**
 * Created by Ashish Upadhyay on 7/19/16.
 */
public interface ResponseListener<O> {

    public void onDataReceived (O data);
    public void onDataError (ErrorData errorData);

}
