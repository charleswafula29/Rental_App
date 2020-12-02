package com.fairmontsintenational.rentalapp.models;

public class house_unit_property {
    private Integer id;
    private String slug,name,location,units_no,status;

    public house_unit_property(Integer id, String slug, String name, String location, String units_no, String status) {
        this.id = id;
        this.slug = slug;
        this.name = name;
        this.location = location;
        this.units_no = units_no;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public String getSlug() {
        return slug;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getUnits_no() {
        return units_no;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "house_unit_property{" +
                "id=" + id +
                ", slug='" + slug + '\'' +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", units_no='" + units_no + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
