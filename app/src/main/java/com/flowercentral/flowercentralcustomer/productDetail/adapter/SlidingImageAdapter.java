package com.flowercentral.flowercentralcustomer.productDetail.adapter;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.flowercentral.flowercentralcustomer.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Ashish Upadhyay on 5/25/17.
 */

public class SlidingImageAdapter extends PagerAdapter {
    private ArrayList<String> mImages;
    private LayoutInflater inflater;
    private Context mContext;


    public SlidingImageAdapter (Context context, ArrayList<String> _images) {
        this.mContext = context;
        this.mImages = _images;
        inflater = LayoutInflater.from (context);
    }

    @Override
    public void destroyItem (ViewGroup container, int position, Object object) {
        container.removeView ((View) object);
    }

    @Override
    public int getCount () {
        int size = 0;
        if (mImages != null) {
            size = mImages.size ();
        }
        return size;
    }

    @Override
    public Object instantiateItem (ViewGroup view, int position) {
        View imageLayout = inflater.inflate (R.layout.layout_sliding_image, view, false);

        assert imageLayout != null;
        final ImageView imageView = (ImageView) imageLayout
                .findViewById (R.id.image);

        Picasso.with(mContext).load(mImages.get (position)).into(imageView);

        view.addView (imageLayout, 0);

        return imageLayout;
    }

    @Override
    public boolean isViewFromObject (View view, Object object) {
        return view.equals (object);
    }

    @Override
    public void restoreState (Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState () {
        return null;
    }


}
