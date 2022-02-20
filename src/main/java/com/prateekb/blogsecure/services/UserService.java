package com.prateekb.blogsecure.services;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prateekb.blogsecure.dao.MailingRepository;
import com.prateekb.blogsecure.dao.UserRepository;
import com.prateekb.blogsecure.model.BaseEntity;
import com.prateekb.blogsecure.model.MailingList;
import com.prateekb.blogsecure.model.User;

@Service
public class UserService {

	@Autowired 
	UserRepository userRepo;
	
	@Autowired
	MailingRepository mailingRepo;
	
	@Transactional
	public BaseEntity subscribeToBlog(HttpServletRequest request) {
		
		// User Not logged In
		MailingList mailList = new MailingList();
		BaseEntity entity = new BaseEntity();
		String UserID = (request.getParameter("user_id") == null)? "0": request.getParameter("user_id");
		String EmailID = (request.getParameter("email") == null)? "NA": request.getParameter("email");
		
		int userID = new Integer(UserID);
		mailList.setUser_id(userID);
		mailList.setMailid(EmailID);
		String resultMsg =( mailingRepo.save(mailList) != null)? " Success":"Failed";
		
		entity.setMessage(resultMsg);
		return entity;
		
	}

	@Transactional
	public int makeUserEntry(User user) {
		int result = (userRepo.save(user)!= null) ? 1: 0; 
		return result;
	}
	
	@Transactional
	public List<User> fetchUsers(){
		return (List<User>) userRepo.findAll();
	}
	
	@Transactional
	private int countUsers() {
		int count = (int) userRepo.count();
		return count;
	}
	
	public User processRequest(JSONObject user) {
		User user1 = new User();
		user1.setUid(countUsers()+1);
		user1.setUsername(user.get("username").toString());
		user1.setEmail(user.get("email").toString());
		user1.setPassword(user.get("password").toString());
		
		user1.setRole_id((Integer)user.get("role_id"));
		String []interests = user.get("interests") == null ? new String[] {"NA"}:user.get("interests").toString().split(",");
		user1.setInterests(Arrays.asList(interests));
		Timestamp ts = new Timestamp(Long.valueOf(user.get("arr_time").toString())); 
		user1.setArr_time(ts);
		user1.setIs_subscribed(Boolean.parseBoolean(user.get("is_subscribed").toString()));
		return user1;
	}
	
	public int checkUserCred(HttpServletRequest request)
	{
		int result = 0;
		try
		{
			String username = request.getParameter("username");
			String password = request.getParameter("password");
			
			if(!username.isEmpty() && !password.isEmpty()) {
				User user = userRepo.findUserByCreds(username, password);
				if(user.equals(null))
				{
					result = 0;
				}
				else
				{
					if(username.compareTo(user.getUsername()) == 0 &&  password.compareTo(user.getPassword()) == 0)
					{
						result = 1;
					}
				}
			}
			
		}
		catch(Exception exception)
		{
			exception.printStackTrace();
		}
		return result;
		
	}

}
