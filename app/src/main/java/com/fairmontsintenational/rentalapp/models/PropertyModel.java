package com.fairmontsintenational.rentalapp.models;

public class PropertyModel {
    private Integer id;
    private String slug,housing_unit_id,tenant_id,status,lease_document,balance,user_id,approved_by,house_unit,property,lease_start,termination_date;

    public PropertyModel(Integer id, String slug, String housing_unit_id, String tenant_id, String status,
                         String lease_document, String balance, String user_id, String approved_by, String house_unit, String property, String lease_start, String termination_date) {
        this.id = id;
        this.slug = slug;
        this.housing_unit_id = housing_unit_id;
        this.tenant_id = tenant_id;
        this.status = status;
        this.lease_document = lease_document;
        this.balance = balance;
        this.user_id = user_id;
        this.approved_by = approved_by;
        this.house_unit = house_unit;
        this.property = property;
        this.lease_start = lease_start;
        this.termination_date = termination_date;
    }

    public Integer getId() {
        return id;
    }

    public String getSlug() {
        return slug;
    }

    public String getHousing_unit_id() {
        return housing_unit_id;
    }

    public String getTenant_id() {
        return tenant_id;
    }

    public String getStatus() {
        return status;
    }

    public String getLease_document() {
        return lease_document;
    }

    public String getBalance() {
        return balance;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getApproved_by() {
        return approved_by;
    }

    public String getHouse_unit() {
        return house_unit;
    }

    public String getProperty() {
        return property;
    }

    public String getLease_start() {
        return lease_start;
    }

    public String getTermination_date() {
        return termination_date;
    }
}
