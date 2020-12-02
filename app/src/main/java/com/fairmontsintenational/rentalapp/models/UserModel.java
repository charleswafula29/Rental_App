package com.fairmontsintenational.rentalapp.models;

public class UserModel {
    private Integer id;
    private String slug,first_name,last_name,middle_name,email,id_number,telephone,kra_pin,phone,address,active,dob,profile_image;

    public UserModel(Integer id, String slug, String first_name, String last_name, String middle_name,
                     String email, String id_number, String telephone, String kra_pin, String phone, String address, String active, String dob,String profile_image) {
        this.id = id;
        this.slug = slug;
        this.first_name = first_name;
        this.last_name = last_name;
        this.middle_name = middle_name;
        this.email = email;
        this.id_number = id_number;
        this.telephone = telephone;
        this.kra_pin = kra_pin;
        this.phone = phone;
        this.address = address;
        this.active = active;
        this.dob = dob;
        this.profile_image = profile_image;
    }

    public String getProfile_image() {
        return profile_image;
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

    public String getMiddle_name() {
        return middle_name;
    }

    public String getEmail() {
        return email;
    }

    public String getId_number() {
        return id_number;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getKra_pin() {
        return kra_pin;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getActive() {
        return active;
    }

    public String getDob() {
        return dob;
    }

    @Override
    public String toString() {
        return "UserModel{" +
                "id=" + id +
                ", slug='" + slug + '\'' +
                ", first_name='" + first_name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", middle_name='" + middle_name + '\'' +
                ", email='" + email + '\'' +
                ", id_number='" + id_number + '\'' +
                ", telephone='" + telephone + '\'' +
                ", kra_pin='" + kra_pin + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", active='" + active + '\'' +
                ", dob='" + dob + '\'' +
                '}';
    }
}
