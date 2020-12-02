package com.fairmontsintenational.rentalapp.models;

import java.util.List;

public class TransactionModel {

    private  int id;
    private String slug,code,payment_mode_id,payment_mode,amount,lease_id,house_unit,property,
    comment,status,status_name,created_at;

    public TransactionModel(int id, String slug, String code, String payment_mode_id, String payment_mode, String amount,
                            String lease_id, String house_unit, String property, String comment, String status, String status_name, String created_at) {
        this.id = id;
        this.slug = slug;
        this.code = code;
        this.payment_mode_id = payment_mode_id;
        this.payment_mode = payment_mode;
        this.amount = amount;
        this.lease_id = lease_id;
        this.house_unit = house_unit;
        this.property = property;
        this.comment = comment;
        this.status = status;
        this.status_name = status_name;
        this.created_at = created_at;
    }

    public int getId() {
        return id;
    }

    public String getSlug() {
        return slug;
    }

    public String getCode() {
        return code;
    }

    public String getPayment_mode_id() {
        return payment_mode_id;
    }

    public String getPayment_mode() {
        return payment_mode;
    }

    public String getAmount() {
        return amount;
    }

    public String getLease_id() {
        return lease_id;
    }

    public String getHouse_unit() {
        return house_unit;
    }

    public String getProperty() {
        return property;
    }

    public String getComment() {
        return comment;
    }

    public String getStatus() {
        return status;
    }

    public String getStatus_name() {
        return status_name;
    }

    public String getCreated_at() {
        return created_at;
    }

    @Override
    public String toString() {
        return "TransactionModel{" +
                "id=" + id +
                ", slug='" + slug + '\'' +
                ", code='" + code + '\'' +
                ", payment_mode_id='" + payment_mode_id + '\'' +
                ", payment_mode='" + payment_mode + '\'' +
                ", amount='" + amount + '\'' +
                ", lease_id='" + lease_id + '\'' +
                ", house_unit='" + house_unit + '\'' +
                ", property='" + property + '\'' +
                ", comment='" + comment + '\'' +
                ", status='" + status + '\'' +
                ", status_name='" + status_name + '\'' +
                ", created_at='" + created_at + '\'' +
                '}';
    }
}
