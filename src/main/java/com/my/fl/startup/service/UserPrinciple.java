package com.my.fl.startup.service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.my.fl.startup.entity.RegistrationEntity;
import com.my.fl.startup.entity.Role;
import com.my.fl.startup.entity.RoleName;

public class UserPrinciple implements UserDetails {

	private static final long serialVersionUID = 1L;

	private Long id;

	private String name;

	private String username;

	private String email;

	private String candidateID;

	@JsonIgnore
	private String password;

	private Collection<? extends GrantedAuthority> authorities;

	private Boolean isVerified;

	public UserPrinciple(Long id, String name, String username, String email, String password,
			Collection<? extends GrantedAuthority> authorities, String candidateID, Boolean isVerified) {
		this.id = id;
		this.name = name;
		this.username = username;
		this.email = email;
		this.password = password;
		this.authorities = authorities;
		this.isVerified = isVerified;
		this.candidateID = candidateID;
	}

	public static UserPrinciple build(RegistrationEntity user) {

		// For setting roles
		Role role1 = new Role();
		role1.setName(RoleName.ADMIN);
		Set<Role> roleSet = new HashSet<>();
		roleSet.add(role1);
		user.setRoles(roleSet);

		List<GrantedAuthority> authorities = user.getRoles().stream()
				.map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList());

		return new UserPrinciple(user.getId(), user.getFirstName(), user.getEmail(), user.getEmail(),
				user.getPassword(), authorities, user.getCandidateID(),
				(user.getStatus().equals("ACTIVE") ? true : false));
	}

	public Long getId() {
		return id;
	}

	public String getCandidateID() {
		return candidateID;
	}

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}

	public Boolean getIsVerified() {
		return isVerified;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		UserPrinciple user = (UserPrinciple) o;
		return Objects.equals(id, user.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}


}
