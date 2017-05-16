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
        sb.append("customerApi/v1/login");
        return sb.toString();
    }

    public static String getRegistrationUrl()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(BuildConfig.SERVER);
        sb.append("customerApi/v1/register");
        return sb.toString();
    }

}