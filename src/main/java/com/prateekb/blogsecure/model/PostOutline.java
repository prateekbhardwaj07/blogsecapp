package com.prateekb.blogsecure.model;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Table(name="post_outline")
@NoArgsConstructor
@AllArgsConstructor
public class PostOutline extends BaseEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id@GenericGenerator(name = "blog_generator", strategy = "sequence", parameters = {
            @org.hibernate.annotations.Parameter(name = "sequenceName", value = "sequence"),
            @org.hibernate.annotations.Parameter(name = "allocationSize", value = "1"),
    })
	@GeneratedValue(strategy=GenerationType.SEQUENCE,generator="blog_generator")
	@Column(name="post_id")
	private Integer pid;
	
	private String heading;
	private String author;
	private String relatedtxt;
	private Timestamp created_tm;
	private String imagesrc;
	
	public PostOutline(String message) {
		super.message = message;		
	}
	
	public Integer getPid() {
		return pid;
	}
	public void setPid(Integer pid) {
		this.pid = pid;
	}
	public String getHeading() {
		return heading;
	}
	public void setHeading(String heading) {
		this.heading = heading;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getRelatedtxt() {
		return relatedtxt;
	}
	public void setRelatedtxt(String relatedtxt) {
		this.relatedtxt = relatedtxt;
	}
	public Timestamp getCreated_tm() {
		return created_tm;
	}
	public void setCreated_tm(Timestamp created_tm) {
		this.created_tm = created_tm;
	}
	public String getImagesrc(){
		return imagesrc;
	}
	public void setImagesrc(String imagesrc){
		this.imagesrc = imagesrc;
	}
}

/*@GenericGenerator(name = "blog_generator", strategy = "sequence", parameters = {
@org.hibernate.annotations.Parameter(name = "sequenceName", value = "sequence"),
@org.hibernate.annotations.Parameter(name = "allocationSize", value = "1"),
})
@GeneratedValue(strategy=GenerationType.SEQUENCE,generator="blog_generator")
*/