package com.carpool.tagalong.managers;

import com.carpool.tagalong.models.ModelSearchRideRequest;
import com.carpool.tagalong.models.ModelSearchRideResponseData;
import com.carpool.tagalong.models.ModelUserProfileData;

import java.util.List;

public class DataManager {

    public static boolean isDocumentUploaded = false;

    public static ModelUserProfileData modelUserProfileData = null;

    public static List<ModelSearchRideResponseData> modelSearchRideResponseDataLIst = null;

    public static ModelSearchRideResponseData modelSearchRideResponseData = null;

    public static ModelSearchRideRequest modelSearchRideRequest = null;
    public static int status = 0;
    private  static List<Integer> yearList = null;
    private  static List<String> colorList = null;

    public static boolean isIsDocumentUploaded() {
        return isDocumentUploaded;
    }

    public static void setIsDocumentUploaded(boolean isDocumentUploaded) {
        DataManager.isDocumentUploaded = isDocumentUploaded;
    }

    public static ModelUserProfileData getModelUserProfileData() {
        return modelUserProfileData;
    }

    public static void setModelUserProfileData(ModelUserProfileData modelUserProfileData) {
        DataManager.modelUserProfileData = modelUserProfileData;
    }

    public static List<ModelSearchRideResponseData> getModelSearchRideResponseDataLIst() {
        return modelSearchRideResponseDataLIst;
    }

    public static void setModelSearchRideResponseDataLIst(List<ModelSearchRideResponseData> modelSearchRideResponseDataLIst) {
        DataManager.modelSearchRideResponseDataLIst = modelSearchRideResponseDataLIst;
    }

    public static ModelSearchRideResponseData getModelSearchRideResponseData() {
        return modelSearchRideResponseData;
    }

    public static void setModelSearchRideResponseData(ModelSearchRideResponseData modelSearchRideResponseData) {
        DataManager.modelSearchRideResponseData = modelSearchRideResponseData;
    }

    public static ModelSearchRideRequest getModelSearchRideRequest() {
        return modelSearchRideRequest;
    }

    public static void setModelSearchRideRequest(ModelSearchRideRequest modelSearchRideRequest) {
        DataManager.modelSearchRideRequest = modelSearchRideRequest;
    }

    public static int getStatus() {
        return status;
    }

    public static void setStatus(int status) {
        DataManager.status = status;
    }

    public static void setYearsList(List<Integer> data) {

        yearList = data;
    }

    public static List<Integer> getYearsList() {

        return  yearList ;
    }

    public static void setColorList(List<String> colordata) {
        colorList = colordata;
    }

    public static List<String> getColorList() {

        return  colorList ;
    }
}