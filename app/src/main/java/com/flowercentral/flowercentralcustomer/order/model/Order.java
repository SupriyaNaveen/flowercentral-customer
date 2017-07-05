package com.flowercentral.flowercentralcustomer.order.model;

/**
 * Created by Ashish Upadhyay on 7/5/17.
 * This is used only to save the data into local database.
 * Mainly called whenever we call Order API and want to save the details into local database
 *
 */

public class Order {

    private String mOrderID;
    private String mOrder;
    private int mIsSynced;

    public Order(){

    }

    public String getOrderID () {
        return mOrderID;
    }

    public void setOrderID (String orderID) {
        mOrderID = orderID;
    }

    public String getOrder () {
        return mOrder;
    }

    public void setOrder (String order) {
        mOrder = order;
    }

    public int getIsSynced () {
        return mIsSynced;
    }

    public void setIsSynced (int isSynced) {
        mIsSynced = isSynced;
    }
}
