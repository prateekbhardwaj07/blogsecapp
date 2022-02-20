package com.prateekb.blogsecure.services;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prateekb.blogsecure.dao.UserRepository;
import com.prateekb.blogsecure.model.User;

@Service
public class BlogUserDetailService implements UserDetailsService {

	@Autowired
	UserRepository userRepository;

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByUsername(username).
				orElseThrow(() -> new UsernameNotFoundException("User Not Found with username"+username));
		return UserDetailsImpl.build(user);
	}

	
}
