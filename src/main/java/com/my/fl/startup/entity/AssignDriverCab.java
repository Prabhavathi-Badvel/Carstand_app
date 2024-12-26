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
@Table(name = "assign_driver_cab")
@Getter
@Setter
public class AssignDriverCab {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ASSIGN_ID")
	private Integer id;

	@Column(name = "DRIVER_ID")
	private String driverId;

	@Column(name = "CAB_ID")
	private String cabId;

	@Column(name = "CAB_OWNER_ID")
	private String cabOwnerId;

	@Column(name = "ASSIGN_DATE")
	private String assignDate;

	@Column(name = "ASSIGN_TIME")
	private String assignTime;

	@Column(name = "STATUS")
	private String status;

}
