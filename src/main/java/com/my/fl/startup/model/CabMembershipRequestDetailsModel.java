package com.my.fl.startup.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CabMembershipRequestDetailsModel {

    private Long id;
    private String purchaseDate;
    private String planExpiryDate;
    private String planDuration;
    private Long membershipPlanId;
    private String membershipRequestId;
    private Long addCabId;


}

