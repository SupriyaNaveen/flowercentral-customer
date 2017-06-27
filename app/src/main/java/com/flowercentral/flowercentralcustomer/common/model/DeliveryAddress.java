package com.flowercentral.flowercentralcustomer.common.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.renderscript.Double2;

import com.flowercentral.flowercentralcustomer.preference.UserPreference;

/**
 * Created by Ashish Upadhyay on 6/25/17.
 */

public class DeliveryAddress implements Parcelable {

    private String mCustomerName;
    private String mAddress;
    private String mCity;
    private String mState;
    private String mZip;
    private String mPrimaryPhone;
    private String mAlternatePhone;
    private Double mLongitude;
    private Double mLatitude;

    protected DeliveryAddress () {

    }

    protected DeliveryAddress (Parcel in) {

        mCustomerName = in.readString ();
        mAddress = in.readString ();
        mCity = in.readString ();
        mState = in.readString ();
        mZip = in.readString ();
        mPrimaryPhone = in.readString ();
        mAlternatePhone = in.readString ();
        mLongitude = in.readDouble ();
        mLatitude = in.readDouble ();

    }

    @Override
    public void writeToParcel (Parcel dest, int flags) {
        dest.writeString (mCustomerName);

    }

    @Override
    public int describeContents () {
        return 0;
    }

    public static final Creator<DeliveryAddress> CREATOR = new Creator<DeliveryAddress> () {
        @Override
        public DeliveryAddress createFromParcel (Parcel in) {
            return new DeliveryAddress (in);
        }

        @Override
        public DeliveryAddress[] newArray (int size) {
            return new DeliveryAddress[size];
        }
    };

    public String getCustomerName () {
        return mCustomerName;
    }

    public void setCustomerName (String _customerName) {
        mCustomerName = _customerName;
    }

    public String getAddress () {
        return mAddress;
    }

    public void setAddress (String _address) {
        mAddress = _address;
    }

    public String getCity () {
        return mCity;
    }

    public void setCity (String _city) {
        mCity = _city;
    }

    public String getState () {
        return mState;
    }

    public void setState (String _state) {
        mState = _state;
    }

    public String getZip () {
        return mZip;
    }

    public void setZip (String _zip) {
        mZip = _zip;
    }

    public String getPrimaryPhone () {
        return mPrimaryPhone;
    }

    public void setPrimaryPhone (String _primaryPhone) {
        mPrimaryPhone = _primaryPhone;
    }

    public String getAlternatePhone () {
        return mAlternatePhone;
    }

    public void setAlternatePhone (String _alternatePhone) {
        mAlternatePhone = _alternatePhone;
    }

    public Double getLongitude () {
        return mLongitude;
    }

    public void setLongitude (Double _longitude) {
        mLongitude = _longitude;
    }

    public Double getLatitude () {
        return mLatitude;
    }

    public void setLatitude (Double _latitude) {
        mLatitude = _latitude;
    }

}
