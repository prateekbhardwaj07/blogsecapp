package com.prateekb.blogsecure.model;

public class AuthenticationResponse extends BaseEntity{

	private final String jwt;
	
	public AuthenticationResponse(String jwt) {
		this.jwt = jwt;
	}
	public String getJwt() {
		return jwt;
	}
}
