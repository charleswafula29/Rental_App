package com.fairmontsintenational.rentalapp.models;

import java.util.List;

public class TransRespModel {
    private List<TransactionModel> data;

    public TransRespModel(List<TransactionModel> data) {
        this.data = data;
    }

    public List<TransactionModel> getData() {
        return data;
    }
}
