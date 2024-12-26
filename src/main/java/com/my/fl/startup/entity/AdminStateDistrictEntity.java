package com.my.fl.startup.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "admin_state_dist")
public class AdminStateDistrictEntity {

    @Id
    @Column(name="state_dist_id")
    private String stateDistrictId;

    @Column(length = 100)
    private String district;

    @Column(length = 100)
    private String state;

    @Column(name = "updated_by", length = 45)
    private String updatedBy;

    @Column(name = "updated_date", length = 45)
    private String updatedDate;

    @Column(length = 45)
    private String status;
}
