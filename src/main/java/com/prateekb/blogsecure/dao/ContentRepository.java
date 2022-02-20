package com.prateekb.blogsecure.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.prateekb.blogsecure.model.PostContent;


public interface ContentRepository extends JpaRepository<PostContent,Integer> {
	
	@Query(value = "select post_id from post_content where category_id =:category_id",nativeQuery = true)
	List<Integer> findPostIdsByCategory(@Param("category_id") Integer category_id);

	@Query(value = "select * from post_content where post_id =:postId",nativeQuery=true)
	PostContent findContentById(@Param("postId") Integer postId);

}
