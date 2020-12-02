package com.fairmontsintenational.rentalapp.models;

import java.util.List;

public class RespComplaintsModel {
    List<ComplaintsModel> data;

    public RespComplaintsModel(List<ComplaintsModel> data) {
        this.data = data;
    }

    public List<ComplaintsModel> getData() {
        return data;
    }

    @Override
    public String toString() {
        return "RespComplaintsModel{" +
                "data=" + data +
                '}';
    }
}
