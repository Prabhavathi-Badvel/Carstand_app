package com.my.fl.startup.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@IdClass(UserRoleKey.class)
@EqualsAndHashCode
@Table(name = "user_roles")
public class UserRoleEntity {
    @Id
    @Column(name="role_id")
    private Long roleId;


    @Id
    @Column(name="reg_id")
    private Long regId;

}