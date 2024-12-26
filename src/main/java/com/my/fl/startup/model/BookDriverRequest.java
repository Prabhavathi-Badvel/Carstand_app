package com.my.fl.startup.model;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class BookDriverRequest {


    private String fromLocation;

    private String toLocation;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate pickUpDate;

    private LocalDate returnDate;

    private LocalTime pickUpTime;

    private String driverId;

//    private String mUerId; // not required

    private String travellerId;


}