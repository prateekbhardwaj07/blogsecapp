package com.prateekb.blogsecure.services;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prateekb.blogsecure.dao.CategoryRepository;
import com.prateekb.blogsecure.model.Blogcategory;


@Service
public class CategoryService {

	@Autowired
	CategoryRepository categoryRepo;
	
	List<Blogcategory> categories;
	

	@Transactional
	public String saveCategory(HttpServletRequest request) {
		String returnMessage = "";
		try
		{
			Blogcategory category = categoryRepo.save(processCategoryRequest(request));
			returnMessage = (!category.equals(null))? "Suceess":"Error";
		}
		catch(Exception exception) {
			exception.printStackTrace();
			return returnMessage.concat("Exception Occurred");
		}
		return returnMessage;
	}
	
	@Transactional
	public List<Blogcategory> fetchListDB(HttpServletRequest request){
		String limit = request.getParameter("limitResult");
		try {
			categories = categoryRepo.findAllCategory();
		}
		catch(Exception exception) {
			exception.printStackTrace();
			categories = Stream.of(new Blogcategory("No Result")).collect(Collectors.toList());
			categories.get(0).setMessage("Exception Occured");
		}
		return categories;
	}
	
	private Blogcategory processCategoryRequest(HttpServletRequest request)
	{
		Blogcategory category = new Blogcategory();
		category.setCategory_name(request.getParameter("category_name"));
		category.setKeywords(request.getParameter("keywords"));
		category.setCreated_on(Timestamp.from(Instant.now()));
		return category;
		
	}
	
}
