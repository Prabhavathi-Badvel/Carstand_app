package com.my.fl.startup.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cab_verification_feedback")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CabVerificationFeedbackEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cab_verify_SeqNo")
    private Long cabVerifySeqNo;

    @Column(name = "cab_gen_Id",unique = true)
    private String cabGenId;

    @Column(name = "verification_feedback", length = 300)
	private String verificationFeedback;

    @Column(name = "verification_status")
    private String  verificationStatus;
    
    private LocalDateTime  verificationDateTime;
    
    private String  adminId;

}
