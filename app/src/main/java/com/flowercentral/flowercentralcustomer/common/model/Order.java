package com.flowercentral.flowercentralcustomer.common.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Ashish Upadhyay on 6/26/17.
 */

public class Order implements Parcelable {

    @SerializedName ("id")
    private String mOrderID;

    @SerializedName ("order_date")
    private String mOrderDate;

    @SerializedName ("address")
    private String mDeliveryAddress;

    @SerializedName ("delivered_at")
    private String mDeliveredAt;

    @SerializedName ("status")
    private String mStatus;

    @SerializedName ("product_details")
    private ArrayList<Product> mProducts;

    @SerializedName ("payment_status")
    private String mPaymentStatus;

    //@SerializedName ("order_total")
    //private double mOrderTotal;

    //@SerializedName ("latitude")
    //private Double mLatitude;

    //@SerializedName ("longitude")
    //private Double mLongitude;

    //@SerializedName ("scheduled_delivery")
    //private int mScheduledDelivery;

    //@SerializedName ("schedule_datetime")
    //private String mSchduleDateTime;

    public Order () {

    }

    protected Order (Parcel in) {
        mOrderID = in.readString ();
        mOrderDate = in.readString ();
        mDeliveryAddress = in.readString ();
        mDeliveredAt = in.readString ();
        mStatus = in.readString ();
        mPaymentStatus = in.readString ();
        mProducts = in.readArrayList (Product.class.getClassLoader ());

        //mOrderTotal = in.readDouble ();
        //mLatitude = in.readDouble ();
        //mLongitude = in.readDouble ();
        //mScheduledDelivery = in.readInt ();
        //mSchduleDateTime = in.readString ();

    }

    public static final Creator<Order> CREATOR = new Creator<Order> () {
        @Override
        public Order createFromParcel (Parcel in) {
            return new Order (in);
        }

        @Override
        public Order[] newArray (int size) {
            return new Order[size];
        }
    };

    @Override
    public int describeContents () {
        return 0;
    }

    @Override
    public void writeToParcel (Parcel dest, int flags) {
        dest.writeString (mOrderID);
        dest.writeString (mOrderDate);
        dest.writeString (mDeliveryAddress);
        dest.writeString (mDeliveredAt);
        dest.writeString (mStatus);
        dest.writeString (mPaymentStatus);
        dest.writeList (mProducts);

        //dest.writeDouble (mOrderTotal);
        //dest.writeDouble (mLatitude);
        //dest.writeDouble (mLongitude);
        //dest.writeInt (mScheduledDelivery);
        //dest.writeString (mSchduleDateTime);

    }

    public String getOrderID () {
        return mOrderID;
    }

    public void setOrderID (String orderID) {
        mOrderID = orderID;
    }

    public String getOrderDate () {
        return mOrderDate;
    }

    public void setOrderDate (String _orderDate) {
        mOrderDate = _orderDate;
    }

    public String getDeliveryAddress () {
        return mDeliveryAddress;
    }

    public void setDeliveryAddress (String _deliveryAddress) {
        mDeliveryAddress = _deliveryAddress;
    }

    public String getDeliveredAt () {
        return mDeliveredAt;
    }

    public void setDeliveredAt (String _deliveredAt) {
        mDeliveredAt = _deliveredAt;
    }

    public String getStatus () {
        return mStatus;
    }

    public void setStatus (String _status) {
        mStatus = _status;
    }

    public ArrayList<Product> getProducts () {
        return mProducts;
    }

    public void setProducts (ArrayList<Product> _products) {
        mProducts = _products;
    }

    public void setProducts (JSONArray _products) {
        if(_products != null){
            Product product = null;

            if(mProducts == null){
                mProducts = new ArrayList<Product> ();
            }
            try{
                for(int i=0; i<_products.length (); i++){
                    JSONObject object = _products.getJSONObject (i);
                    product = new Product ();
                    product.setTitle (object.getString ("title"));
                    product.setCategory (object.getString ("category"));
                    product.setImage (object.getString ("img_url"));
                    product.setPrice (Double.valueOf (object.getString ("price")));

                    mProducts.add (product);
                }

            }catch (JSONException jsEx){
                jsEx.printStackTrace ();

            }catch (Exception ex){
                ex.printStackTrace ();

            }
        }
    }

    public String getPaymentStatus () {
        return mPaymentStatus;
    }

    public void setPaymentStatus (String paymentStatus) {
        mPaymentStatus = paymentStatus;
    }

    /*public double getOrderTotal () {
        return mOrderTotal;
    }

    public void setOrderTotal (double _orderTotal) {
        mOrderTotal = _orderTotal;
    }

    public Double getLatitude () {
        return mLatitude;
    }

    public void setLatitude (Double _latitude) {
        mLatitude = _latitude;
    }

    public Double getLongitude () {
        return mLongitude;
    }

    public void setLongitude (Double _longitude) {
        mLongitude = _longitude;
    }

    public int getScheduledDelivery () {
        return mScheduledDelivery;
    }

    public void setScheduledDelivery (int _scheduledDelivery) {
        mScheduledDelivery = _scheduledDelivery;
    }

    public String getSchduleDateTime () {
        return mSchduleDateTime;
    }

    public void setSchduleDateTime (String _schduleDateTime) {
        mSchduleDateTime = _schduleDateTime;
    }

    */

    public class Product {
        private String mTitle;
        private String mCategory;
        private Double mPrice;
        private String mImage;

        Product(){

        }

        protected Product (Parcel in) {
            mTitle = in.readString ();
            mCategory = in.readString ();
            mImage = in.readString ();
        }

        public String getTitle () {
            return mTitle;
        }

        public void setTitle (String title) {
            mTitle = title;
        }

        public String getCategory () {
            return mCategory;
        }

        public void setCategory (String category) {
            mCategory = category;
        }

        public Double getPrice () {
            return mPrice;
        }

        public void setPrice (Double price) {
            mPrice = price;
        }

        public String getImage () {
            return mImage;
        }

        public void setImage (String image) {
            mImage = image;
        }
    }
}


