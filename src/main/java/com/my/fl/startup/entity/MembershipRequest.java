package com.my.fl.startup.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "membershp_request_header")
@Getter
@Setter
public class MembershipRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String status;

    @Column(name = "transaction_no")
    private String transactionNo;

    @Column(name = "phone_pay_number")
    private String phonePayNumber;

    @Column(name = "total_amount")
    private String totalAmount;

    @Column(name = "purchase_date")
    private LocalDateTime purchaseDate;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;
    
    @Column(name = "membership_request_id")
    private String membershipRequestId;

}

