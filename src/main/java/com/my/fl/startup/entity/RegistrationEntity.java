package com.my.fl.startup.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "registration")
@Getter
@Setter
public class RegistrationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REG_ID")
    private Long Id;

    @Column(name = "CANDIDATE_NAME")
    private String firstName;

    @Column(name = "ADDRESS")
    private String address;

    @Column(name = "EMAIL_ID")
    private String email;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "CONTACT_NO")
    private String mobileNumber;

    @Column(name = "USER_TYPE")
    private String userType;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "REG_DATE")
    private LocalDateTime regDate;

    @Column(name = "CANDIDATE_ID")
    private String candidateID;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "REG_ID"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

}