package com.flowercentral.flowercentralcustomer.rest;


import com.flowercentral.flowercentralcustomer.BuildConfig;

/**
 * Created by Ashish Upadhyay on 11/20/16.
 */

public class QueryBuilder {

    private static String profileUpdateUrl;
    private static String cartItemUrl;

    public static String getLoginUrl()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(BuildConfig.SERVER);
        sb.append("login");
        return sb.toString();
    }

    public static String getRegistrationUrl()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(BuildConfig.SERVER);
        sb.append("customer_login");
        return sb.toString();
    }

    public static String getProducts()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(BuildConfig.SERVER);
        sb.append("products");
        return sb.toString();
    }

    public static String getDeliveryAddressUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append(BuildConfig.SERVER);
        sb.append("deliveryaddress");
        return sb.toString();
    }

    public static String getCheckDeliveryAddressUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append(BuildConfig.SERVER);
        sb.append("checkdeliveryaddress");
        return sb.toString();
    }

    public static String getProfileUpdateUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append(BuildConfig.SERVER);
        sb.append("personalinformation");
        return sb.toString();
    }

    public static String getSubmitOrderUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append(BuildConfig.SERVER);
        sb.append("submitOrder");
        return sb.toString();
    }

    public static String getCartItemUrl () {
        StringBuilder sb = new StringBuilder();
        sb.append(BuildConfig.SERVER);
        sb.append("shoppingcart");
        return sb.toString();

    }

    public static String getPreviousOrderUrl () {
        StringBuilder sb = new StringBuilder ();
        sb.append(BuildConfig.SERVER);
        sb.append ("previousorders");
        return sb.toString ();
    }
}
