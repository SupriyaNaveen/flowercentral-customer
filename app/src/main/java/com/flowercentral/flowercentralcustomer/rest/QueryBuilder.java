package com.flowercentral.flowercentralcustomer.rest;


import com.flowercentral.flowercentralcustomer.BuildConfig;

/**
 * Created by Ashish Upadhyay on 11/20/16.
 */

public class QueryBuilder {

    private static String profileUpdateUrl;

    public static String getLoginUrl()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(BuildConfig.SERVER);
        sb.append("customer_login");
        return sb.toString();
    }

    public static String getRegistrationUrl()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(BuildConfig.SERVER);
        sb.append("customer_login");
        return sb.toString();
    }

    public static String getDeliveryAddressUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append(BuildConfig.SERVER);
        sb.append("delivery_address");
        return sb.toString();
    }

    public static String getCheckDeliveryAddressUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append(BuildConfig.SERVER);
        sb.append("checkdelivery_address");
        return sb.toString();
    }

    public static String getProfileUpdateUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append(BuildConfig.SERVER);
        sb.append("personal_info");
        return sb.toString();
    }
}
