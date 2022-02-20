package com.prateekb.blogsecure.model;


import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import com.vladmihalcea.hibernate.type.array.ListArrayType;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Table(name="bloguser",uniqueConstraints = {
		@UniqueConstraint(columnNames = "email"),
		@UniqueConstraint(columnNames = "username")
})
@TypeDef(
	    name = "list-array",
	    typeClass = ListArrayType.class
	)
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="user_id")
	private Integer uid;
	
	@NotNull
	private String username;
	
	@NotNull
	private String password;
	
	private String email;
	
	private Integer role_id;
	
	@Type(type="list-array")
	@Column(name="interests",columnDefinition = "text[]")
	private List<String> interests;
	
	private Timestamp arr_time;
	
	private boolean is_subscribed;
	
	public User(String message) {
		super.message = message;		
	}
	
	public User(String username, String email, String password) {
		this.username = username;
		this.email = email;
		this.password = password;
	}

	
	public Integer getUid() {
		return uid;
	}
	public void setUid(Integer uid) {
		this.uid = uid;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getRole_id() {
		return role_id;
	}

	public void setRole_id(Integer role_id) {
		this.role_id = role_id;
	}
	
	public List<String> getInterests() {
		return interests;
	}

	public void setInterests(List<String> interests) {
		this.interests = interests;
	}

	public Timestamp getArr_time() {
		return arr_time;
	}

	public void setArr_time(Timestamp arr_time) {
		this.arr_time = arr_time;
	}

	public boolean isIs_subscribed() {
		return is_subscribed;
	}

	public void setIs_subscribed(boolean is_subscribed) {
		this.is_subscribed = is_subscribed;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}