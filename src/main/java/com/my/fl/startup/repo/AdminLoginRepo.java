package com.my.fl.startup.repo;

import com.my.fl.startup.entity.AdminLogin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminLoginRepo extends JpaRepository<AdminLogin, String> {


    AdminLogin findByEmpEmailOrEmpMobile(String empEmail, String empMobile);

    Optional<AdminLogin> findByEmpEmail(String empEmail);
}
