package com.my.fl.startup.service;

import com.my.fl.startup.entity.AdminLogin;
import com.my.fl.startup.jwt.JwtProvider;
import com.my.fl.startup.jwt.JwtResponse;
import com.my.fl.startup.jwt.LoginForm;
import com.my.fl.startup.model.ResponseMessageDto;
import com.my.fl.startup.repo.AdminLoginRepo;
import com.my.fl.startup.repo.AdminSecurityRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdminLoginService {
    @Autowired
    private AdminLoginRepo adminLoginRepo;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

//    @Autowired
//    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private AdminSecurityRepository adminSecurityRepository;

    @Autowired
    private AuthenticationManager authenticationManager;


    public ResponseEntity<ResponseMessageDto> authenticateAdmin(LoginForm loginRequest, HttpServletRequest request) {

        ResponseMessageDto response = new ResponseMessageDto();

        if (loginRequest.getPassword() == null) {
            response.setMessage("Error: Password is null");
            response.setStatus(false);
            return ResponseEntity.badRequest().body(response);
        }

		AdminLogin admin = adminLoginRepo.findByEmpEmailOrEmpMobile(loginRequest.getUsername(), loginRequest.getUsername());
        if (admin == null) {
            response.setMessage("Error: Admin not found");
            response.setStatus(false);
            return ResponseEntity.badRequest().body(response);
        }

        if (!bCryptPasswordEncoder.matches(loginRequest.getPassword(), admin.getEmpPwd())) {
            response.setMessage("Error: Incorrect password");
            response.setStatus(false);
            return ResponseEntity.badRequest().body(response);
        }

        // Encrypt the password and update in the database if it matches the stored password but is not encrypted
        if (!loginRequest.getPassword().equals(admin.getEmpPwd())) {
            admin.setEmpPwd(bCryptPasswordEncoder.encode(loginRequest.getPassword()));
            adminLoginRepo.save(admin);
        }



        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(admin.getUsername(), loginRequest.getPassword(), admin.getAuthorities())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtProvider.generateJwtToken(admin.getUsername(), request.getRemoteAddr());
        JwtResponse jwtResponse = new JwtResponse(
                jwt,
                admin.getUsername(),
                admin.getEmpId(),
                null, // Assuming businessId is not applicable here
                admin.getEmpName(),
                admin.getEmpEmail(),
                admin.getUserType(),
                true,
                "Success",
                null
        );
        response.setMessage("User authenticated successfully");
        response.setStatus(false);
        response.setData(jwtResponse);
        return ResponseEntity.ok(response);

    }

    public ResponseEntity<?> changePassword(String username, String newPassword) {
        AdminLogin admin = adminLoginRepo.findByEmpEmailOrEmpMobile(username, username);
        if (admin == null) {
            return ResponseEntity.badRequest().body("Admin not found");
        }

        // Encrypt the new password
        admin.setEmpPwd(bCryptPasswordEncoder.encode(newPassword));
        adminLoginRepo.save(admin);

        return ResponseEntity.ok("Password changed successfully");
    }


}
