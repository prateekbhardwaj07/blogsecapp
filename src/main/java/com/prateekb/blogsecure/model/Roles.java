package com.prateekb.blogsecure.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.prateekb.blogsecure.util.UserRole;

@Entity
@Table(name="user_roles")
public class Roles extends BaseEntity implements Serializable{
	private static final long serialVersionUID = 1L;

	@Id@GenericGenerator(name = "blog_generator", strategy = "sequence", parameters = {
            @org.hibernate.annotations.Parameter(name = "sequenceName", value = "sequence"),
            @org.hibernate.annotations.Parameter(name = "allocationSize", value = "1"),
    })
	@GeneratedValue(strategy=GenerationType.SEQUENCE,generator="blog_generator")
	@Column(name="role_id")
	private Integer role_id;

	@Enumerated(EnumType.STRING)
	@Column(length = 20)
	private UserRole user_role;
	
	public Roles() {}
	
	public Roles(UserRole role) {
		this.user_role = role;
	}
	
	public Integer getRole_id() {
		return role_id;
	}

	public void setRole_id(Integer role_id) {
		this.role_id = role_id;
	}

	public UserRole getUser_role() {
		return user_role;
	}

	public void setUser_role(UserRole user_role) {
		this.user_role = user_role;
	}

	
}
