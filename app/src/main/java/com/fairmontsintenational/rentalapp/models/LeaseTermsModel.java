package com.fairmontsintenational.rentalapp.models;

public class LeaseTermsModel {
    private String title,description;

    public LeaseTermsModel(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
