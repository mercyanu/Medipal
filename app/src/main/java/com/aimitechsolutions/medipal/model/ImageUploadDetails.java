package com.aimitechsolutions.medipal.model;

public class ImageUploadDetails {

    String mDate;
    String mUrl;
    String mDescription;

    public ImageUploadDetails(){
    }

    public ImageUploadDetails(String date, String url, String description){
        this.mDate = date;
        this.mUrl = url;
        this.mDescription = description;
    }

    public String getDate(){
        return mDate;
    }
    public void setDate(String date){
        this.mDate = date;
    }
    public String getUrl(){
        return mUrl;
    }
    public void setUrl(String url){
        this.mUrl = url;
    }
    public String getDescription(){
        return mDescription;
    }
    public void setDescription(String  desc){
        this.mDescription = desc;
    }
}
