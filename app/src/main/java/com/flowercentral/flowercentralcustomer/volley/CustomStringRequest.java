package com.flowercentral.flowercentralcustomer.volley;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.Map;

/**
 * Created by Ashish Upadhyay on 7/19/16.
 */
public class CustomStringRequest extends StringRequest {

    private HttpResponseListener responseListener;
    private NetworkResponse networkResponse;

    public CustomStringRequest(int method, String url, Response.Listener listener, Response.ErrorListener errorListener)
    {
        super(method,url,listener,errorListener);
    }

    public void appendParams(Map<String,String> map)
    {
        try {
            if(getParams()!=null) {
                getParams().putAll(map);
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void setCustomResponseListener(HttpResponseListener responseListener)
    {
        this.responseListener = responseListener;
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        networkResponse = response;
        return super.parseNetworkResponse(response);

    }


    @Override
    protected void deliverResponse(String response) {
        super.deliverResponse(response);
        if(responseListener!=null&&networkResponse!=null)
        {
            responseListener.onSuccess(networkResponse.statusCode,networkResponse.headers,response);
        }

    }
}
