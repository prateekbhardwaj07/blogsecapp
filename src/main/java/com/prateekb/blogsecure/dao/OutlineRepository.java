package com.prateekb.blogsecure.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.prateekb.blogsecure.model.PostOutline;



public interface OutlineRepository extends JpaRepository<PostOutline, Integer> {
	
	@Query(value = "select post_id from post_outline where heading = :heading and author = :author limit 1",nativeQuery = true)
	Integer findPostIDFromOutline(@Param("heading") String heading,@Param("author") String author);
	
	@Query(value = "select * from post_outline order by created_tm desc limit 5 offset :offset",nativeQuery = true)
	List<PostOutline> findRecentPostOutlines(@Param("offset") Integer offset);

	@Query(value = "select post_outline.post_id,heading,author,relatedtxt,created_tm,imagesrc from post_outline inner join pagevisits "
			+ "on post_outline.post_id = pagevisits.post_id order by visit_count desc;",nativeQuery = true)
	List<PostOutline> findTrendingPostOutlines();
	

}
