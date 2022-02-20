package com.prateekb.blogsecure.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.prateekb.blogsecure.model.Blogcategory;



public interface CategoryRepository extends JpaRepository<Blogcategory,Integer> {

	@Query(value = "select categry_id from blogcategory where category_name =:category_name limit 1",nativeQuery = true)
	Integer findIdFromCategoryName(@Param("category_name") String category_name);
	
	@Query(value="select * from blogcategory order by category_name",nativeQuery = true)
	List<Blogcategory> findAllCategory();

	
}
