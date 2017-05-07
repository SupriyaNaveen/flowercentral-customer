package com.flowercentral.flowercentralcustomer.volley;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.toolbox.ImageLoader;

/**
 * Created by Ashish Upadhyay on 7/18/16.
 */
public class LRUBitmapCache extends LruCache<String, Bitmap>
        implements ImageLoader.ImageCache {


    public LRUBitmapCache (int maxSize) {
        super (maxSize);
    }

    public LRUBitmapCache () {
        this (getDefaultLruCacheSize ());
    }

    public static int getDefaultLruCacheSize () {
        return (int) (Runtime.getRuntime ().maxMemory () / 1024) / 8;
    }

    @Override
    protected int sizeOf (String key, Bitmap value) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB_MR2) {
            return value.getByteCount () / 1024;
        } else {
            return (value.getRowBytes () * value.getHeight ()) / 1024;
        }
    }

    @Override
    public Bitmap getBitmap (String url) {
        return get (url);
    }

    @Override
    public void putBitmap (String url, Bitmap bitmap) {
        put (url, bitmap);
    }
}
