package com.flowercentral.flowercentralcustomer.common.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Ashish Upadhyay on 6/26/17.
 */

public class Order implements Parcelable {

    private String mOrderID;

    @SerializedName ("order_date")
    private String mOrderDate;

    @SerializedName ("order_total")
    private double mOrderTotal;

    @SerializedName ("delivery_address")
    private String mDeliveryAddress;

    @SerializedName ("delivered_at")
    private String mDeliveredAt;

    @SerializedName ("latitude")
    private Double mLatitude;

    @SerializedName ("longitude")
    private Double mLongitude;

    @SerializedName ("scheduled_delivery")
    private int mScheduledDelivery;

    @SerializedName ("schedule_datetime")
    private String mSchduleDateTime;

    @SerializedName ("status")
    private String mStatus;

    @SerializedName ("order_date")
    private ArrayList<Product> mProducts;


    public Order () {

    }

    protected Order (Parcel in) {
        mOrderID = in.readString ();
        mOrderDate = in.readString ();
        mOrderTotal = in.readDouble ();
        mDeliveryAddress = in.readString ();
        mDeliveredAt = in.readString ();
        mLatitude = in.readDouble ();
        mLongitude = in.readDouble ();
        mScheduledDelivery = in.readInt ();
        mSchduleDateTime = in.readString ();
        mStatus = in.readString ();
        mProducts = in.readArrayList (Product.class.getClassLoader ());
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
        dest.writeDouble (mOrderTotal);
        dest.writeString (mDeliveryAddress);
        dest.writeString (mDeliveredAt);
        dest.writeDouble (mLatitude);
        dest.writeDouble (mLongitude);
        dest.writeInt (mScheduledDelivery);
        dest.writeString (mSchduleDateTime);
        dest.writeString (mStatus);
        dest.writeList (mProducts);

    }

    public String getOrderDate () {
        return mOrderDate;
    }

    public void setOrderDate (String _orderDate) {
        mOrderDate = _orderDate;
    }

    public double getOrderTotal () {
        return mOrderTotal;
    }

    public void setOrderTotal (double _orderTotal) {
        mOrderTotal = _orderTotal;
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

    public String getOrderID () {
        return mOrderID;
    }

    public void setOrderID (String orderID) {
        mOrderID = orderID;
    }
}
