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
        sb.append("patientApi/v1/login");
        return sb.toString();
    }

    public static String getCareGiversUrl()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(BuildConfig.SERVER);
        sb.append("patientApi/v1/careGivers");
        return sb.toString();
    }

    public static String getCareTeamUrl()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(BuildConfig.SERVER);
        sb.append("patientApi/v1/careTeam");
        return sb.toString();
    }

    public static String getQuestionsUrl()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(BuildConfig.SERVER);
        sb.append("patientApi/v1/questions");
        return sb.toString();
    }

    public static String getProfileUrl()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(BuildConfig.SERVER);
        sb.append("patientApi/v1/profile");
        return sb.toString();
    }

    public static String getReportProblemUrl()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(BuildConfig.SERVER);
        sb.append("patientApi/v1/reportProblem");
        return sb.toString();
    }

    public static String getPostOperationUrl()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(BuildConfig.SERVER);
        sb.append("patientApi/v1/postOpInstructions");
        return sb.toString();
    }


    public static String getSubmitAnswerUrl()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(BuildConfig.SERVER);
        sb.append("patientApi/v1/submitAnswer");
        return sb.toString();
    }

    public static String getProfileUpdateUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append(BuildConfig.SERVER);
        sb.append("patientApi/v1/updateProfile");
        return sb.toString();
    }

    public static String getLayerToken() {
        StringBuilder sb = new StringBuilder();
        sb.append(BuildConfig.SERVER);
        sb.append("patientApi/v1/getLayerToken");
        return sb.toString();

    }

    public static String getUpdateCareGiverStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append(BuildConfig.SERVER);
        sb.append("patientApi/v1/updateCareGiverStatus");
        return sb.toString();
    }

    public static String getRegisterFCMToken() {
        StringBuilder sb = new StringBuilder();
        sb.append(BuildConfig.SERVER);
        sb.append("patientApi/v1/registerFcmToken");
        return sb.toString();
    }

    public static String getAddCareGiverUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append(BuildConfig.SERVER);
        sb.append("patientApi/v1/addCareGiver");
        return sb.toString();
    }
}
