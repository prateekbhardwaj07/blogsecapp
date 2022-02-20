package com.prateekb.blogsecure.services;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.prateekb.blogsecure.dao.CategoryRepository;
import com.prateekb.blogsecure.dao.ContentRepository;
import com.prateekb.blogsecure.dao.OutlineRepository;
import com.prateekb.blogsecure.model.BaseEntity;
import com.prateekb.blogsecure.model.PostContent;
import com.prateekb.blogsecure.model.PostOutline;
import com.prateekb.blogsecure.util.Constants;

@Service
public class PostActionService {

	@Autowired
	ContentRepository contentRepository;

	@Autowired
	OutlineRepository outlineRepository;

	@Autowired
	CategoryRepository categoryRepository;

	private Path uploadDirLocation;

	public List<PostOutline> recentposts, trendingposts, allposts, categoryPosts;

	private PostContent savedEntity;

	@PostConstruct
	public void init() {
		this.uploadDirLocation = Paths.get(Constants.UPLOAD_LOCATION);
		try {
			Files.createDirectories(uploadDirLocation);
		} catch (IOException e) {
			throw new RuntimeException("Could not initialize storage", e);
		}
	}

	@Transactional
	public int storePostContent(CommonsMultipartFile file, HttpServletRequest request) {
		int stored = 0;
		try {
			savedEntity = contentRepository.save(processPostRequest(file, request));
			if (savedEntity != null) {
				stored = saveOutlineEntry(savedEntity);
			}
		} catch (Exception exception) {
			exception.printStackTrace();
			stored = -1;
		}
		return stored;
	}

	@Transactional
	public List<PostOutline> findRecentPostfromDB(HttpServletRequest request) {
		Integer offset = Integer.parseInt(request.getParameter("offset"));
		try {
			recentposts = outlineRepository.findRecentPostOutlines(offset);

		} catch (Exception exception) {
			exception.printStackTrace();
			recentposts = Stream.of(new PostOutline("No Result")).collect(Collectors.toList());

		}
		return recentposts;

	}

	@Transactional
	public List<PostOutline> findTrendingPostfromDB() {
		try {
			trendingposts = outlineRepository.findTrendingPostOutlines();
		} catch (Exception exception) {
			exception.printStackTrace();
			trendingposts = Stream.of(new PostOutline("No Result")).collect(Collectors.toList());
			;
		}
		return trendingposts;

	}

	@Transactional
	public List<PostOutline> fetchAllPostsDB() {
		try {
			allposts = (List<PostOutline>) outlineRepository.findAll();
		} catch (Exception exception) {
			exception.printStackTrace();
			allposts = Stream.of(new PostOutline("No Result")).collect(Collectors.toList());
		}
		return allposts;
	}

	@Transactional
	public List<PostOutline> fetchPostsByCategory(HttpServletRequest request) {

		String category_name = request.getParameter("category_name");
		Integer category_id = 0;
		if (category_name != null && category_name.length() > 0) {
			category_name.trim().toLowerCase();
			try {
				category_id = categoryRepository.findIdFromCategoryName(category_name);
				List<Integer> postIds = contentRepository.findPostIdsByCategory(category_id);
				categoryPosts = (List<PostOutline>) outlineRepository.findAllById(postIds);
			} catch (Exception exception) {
				System.out.println(exception.getMessage());
				categoryPosts = Stream.of(new PostOutline("No Result")).collect(Collectors.toList());
			}

		} else {
			categoryPosts = Stream.of(new PostOutline("Params Missing")).collect(Collectors.toList());
		}
		return categoryPosts;

	}

	@Transactional
	public Integer getPostOutlineRowCount() {
		int result = 0;
		try {
			Long count = outlineRepository.count();
			result = count.intValue();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return result;
	}

	@Transactional
	public PostContent getContentFromDBById(HttpServletRequest request) {
		Integer postID = Integer.parseInt(request.getParameter("post_id"));
		PostContent content = new PostContent();
		try {
			content = contentRepository.findContentById(postID);
			content.setMessage(postID.toString());
		} catch (Exception exception) {
			exception.printStackTrace();
			content.setMessage("Error Occurred");
		}
		return content;
	}

	private PostContent processPostRequest(CommonsMultipartFile file, HttpServletRequest request) {
		PostContent postContent = new PostContent();

		Integer category_id = categoryRepository.findIdFromCategoryName(request.getParameter("category_name"));

		postContent.setHeading(processToUTF_8(request.getParameter("postHeading")));
		postContent.setAuthor(processToUTF_8(request.getParameter("postAuthor")));
		postContent.setContent(processToUTF_8(request.getParameter("postContent")));
		postContent.setImagesrc(storeAtLocalPath(file));
		postContent.setCreated_tm(Timestamp.from(Instant.now()));
		postContent.setCategory_id(category_id);

		return postContent;
	}

	@Transactional
	private int saveOutlineEntry(PostContent postContent) {
		PostOutline outlineObj = new PostOutline();
		outlineObj.setPid(postContent.getPid());
		outlineObj.setHeading(postContent.getHeading());
		outlineObj.setAuthor(postContent.getAuthor());
		String[] strBufferArr = postContent.getContent().split("<p>");
		System.out.println(strBufferArr[1].substring(0, strBufferArr[1].length() - 5));
		StringBuffer buffer = new StringBuffer(strBufferArr[1].substring(0, strBufferArr[1].length() - 5));
		outlineObj.setRelatedtxt(buffer.toString());
		outlineObj.setCreated_tm(postContent.getCreated_tm());
		outlineObj.setImagesrc(postContent.getImagesrc());

		int result = (outlineRepository.save(outlineObj) != null) ? 1 : 0;
		System.out.println("Outline Entity save returned code" + result);
		return result;

	}

	private String storeAtLocalPath(CommonsMultipartFile file) {
		String filename = StringUtils.cleanPath(file.getOriginalFilename());
		try {
			if (file.isEmpty()) {
				throw new IOException("No File Found");
			}
			// This is a security check
			if (filename.contains("..")) {
				throw new IOException("Cannot store file with relative path outside current directory " + filename);
			}

			try {
				InputStream inputStream = file.getInputStream();
				Path targetFile = this.uploadDirLocation.resolve(filename);
				long success = Files.copy(inputStream, targetFile, StandardCopyOption.REPLACE_EXISTING);
				filename = Constants.UPLOAD_LOCATION + File.separator + filename;
				System.out.println(filename + " " + success);
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}

		} catch (IOException exception) {
			exception.printStackTrace();
			System.out.println(exception.getMessage());
		}
		return filename;
	}

	private BaseEntity generateEmptyObject(Class<?> baseEntity) {

		String classname = baseEntity.getName();
		try {
			Class<?> requiredClass = Class.forName(classname);
			Constructor<?> requiredConstruct = requiredClass.getConstructor();
			Object requiredObject = requiredConstruct.newInstance();
			return (BaseEntity) requiredObject;

		} catch (Exception exception) {
			exception.printStackTrace();
			return null;
		}

	}

	private String processToUTF_8(String rawString) {
		ByteBuffer buffer = StandardCharsets.UTF_8.encode(rawString);
		String utf8EncodedString = StandardCharsets.UTF_8.decode(buffer).toString();
		return utf8EncodedString;
	}

}
