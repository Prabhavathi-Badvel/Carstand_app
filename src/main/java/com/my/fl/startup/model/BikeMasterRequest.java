package com.my.fl.startup.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BikeMasterRequest implements Serializable {

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
    @JsonFormat(pattern = "dd-MM-yyyy,HH:mm a", shape = JsonFormat.Shape.STRING)
    private LocalDateTime updatedDate;

}
