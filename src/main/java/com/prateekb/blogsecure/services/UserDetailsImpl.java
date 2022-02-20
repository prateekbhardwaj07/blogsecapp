package com.prateekb.blogsecure.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.prateekb.blogsecure.model.Roles;
import com.prateekb.blogsecure.model.User;
import com.prateekb.blogsecure.util.UserRole;

public class UserDetailsImpl implements UserDetails {
	
	private static final long serialVersionUID = 1L;

	private Integer uid;
	
	private String username;

	private String email;

	@JsonIgnore
	private String password;
	
	private Collection<? extends GrantedAuthority> authorities;
	
	public UserDetailsImpl(Integer uid, String username, String email, String password,
			Collection<? extends GrantedAuthority> authorities) {
		this.uid = uid;
		this.username = username;
		this.email = email;
		this.password = password;
		this.authorities = authorities;
	}

	public static UserDetailsImpl build(User user) {
		UserRole[] rolesArr = UserRole.values();
		int index = user.getRole_id();
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority(rolesArr[index].name()));

		return new UserDetailsImpl(
				user.getUid(), 
				user.getUsername(), 
				user.getEmail(),
				user.getPassword(), 
				authorities);
	}


	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	public String getEmail() {
		return email;
	}
	
	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
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
		// TODO Auto-generated method stub
		return true;
	}
	

}
