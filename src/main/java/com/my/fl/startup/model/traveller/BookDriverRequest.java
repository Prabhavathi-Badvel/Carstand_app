package com.my.fl.startup.model.traveller;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

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
