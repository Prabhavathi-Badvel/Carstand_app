package com.my.fl.startup.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

@Getter
@Setter
@Entity
@Table(name = "admin_bike_master")
public class BikeMasterEntity {

    @Id
    @Column(name = "master_id", nullable = false, length = 45)
    private String masterId;

    @Column(name = "brand", length = 45)
    private String brand;

    @Column(name = "model", length = 45)
    private String model;

    @Column(name = "sub_model", length = 45)
    private String subModel;

    @Column(name = "body_type", length = 45)
    private String bodyType;

    @Column(name = "no_of_passengers", length = 45)
    private String noOfPassengers;

    @Column(name = "fuel_type", length = 45)
    private String fuelType;

    @Column(name = "transmission", length = 45)
    private String transmission;

    @Column(name = "color", length = 45)
    private String color;

    @Column(name = "updated_by", length = 45)
    private String updatedBy;

    @Column(name = "updated_date")
    @JsonFormat(pattern = "dd-MM-yyyy,HH:mm a")
    private LocalDateTime updatedDate;

    @PreUpdate
    public void preUpdate() {
        this.updatedDate = LocalDateTime.now();
    }

    @PrePersist
    public void prePersist() {
        this.updatedDate = LocalDateTime.now();
    }

}
