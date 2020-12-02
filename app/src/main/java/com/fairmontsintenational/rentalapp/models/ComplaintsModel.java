package com.fairmontsintenational.rentalapp.models;

public class ComplaintsModel {
    private int id;
    private String slug,name,description,statusName,status,image,created_at;
    private TenantModel tenant;
    private house_unit houseUnit;

    public ComplaintsModel(int id, String slug, String name, String description, String statusName, String status, String image, String created_at, TenantModel tenant, house_unit houseUnit) {
        this.id = id;
        this.slug = slug;
        this.name = name;
        this.description = description;
        this.statusName = statusName;
        this.status = status;
        this.image = image;
        this.created_at = created_at;
        this.tenant = tenant;
        this.houseUnit = houseUnit;
    }

    public int getId() {
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

    public String getStatusName() {
        return statusName;
    }

    public String getStatus() {
        return status;
    }

    public String getImage() {
        return image;
    }

    public String getCreated_at() {
        return created_at;
    }

    public TenantModel getTenant() {
        return tenant;
    }

    public house_unit getHouseUnit() {
        return houseUnit;
    }

    @Override
    public String toString() {
        return "ComplaintsModel{" +
                "id=" + id +
                ", slug='" + slug + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", statusName='" + statusName + '\'' +
                ", status='" + status + '\'' +
                ", image='" + image + '\'' +
                ", created_at='" + created_at + '\'' +
                ", tenant=" + tenant +
                ", houseUnit=" + houseUnit +
                '}';
    }
}
