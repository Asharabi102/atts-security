package com.asharabi.atts.security.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails implements UserDetails {

	private static final long serialVersionUID = 8391514525638705280L;

	private final transient Logger log = LoggerFactory.getLogger(this.getClass());

	private String userName;
	private String password;
	//private boolean active;
	private List<GrantedAuthority> authorities = new ArrayList<>();

	public CustomUserDetails() {
	}

	public CustomUserDetails(User user) {
		this.userName = user.getEmail();
		this.password = user.getPassword();
		//this.active = true;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return userName;
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
}