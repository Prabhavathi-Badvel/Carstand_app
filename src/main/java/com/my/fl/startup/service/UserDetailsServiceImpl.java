package com.my.fl.startup.service;

import java.util.HashSet;
import java.util.Set;

import com.my.fl.startup.entity.*;
import com.my.fl.startup.repo.AdminLoginRepo;
import com.my.fl.startup.repo.RegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.my.fl.startup.model.UserDetailsModel;
import com.my.fl.startup.repo.RoleRepository;
import com.my.fl.startup.repo.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private AdminLoginRepo adminLoginRepo;
	@Autowired
    private UserRepository userRepository;

    @Autowired
    private RegistrationRepository registrationRepository;


    @Autowired
    RoleRepository roleRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        AdminLogin adminLogin = adminLoginRepo.findByEmpEmailOrEmpMobile(username, username);
        if (adminLogin != null) {
            return adminLogin;
        }

	    RegistrationEntity user = registrationRepository.findByEmailORMobileNumber(username);
	    if (user != null) {
//	        return org.springframework.security.core.userdetails.User
//	                .withUsername(username)
//	                .password(user.getPassword())
//	                .roles("USER")
//	                .build();
	    	return UserPrinciple.build(user);
	    } else {
	        throw new UsernameNotFoundException("User not found with username: " + username);
	    }
	}
	
	/*
     * Check Is User Deleted
     * 
     */
    public Boolean IsUserDeleted(String userName) {
        Boolean isUserDeleted;
        try {
            User user = userRepository.findByEmail(userName.trim().toLowerCase());
            if (user!=null) {
             isUserDeleted = user.getIsArchived();
            }else{
             isUserDeleted = null; 
            }
        } catch (Exception ex) {
            isUserDeleted = false;
        }
        return isUserDeleted;
    }
    
    /*
     * Check User Email Exist
     * 
     */
    public Boolean IsEmailExist(String email) {
        Boolean isEmailExist;
        try {
            User user = userRepository.findByEmail(email);
            if (user != null) {
            isEmailExist = true;
            }else{
            isEmailExist = false;  
            }
        } catch (Exception ex) {
            isEmailExist = false;
        }
        return isEmailExist;
    }
    
    /*
     * User Role mapp Enum
     * 
     */
    @Transactional
    public Set<Role> setUserRole(UserDetailsModel userDetailsModel) {
        String strRoles = "";
        String rolename = "";
        if ((!"".equals(userDetailsModel.getRoleName()) || userDetailsModel.getRoleName() != null)) {
            strRoles = userDetailsModel.getRoleName().toLowerCase();
        }

        Set<Role> roles = new HashSet<>();

        switch (strRoles) {
            case "admin":
                Role adminRole = roleRepository.findByName(RoleName.ADMIN)
                        .orElseThrow(() -> new RuntimeException("Fail! -> Cause: User Role not find."));
                roles.add(adminRole);

                break;
            case "pm":
                Role pmRole = roleRepository.findByName(RoleName.ROLE_PM)
                        .orElseThrow(() -> new RuntimeException("Fail! -> Cause: User Role not find."));
                roles.add(pmRole);

                break;
            case "business_admin":
                Role businessAdminRole = roleRepository.findByName(RoleName.BUSINESS_ADMIN)
                        .orElseThrow(() -> new RuntimeException("Fail! -> Cause: User Role not find."));
                roles.add(businessAdminRole);

                break;
            default:
                Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                        .orElseThrow(() -> new RuntimeException("Fail! -> Cause: User Role not find."));
                roles.add(userRole);
        }
        return roles;
    }


}
