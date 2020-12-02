package com.fairmontsintenational.rentalapp.models;

import java.util.List;

public class BillsModel {
    private List<PendingBillModel> data;

    public BillsModel(List<PendingBillModel> data) {
        this.data = data;
    }

    public List<PendingBillModel> getData() {
        return data;
    }
}
