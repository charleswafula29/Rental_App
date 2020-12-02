package com.fairmontsintenational.rentalapp.models;

import java.util.List;

public class TenantModel {
    private Integer id;
    private String slug,first_name,last_name,id_number,middle_name,telephone,phone,
    address,email,active,dob;
    //private List<String> noks;
    private List<PropertyModel> leases;

    public TenantModel(Integer id, String slug, String first_name, String last_name, String id_number,
                       String middle_name, String telephone, String phone, String address, String email, String active, String dob, List<PropertyModel> leases) {
        this.id = id;
        this.slug = slug;
        this.first_name = first_name;
        this.last_name = last_name;
        this.id_number = id_number;
        this.middle_name = middle_name;
        this.telephone = telephone;
        this.phone = phone;
        this.address = address;
        this.email = email;
        this.active = active;
        this.dob = dob;
        this.leases = leases;
    }

    public Integer getId() {
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

    public String getId_number() {
        return id_number;
    }

    public String getMiddle_name() {
        return middle_name;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getEmail() {
        return email;
    }

    public String getActive() {
        return active;
    }

    public String getDob() {
        return dob;
    }

//    public List<String> getNoks() {
//        return noks;
//    }

    public List<PropertyModel> getLeases() {
        return leases;
    }

    @Override
    public String toString() {
        return "TenantModel{" +
                "id=" + id +
                ", slug='" + slug + '\'' +
                ", first_name='" + first_name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", id_number='" + id_number + '\'' +
                ", middle_name='" + middle_name + '\'' +
                ", telephone='" + telephone + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", email='" + email + '\'' +
                ", active='" + active + '\'' +
                ", dob='" + dob + '\'' +
                ", leases=" + leases +
                '}';
    }
}
