package com.fairmontsintenational.rentalapp.models;

public class RespStatementModel {
    private TenantStatementModel data;

    public RespStatementModel(TenantStatementModel data) {
        this.data = data;
    }

    public TenantStatementModel getData() {
        return data;
    }
}
