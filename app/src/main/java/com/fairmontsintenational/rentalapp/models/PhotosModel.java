package com.fairmontsintenational.rentalapp.models;

public class PhotosModel {
    private String url,desc;

    public PhotosModel(String url, String desc) {
        this.url = url;
        this.desc = desc;
    }

    public String getUrl() {
        return url;
    }

    public String getDesc() {
        return desc;
    }
}
