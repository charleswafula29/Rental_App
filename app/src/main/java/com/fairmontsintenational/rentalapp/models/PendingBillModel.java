package com.fairmontsintenational.rentalapp.models;

import java.util.List;

public class PendingBillModel {
    private String slug,code;
    private Integer id;
    private String billing_month;
    private house_unit housing_unit;
    private PropertyModel lease;
    private String amount,previous_balance,billing_type,balance;
    private List<String> paid_amounts;
    private String created_at;

    public PendingBillModel(String slug, String code, Integer id, String billing_month, house_unit housing_unit,
                            PropertyModel lease, String amount, String previous_balance, String billing_type, String balance,
                            List<String> paid_amounts, String created_at) {
        this.slug = slug;
        this.code = code;
        this.id = id;
        this.billing_month = billing_month;
        this.housing_unit = housing_unit;
        this.lease = lease;
        this.amount = amount;
        this.previous_balance = previous_balance;
        this.billing_type = billing_type;
        this.balance = balance;
        this.paid_amounts = paid_amounts;
        this.created_at = created_at;
    }

    public String getSlug() {
        return slug;
    }

    public String getCode() {
        return code;
    }

    public Integer getId() {
        return id;
    }

    public String getBilling_month() {
        return billing_month;
    }

    public house_unit getHousing_unit() {
        return housing_unit;
    }

    public PropertyModel getLease() {
        return lease;
    }

    public String getAmount() {
        return amount;
    }

    public String getPrevious_balance() {
        return previous_balance;
    }

    public String getBilling_type() {
        return billing_type;
    }

    public String getBalance() {
        return balance;
    }

    public List<String> getPaid_amounts() {
        return paid_amounts;
    }

    public String getCreated_at() {
        return created_at;
    }
}
