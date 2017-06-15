package com.flowercentral.flowercentralcustomer.common.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by Ashish Upadhyay on 5/21/17.
 */

public class Product implements Parcelable, Comparable<Product>{

    //This is used for actual product id 

    private int mID;

    @SerializedName ("id")
    private String mProductID;

    @SerializedName ("flower")
    private String mFlower;

    @SerializedName ("description")
    private String mDescription;

    @SerializedName ("image")
    private String mImage;

    @SerializedName ("category")
    private String mCategory;

    @SerializedName ("price")
    private double mPrice;

    @SerializedName ("quantity")
    private int mQty;

    @SerializedName ("liked")
    private int mIsLiked;

    @SerializedName ("images")
    private ArrayList<String> mRelatedImages;

    @SerializedName ("tag")
    private ArrayList<String> mTags;

    public Product () {

    }

    protected Product (Parcel in) {
        mID = in.readInt ();
        mProductID = in.readString ();
        mFlower = in.readString ();
        mDescription = in.readString ();
        mImage = in.readString ();
        mCategory = in.readString ();
        mPrice = in.readDouble ();
        mQty = in.readInt ();
        mIsLiked = in.readInt ();
        mRelatedImages = in.readArrayList (String.class.getClassLoader ());
        mTags = in.readArrayList (String.class.getClassLoader ());

    }

    @Override
    public void writeToParcel (Parcel dest, int flags) {
        dest.writeInt (mID);
        dest.writeString (mProductID);
        dest.writeString (mFlower);
        dest.writeString (mDescription);
        dest.writeString (mImage);
        dest.writeString (mCategory);
        dest.writeDouble (mPrice);
        dest.writeInt (mQty);
        dest.writeInt (mIsLiked);
        dest.writeList (mRelatedImages);
        dest.writeList (mTags);
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

    public String getFlowerName () {
        return mFlower;
    }

    public void setFlowerName (String _title) {
        mFlower = _title;
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

    public ArrayList<String> getRelatedImages () {
        return mRelatedImages;
    }

    public void setRelatedImages (ArrayList<String> relatedImages) {
        this.mRelatedImages = relatedImages;
    }

    public void addRelatedImage(String _imageUrl){
        if(mRelatedImages == null){
            mRelatedImages = new ArrayList<String> ();
        }
        mRelatedImages.add (_imageUrl);
    }

    public int getID () {
        return mID;
    }

    public void setID (int ID) {
        mID = ID;
    }

    public int getQty () {
        return mQty;
    }

    public void setQty (int qty) {
        mQty = qty;
    }

    public String getCategory () {
        return mCategory;
    }

    public void setCategory (String category) {
        mCategory = category;
    }

    public ArrayList<String> getTags () {
        return mTags;
    }

    public void setTags (ArrayList<String> tags) {
        this.mRelatedImages = tags;
    }

    public void addTag(String tag){
        if(mTags == null){
            mTags = new ArrayList<String> ();
        }
        mTags.add (tag);
    }

    public String getProductID(){
        return mProductID;
    }

    public void setProductID(String _productID){
        mProductID = _productID;
    }


    @Override
    public String toString () {
        return "[ ID: "+String.valueOf (mID)+", Flower Name: "+mFlower+", Price: "+String.valueOf (mPrice)+" ]";
    }

    @Override
    public int compareTo (@NonNull Product _product) {
        //Sort by id ascending
        int id = this.mID - _product.mID;
        return id;
    }

    //Sort by flower name in ascending order
    public static Comparator<Product> sortByName = new Comparator<Product> () {
        @Override
        public int compare (Product p1, Product p2) {
            int id = 0;
            try{
                p1.getFlowerName ().compareToIgnoreCase (p2.getFlowerName ());
            }catch(Exception ex){
                id = 0;
            }

            return id;
        }
    };

    //Sort by flower price in ascending order
    public static Comparator<Product> sortByPrice = new Comparator<Product> () {
        @Override
        public int compare (Product p1, Product p2) {
            int id = 0;
            try{
                id = (int) (p1.getPrice () - p2.getPrice ());
            }catch(Exception ex){
                id = 0;
                ex.printStackTrace ();
            }
            return id;
        }
    };

    //Sort by flower category in ascending order
    public static Comparator<Product> sortByCategory = new Comparator<Product> () {
        @Override
        public int compare (Product p1, Product p2) {
            int id = 0;
            try{
                id = p1.getCategory ().compareToIgnoreCase (p2.getCategory ());
            }catch (Exception ex){
                id = 0;
                ex.printStackTrace ();
            }
            return id;
        }
    };

}
