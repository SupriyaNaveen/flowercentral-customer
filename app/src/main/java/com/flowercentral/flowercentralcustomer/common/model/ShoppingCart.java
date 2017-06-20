package com.flowercentral.flowercentralcustomer.common.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Ashish Upadhyay on 6/19/17.
 */

public class ShoppingCart implements Parcelable {

    private int mProductID;
    private String mProductName;
    private String mProductCategory;
    private int mProductQuantity;
    private double mProductPrice;
    private int mShoppingCartQuantity;
    private String mUserMessage;
    private int mStatus;

    private ArrayList<ShoppingCart> mCartItems;

    public ShoppingCart(){
        mCartItems = new ArrayList<ShoppingCart> ();
    }

    protected ShoppingCart (Parcel in) {
        mProductID = in.readInt ();
        mProductName = in.readString ();
        mProductCategory = in.readString ();
        mProductQuantity = in.readInt ();
        mProductPrice = in.readDouble ();
        mShoppingCartQuantity = in.readInt ();
        mUserMessage = in.readString ();
        mStatus = in.readInt ();
        mCartItems = in.readArrayList (ShoppingCart.class.getClassLoader ());
    }

    @Override
    public void writeToParcel (Parcel dest, int flags) {
        dest.writeInt (mProductID);
        dest.writeString (mProductName);
        dest.writeString (mProductCategory);
        dest.writeInt (mProductQuantity);
        dest.writeDouble (mProductPrice);
        dest.writeInt (mShoppingCartQuantity);
        dest.writeString (mUserMessage);
        dest.writeInt (mStatus);
        dest.writeList (mCartItems);

    }

    @Override
    public int describeContents () {
        return 0;
    }

    public static final Creator<ShoppingCart> CREATOR = new Creator<ShoppingCart> () {
        @Override
        public ShoppingCart createFromParcel (Parcel in) {
            return new ShoppingCart (in);
        }

        @Override
        public ShoppingCart[] newArray (int size) {
            return new ShoppingCart[size];
        }
    };

    public int getProductID () {
        return mProductID;
    }

    public void setProductID (int productID) {
        mProductID = productID;
    }

    public String getProductName () {
        return mProductName;
    }

    public void setProductName (String productName) {
        mProductName = productName;
    }

    public String getProductCategory () {
        return mProductCategory;
    }

    public void setProductCategory (String productCategory) {
        mProductCategory = productCategory;
    }

    public int getProductQuantity () {
        return mProductQuantity;
    }

    public void setProductQuantity (int productQuantity) {
        mProductQuantity = productQuantity;
    }

    public double getProductPrice () {
        return mProductPrice;
    }

    public void setProductPrice (double productPrice) {
        mProductPrice = productPrice;
    }

    public int getShoppingCartQuantity () {
        return mShoppingCartQuantity;
    }

    public void setShoppingCartQuantity (int shoppingCartQuantity) {
        mShoppingCartQuantity = shoppingCartQuantity;
    }

    public String getUserMessage () {
        return mUserMessage;
    }

    public void setUserMessage (String userMessage) {
        mUserMessage = userMessage;
    }

    public int getStatus () {
        return mStatus;
    }

    public void setStatus (int status) {
        mStatus = status;
    }

    public ArrayList<ShoppingCart> getCartItems () {
        return mCartItems;
    }

    public boolean AddToCart(ShoppingCart _cartItem){
        boolean status = false;
        if(mCartItems == null){
            mCartItems = new ArrayList<ShoppingCart> ();
        }

        boolean itemExists = checkItemExists(_cartItem);

        if(itemExists){
            // Add quantity into existing product's quantity
            updateQuantity(_cartItem);
            status = true;
        }else{
            //Add Product into cart
            mCartItems.add (_cartItem);
            status = true;

        }

        return status;
    }

    private void updateQuantity (ShoppingCart _cartItem) {
        if(_cartItem != null && mCartItems != null){
            for(int i=0; i<mCartItems.size (); i++){
                if(mCartItems.get (i).getProductID () == _cartItem.getProductID ()){
                    if(_cartItem.getShoppingCartQuantity () > 0){
                        int qty = mCartItems.get (i).getShoppingCartQuantity () + _cartItem.getShoppingCartQuantity ();
                        mCartItems.get (i).setShoppingCartQuantity (qty);
                        break;
                    }
                }
            }
        }
    }

    private boolean checkItemExists (ShoppingCart _cartItem) {
        boolean isExists = false;
        if(mCartItems == null){
            return isExists;
        }
        if(_cartItem == null){
            return isExists;
        }
        for(ShoppingCart cart : mCartItems){
            if(cart.getProductID () == _cartItem.getProductID ()){
                isExists = true;
                break;
            }
        }
        return isExists;
    }

    public boolean removeCartItem(int _productID){
        boolean isDeleted = false;

        if(mCartItems == null){
            return isDeleted;
        }


        for(int i = 0; i < mCartItems.size (); i++){
            if(mCartItems.get (i).getProductID () == _productID){
                mCartItems.remove (i);
                isDeleted = true;
                break;
            }
        }

        return isDeleted;
    }

    public boolean removeCartItems(){
        boolean isDeleted = false;
        if(mCartItems != null){
            mCartItems.clear ();
            isDeleted = true;
        }
        return isDeleted;
    }
}
