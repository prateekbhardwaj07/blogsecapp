package com.prateekb.blogsecure;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;


import com.prateekb.blogsecure.model.BaseEntity;
import com.prateekb.blogsecure.model.Blogcategory;
import com.prateekb.blogsecure.model.PostContent;
import com.prateekb.blogsecure.model.PostOutline;
import com.prateekb.blogsecure.model.User;
import com.prateekb.blogsecure.services.CategoryService;
import com.prateekb.blogsecure.services.PostActionService;
import com.prateekb.blogsecure.services.UserService;
import com.prateekb.blogsecure.model.AuthenticationRequest;
import com.prateekb.blogsecure.model.AuthenticationResponse;
import com.prateekb.blogsecure.services.BlogUserDetailService;
import com.prateekb.blogsecure.util.JwtUtils;


@RestController
@CrossOrigin(origins = "http://localhost:4200", maxAge = 12000)
public class BlogController {

	private static final Logger logger = LoggerFactory.getLogger(BlogController.class);

	@Autowired
	UserService userService;
	
	@Autowired
	PostActionService postService;
	
	@Autowired
	CategoryService categoryService;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private BlogUserDetailService userDetailsService;
	
	@Autowired
	private JwtUtils jwtTokenUtil;

	
	@PostMapping("/processRegister")
	public ResponseEntity<BaseEntity> makeRegister(@RequestBody  JSONObject rgUser, HttpServletRequest request, HttpServletResponse response) {
		BaseEntity base = new BaseEntity();
		User user = userService.processRequest(rgUser);
		if (userService.makeUserEntry(user) > 0) {
			base.setMessage("Success");
			return ResponseEntity.accepted().body(base);
		}
		else {
			base.setMessage("Error");
			return ResponseEntity.accepted().body(base);
		}
		
	}
	
	public String createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception{
		try {
			authenticationManager.authenticate(
			new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
			);
		}catch(BadCredentialsException exception) {
			exception.getMessage();
		}
		final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
		final String jwt = jwtTokenUtil.generateToken(userDetails);
		System.out.println(jwt);
		return jwt;	
		
	}

	@PostMapping("/processLogin")
	public ResponseEntity<BaseEntity> makeLogin(@RequestBody JSONObject lgUser ,HttpServletRequest request, HttpServletResponse response) {
		AuthenticationRequest authRequest = 
				new AuthenticationRequest(lgUser.get("username").toString(),lgUser.get("password").toString());
		BaseEntity authResponse;
		try {
			String jwt = createAuthenticationToken(authRequest);
			authResponse = new AuthenticationResponse(jwt);
			authResponse.setMessage("Success");
		} catch (Exception exception) {
			exception.printStackTrace();
			authResponse = new BaseEntity();
			authResponse.setMessage("Error");
		}
		return ResponseEntity.accepted().body(authResponse);
	}

	@GetMapping("/getrecentposts")
	public ResponseEntity<List<PostOutline>> findRecentPostOutlineList(HttpServletRequest request,
			HttpServletResponse response) {
		HttpHeaders requestheaders = new HttpHeaders();
		requestheaders.setContentType(MediaType.APPLICATION_JSON);
		List<PostOutline> responseList = postService.findRecentPostfromDB(request);
		if (responseList == null || responseList.isEmpty()) {
			return ((BodyBuilder) ResponseEntity.noContent().headers(requestheaders)).body(null);
		}
		return ResponseEntity.accepted().headers(requestheaders).body(responseList);
	}

	@GetMapping("/gettrendposts")
	public ResponseEntity<List<PostOutline>> findTrendsPostOutlineList(HttpServletRequest request,
			HttpServletResponse response) {
		HttpHeaders requestheaders = new HttpHeaders();
		requestheaders.setContentType(MediaType.APPLICATION_JSON);
		List<PostOutline> responseList = postService.findTrendingPostfromDB();
		if (responseList == null || responseList.isEmpty()) {
			return ((BodyBuilder) ResponseEntity.noContent().headers(requestheaders)).body(null);
		}
		return ResponseEntity.accepted().headers(requestheaders).body(responseList);
	}

	@GetMapping("/getoutlinerowscount")
	public ResponseEntity<Integer> countPostOutlineRowsDB(HttpServletRequest request, HttpServletResponse response) {
		Integer result = postService.getPostOutlineRowCount();
		return ResponseEntity.accepted().body(result);
	}

	@GetMapping("/getpostcontent")
	public ResponseEntity<PostContent> findPostContentById(HttpServletRequest request, HttpServletResponse response) {
		HttpHeaders requestheaders = new HttpHeaders();
		requestheaders.setContentType(MediaType.APPLICATION_JSON);
		PostContent content = postService.getContentFromDBById(request);
		if (content.getMessage() == null) {
			return ((BodyBuilder) ResponseEntity.noContent().headers(requestheaders)).body(content);
		}
		return ResponseEntity.accepted().headers(requestheaders).body(content);
	}

