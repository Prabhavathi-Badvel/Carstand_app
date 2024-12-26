package com.my.fl.startup.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
public class CabMasterRequest implements Serializable {

    private String masterId;
    private String brand;
    private String model;
    private String subModel;
    private String bodyType;
    private String noOfPassengers;
    private String fuelType;
    private String transmission;
    private String color;
    private String updatedBy;
    private LocalDateTime updatedDate;

}
