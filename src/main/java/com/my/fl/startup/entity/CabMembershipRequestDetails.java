package com.my.fl.startup.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "cab_membership_request_details")
@Getter
@Setter
public class CabMembershipRequestDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "plan_duration")
    private String planDuration;

    @Column(name = "plan_expiry_date")
    private String planExpiryDate;

    @Column(name = "membership_plan_id")
    private Long membershipPlanId;

    @Column(name = "membership_request_id")
    private Long membershipRequestId;

    @Column(name = "add_cab_id")
    private Long addCabId;
    
    @Column(name = "membership_request_line_id")
    private String membershipRequestLineId;

}

