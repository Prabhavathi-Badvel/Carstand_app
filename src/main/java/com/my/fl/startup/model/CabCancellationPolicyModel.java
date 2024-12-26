package com.my.fl.startup.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CabCancellationPolicyModel {

    private Long cbClPlSeqNo;

    private String cabRegNo;
    private String time;
    private String cancellationCharges;
    private String chargesAddedDate;
    private String cancellationChargesStatus;
    private String cabOwnerId;
    private String waitingCharges;

}
