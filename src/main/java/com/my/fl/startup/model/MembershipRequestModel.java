package com.my.fl.startup.model;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MembershipRequestModel {

    private ResponseModel response;
    private Long id;
    private String status;
    private String transactionNo;
    private String phonePayNumber;
    private String totalAmount;
    private LocalDateTime purchaseDate;
    private LocalDateTime updatedDate;
    private CabMembershipRequestDetailsModel cabMembershipRequestDetailsModel;

}