	@PostMapping("/subscribeUserEmail")
	public ResponseEntity<BaseEntity> addUserToMailingList(HttpServletRequest request, HttpServletResponse response) {
		HttpHeaders requestheaders = new HttpHeaders();
		requestheaders.setContentType(MediaType.APPLICATION_JSON);
		BaseEntity mailinglistEntity = userService.subscribeToBlog(request);
		return ResponseEntity.accepted().headers(requestheaders).body(mailinglistEntity);

	}

	@PostMapping("/createCategoryDb")
	public ResponseEntity<BaseEntity> makeCategoryEntryDB(HttpServletRequest request, HttpServletResponse response) {
		HttpHeaders requestheaders = new HttpHeaders();
		requestheaders.setContentType(MediaType.APPLICATION_JSON);
		BaseEntity baseResponse = new BaseEntity();
		String saveMessage = categoryService.saveCategory(request);
		baseResponse.setMessage(saveMessage);
		return ResponseEntity.accepted().headers(requestheaders).body(baseResponse);
	}

	@PostMapping("/createPostDb")
	public ResponseEntity<BaseEntity> makePostEntryDB(@RequestParam("postImage") CommonsMultipartFile file,
			HttpServletRequest request, HttpServletResponse response) {
		HttpHeaders requestheaders = new HttpHeaders();
		requestheaders.setContentType(MediaType.APPLICATION_JSON);
		BaseEntity baseResponse = new BaseEntity();
		if (postService.storePostContent(file, request) > 0) {
			baseResponse.setMessage("success");
			return ResponseEntity.accepted().headers(requestheaders).body(baseResponse);
		} else {
			baseResponse.setMessage("error");
			return ResponseEntity.badRequest().headers(requestheaders).body(baseResponse);
		}
	}

	@PostMapping("/createUserDb")
	public ResponseEntity<BaseEntity> makeUserEntryDB(@RequestBody User newuser,HttpServletRequest request, HttpServletResponse response) {
		HttpHeaders requestheaders = new HttpHeaders();
		requestheaders.setContentType(MediaType.APPLICATION_JSON);
		BaseEntity baseResponse = new BaseEntity();
		int saveMessage = userService.makeUserEntry(newuser);
		baseResponse.setMessage((saveMessage == 1)?"Success":"Failed");
		return ResponseEntity.accepted().headers(requestheaders).body(baseResponse);
	}

	@GetMapping("/getAllCategoryDb")
	public ResponseEntity<List<Blogcategory>> fetchCategoriesDB(HttpServletRequest request, HttpServletResponse response){
		HttpHeaders requestheaders = new HttpHeaders();
		requestheaders.setContentType(MediaType.APPLICATION_JSON);
		List<Blogcategory> categoryList = categoryService.fetchListDB(request);
		if(categoryList.isEmpty() || categoryList == null) {
			((BodyBuilder) ResponseEntity.noContent().headers(requestheaders)).body(categoryList);
		}
		return ResponseEntity.accepted().headers(requestheaders).body(categoryList);
	}
	
	@GetMapping("/getAllUserDb")
	public ResponseEntity<List<User>> fetchUsersDB(HttpServletRequest request, HttpServletResponse response){
		HttpHeaders requestheaders = new HttpHeaders();
		requestheaders.setContentType(MediaType.APPLICATION_JSON);
		List<User> users = userService.fetchUsers();
		if(users.isEmpty() || users == null) {
			((BodyBuilder) ResponseEntity.noContent().headers(requestheaders)).body(users);
		}
		return ResponseEntity.accepted().headers(requestheaders).body(users);
	}
	
	@GetMapping("/getAllPostDb")
	public ResponseEntity<List<PostOutline>> fetchPostsDB(HttpServletRequest request, HttpServletResponse response){
		HttpHeaders requestheaders = new HttpHeaders();
		requestheaders.setContentType(MediaType.APPLICATION_JSON);
		List<PostOutline> posts = postService.fetchAllPostsDB();
		if(posts.isEmpty() || posts == null) {
			((BodyBuilder) ResponseEntity.noContent().headers(requestheaders)).body(posts);
		}
		return ResponseEntity.accepted().headers(requestheaders).body(posts);
		
	}
	
	@GetMapping("/getCategoryPosts")
	public ResponseEntity<List<PostOutline>> getPostByCategory(HttpServletRequest request, HttpServletResponse response){
		HttpHeaders requestheaders = new HttpHeaders();
		requestheaders.setContentType(MediaType.APPLICATION_JSON);
		List<PostOutline> posts = postService.fetchPostsByCategory(request);
		if(posts.isEmpty() || posts == null) {
			((BodyBuilder) ResponseEntity.noContent().headers(requestheaders)).body(posts);
		}
		return ResponseEntity.accepted().headers(requestheaders).body(posts);
	}
	
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public ModelAndView getIndex(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView("index");
		return model;
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public ModelAndView getLogin(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView("login");
		return model;
	}
	@RequestMapping(value = "/admin/create_post", method = RequestMethod.GET)
	public ModelAndView getAdminNewPost(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView("admin/posts/create");
		return model;
	}

	@RequestMapping(value = "/admin/index_post", method = RequestMethod.GET)
	public ModelAndView getAdminIndexPost(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView("admin/posts/index");
		return model;
	}


	
}
