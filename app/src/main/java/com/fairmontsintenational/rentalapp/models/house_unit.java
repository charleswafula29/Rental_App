package com.fairmontsintenational.rentalapp.models;

public class house_unit {
    private Integer id;
    private String slug,name,description;
    private house_unit_property property;

    public house_unit(Integer id, String slug, String name, String description, house_unit_property property) {
        this.id = id;
        this.slug = slug;
        this.name = name;
        this.description = description;
        this.property = property;
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

    public String getDescription() {
        return description;
    }

    public house_unit_property getProperty() {
        return property;
    }

    @Override
    public String toString() {
        return "house_unit{" +
                "id=" + id +
                ", slug='" + slug + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", property=" + property +
                '}';
    }
}
