package com.flowercentral.flowercentralcustomer.common.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by Ashish Upadhyay on 5/21/17.
 */

public class Product implements Parcelable {

    private String mTitle;
    private String mDescription;
    private String mImage;
    private double mPrice;
    private int mIsLiked;

    public Product () {

    }

    protected Product (Parcel in) {
        mTitle = in.readString ();
        mDescription = in.readString ();
        mImage = in.readString ();
        mPrice = in.readDouble ();
        mIsLiked = in.readInt ();

    }

    @Override
    public void writeToParcel (Parcel dest, int flags) {
        dest.writeString (mTitle);
        dest.writeString (mDescription);
        dest.writeString (mImage);
        dest.writeDouble (mPrice);
        dest.writeInt (mIsLiked);
    }

    @Override
    public int describeContents () {
        return 0;
    }

    public static final Creator<Product> CREATOR = new Creator<Product> () {
        @Override
        public Product createFromParcel (Parcel in) {
            return new Product (in);
        }

        @Override
        public Product[] newArray (int size) {
            return new Product[size];
        }
    };

    public String getTitle () {
        return mTitle;
    }

    public void setTitle (String _title) {
        mTitle = _title;
    }

    public String getDescription () {
        return mDescription;
    }

    public void setDescription (String _description) {
        mDescription = _description;
    }

    public String getImage () {
        return mImage;
    }

    public void setImage (String _image) {
        mImage = _image;
    }

    public double getPrice () {
        return mPrice;
    }

    public void setPrice (double _price) {
        mPrice = _price;
    }

    public int isLiked () {
        return mIsLiked;
    }

    public void setLiked (int _liked) {
        mIsLiked = _liked;
    }

}
