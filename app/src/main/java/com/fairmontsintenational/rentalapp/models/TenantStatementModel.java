package com.fairmontsintenational.rentalapp.models;

import java.util.List;

public class TenantStatementModel {
    private String id,slug,first_name,last_name,middle_name,id_number;
    private List<StatementModel> statements;

    public TenantStatementModel(String id, String slug, String first_name, String last_name, String middle_name, String id_number, List<StatementModel> statements) {
        this.id = id;
        this.slug = slug;
        this.first_name = first_name;
        this.last_name = last_name;
        this.middle_name = middle_name;
        this.id_number = id_number;
        this.statements = statements;
    }

    public String getId() {
        return id;
    }

    public String getSlug() {
        return slug;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getMiddle_name() {
        return middle_name;
    }

    public String getId_number() {
        return id_number;
    }

    public List<StatementModel> getStatements() {
        return statements;
    }
}
