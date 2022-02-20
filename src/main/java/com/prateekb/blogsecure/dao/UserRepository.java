package com.prateekb.blogsecure.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.prateekb.blogsecure.model.User;


public interface UserRepository extends JpaRepository<User, Integer>{
	
	Optional<User> findByUsername(String username);

	Boolean existsByUsername(String username);

	Boolean existsByEmail(String email);

	@Query(value ="select * from bloguser where username = :username and password = :password limit 1",nativeQuery=true)
	User findUserByCreds(@Param("username") String nameStr, @Param("password") String passwordStr);
	
}
