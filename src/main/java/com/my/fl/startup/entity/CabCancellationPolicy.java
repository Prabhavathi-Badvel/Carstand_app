package com.my.fl.startup.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "cab_cancellation_policy")
@Getter
@Setter
public class CabCancellationPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CB_CL_PL_SEQ_NO")
    private Long cbClPlSeqNo;

    @Column(name = "CAB_REG_NO")
    private String cabRegNo;

    @Column(name = "TIME")
    private String time;

    @Column(name = "CANCELLATION_CHARGES")
    private String cancellationCharges;

    @Column(name = "CHARGES_ADDED_DATE")
    private String chargesAddedDate;

    @Column(name = "CANCELLATION_CHARGES_STATUS")
    private String cancellationChargesStatus;

    @Column(name = "CAB_OWNER_ID")
    private String cabOwnerId;

    @Column(name = "WAITING_CHARGES")
    private String waitingCharges;

}
