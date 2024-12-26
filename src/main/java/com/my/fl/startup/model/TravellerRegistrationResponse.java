package com.my.fl.startup.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TravellerRegistrationResponse  {

    private Long travellerId;

    private String travellerName;

    private String travellerMobile;

    private String travellerEmail;

    private String status;

}
