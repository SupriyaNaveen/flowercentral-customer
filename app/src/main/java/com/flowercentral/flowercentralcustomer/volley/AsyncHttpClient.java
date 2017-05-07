package com.flowercentral.flowercentralcustomer.volley;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;


/**
 * Created by Ashish Upadhyay on 7/18/16.
 */
public class AsyncHttpClient {

    public static final String TAG = AsyncHttpClient.class.getSimpleName();
    private static AsyncHttpClient mInstance;
    private static Context mContext;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;


    public static synchronized AsyncHttpClient getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new AsyncHttpClient(context.getApplicationContext());
        }
        return mInstance;
    }

    private AsyncHttpClient(Context context) {
        mContext = context;
        mRequestQueue = getRequestQueue();
        //mImageLoader = new ImageLoader(mRequestQueue, new LruBitmapCache(context));
        mImageLoader = new ImageLoader(mRequestQueue, new LRUBitmapCache());
    }

    /**
     * Creates a RequestQueue from valley if RequestQueue doesn't doesn't exists
     * else returns the current RequestQueue Object.
     *
     * @return
     */
    private RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            Cache cache = new DiskBasedCache(mContext.getCacheDir(), 10 * 1024 * 1024); //10MB request cache
            Network network = new BasicNetwork(new HurlStack());
            mRequestQueue = new RequestQueue(cache, network);
            // Don't forget to start the volley request queue
            mRequestQueue.start();
        }
        return mRequestQueue;
    }


    public void addToRequestQueue(Request request, @Nullable String tag) {
        // sets the default tag if tag is empty
        request.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(request);
    }

    public void addToRequestQueue(Request request) {
        // sets the default tag if tag is empty
        if(request.getTag()==null)
        {
            request.setTag(TAG);
        }
        getRequestQueue().add(request);
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

  /*  public void addToRequestQueue(StringRequest request,@Nullable String tag) {
        // sets the default tag if tag is empty
        request.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(request);
    }

    public void addToRequestQueue(JsonArrayRequest request,@Nullable String tag) {
        // sets the default tag if tag is empty
        request.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(request);
    }

    public void addToRequestQueue(JsonObjectRequest request, @Nullable String tag) {
        // sets the default tag if tag is empty
        request.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(request);
    }*/

    /*public void addToRequestQueue(CustomGsonRequest request,@Nullable String tag) {
        // sets the default tag if tag is empty
        request.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(request.getGsonRequest());
    }*/

    public void cancelAllRequestsByTag(String tag) {
        //cancels the requests with default tag if tag is null
        getRequestQueue().cancelAll(TextUtils.isEmpty(tag) ? TAG : tag);

    }

    public void cancelAllRequests() {
        getRequestQueue().cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return true;
            }
        });

    }





}
