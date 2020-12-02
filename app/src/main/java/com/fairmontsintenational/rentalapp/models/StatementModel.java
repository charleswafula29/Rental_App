package com.fairmontsintenational.rentalapp.models;

public class StatementModel {
    private String Date,code,tenant_id,Charges,Payments;

    public StatementModel(String date, String code, String tenant_id, String charges, String payments) {
        Date = date;
        this.code = code;
        this.tenant_id = tenant_id;
        Charges = charges;
        Payments = payments;
    }

    public String getDate() {
        return Date;
    }

    public String getCode() {
        return code;
    }

    public String getTenant_id() {
        return tenant_id;
    }

    public String getCharges() {
        return Charges;
    }

    public String getPayments() {
        return Payments;
    }
}
