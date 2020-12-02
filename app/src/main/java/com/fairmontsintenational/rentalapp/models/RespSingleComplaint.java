package com.fairmontsintenational.rentalapp.models;

public class RespSingleComplaint {
    private ComplaintsModel data;

    public RespSingleComplaint(ComplaintsModel data) {
        this.data = data;
    }

    public ComplaintsModel getData() {
        return data;
    }

    @Override
    public String toString() {
        return "RespSingleComplaint{" +
                "data=" + data +
                '}';
    }
}
