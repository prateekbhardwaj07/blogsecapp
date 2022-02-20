package com.prateekb.blogsecure.dao;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.prateekb.blogsecure.model.Roles;
import com.prateekb.blogsecure.util.UserRole;

public interface RoleRepository extends JpaRepository<Roles,Integer> {

	@Query(value="select * from user_roles where user_role =:user_role",nativeQuery = true)
	Roles findByName(@Param("user_role") UserRole user_role);
}
