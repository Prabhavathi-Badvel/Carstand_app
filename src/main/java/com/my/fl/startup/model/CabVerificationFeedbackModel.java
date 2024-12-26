package com.my.fl.startup.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CabVerificationFeedbackModel {

    private Long cabVerifySeqNo;
    private String cabId;
    private String verificationFeedback;
    private String userId;
    private Long cabSeqId;
    private String status;
}
