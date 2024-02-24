package com.wanasoft.test.seviceimple;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;
import com.wanasoft.test.JWT.CustomerUsersDetailsService;
import com.wanasoft.test.JWT.JwtFilter;
import com.wanasoft.test.JWT.JwtUtil;
import com.wanasoft.test.POJO.User;
import com.wanasoft.test.constents.CafeConstants;
import com.wanasoft.test.dao.UserDao;
import com.wanasoft.test.service.UserService;
import com.wanasoft.test.utils.CafeUtils;
import com.wanasoft.test.utils.EmailUtils;
import com.wanasoft.test.wrapper.UserWrapper;

import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.*;


@Slf4j
@Service
public class UserServiceImple implements UserService {
	
	
	private static final Logger log = LoggerFactory.getLogger(UserServiceImple.class);

	
	@Autowired
	UserDao userDao;
	
	@Autowired
	AuthenticationManager authenticationManager;
	
	@Autowired
	CustomerUsersDetailsService customerUsersDetailsService;
	
	@Autowired
	JwtUtil jwtUtil;
	
	@Autowired
	JwtFilter jwtfilter;
	
	@Autowired
	EmailUtils emailUtils;
	
	@Override
	public ResponseEntity<String> signUp(Map<String, String> requestMap) {
		
		log.info("Inside signup{}",requestMap);
		try {
		if (validateSignUpMap(requestMap)) {
			
			User user = userDao.findByEmailId(requestMap.get("email"));
			if(Objects.isNull(user)) {
				
				userDao.save(getUserFromMap(requestMap));
				return CafeUtils.grtResponseEntity("Successfully Registerd", HttpStatus.OK);
			}
			else {
				return CafeUtils.grtResponseEntity("Email Alredy Exits.",HttpStatus.BAD_REQUEST);
			}
		}
		else {
			return CafeUtils.grtResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
		}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return CafeUtils.grtResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);


		
		
		
	}
	
	private boolean validateSignUpMap(Map<String, String> reqeustMap) {
		
		if (reqeustMap.containsKey("name") && reqeustMap.containsKey("contactNumber") && reqeustMap.containsKey("email") && reqeustMap.containsKey("password"))
		{
			return true;
		}
		return false;
		
		
		
	}
	
	
	private User getUserFromMap(Map<String, String> requestMap) {
		User user = new User();
		user.setName(requestMap.get("name"));
		user.setContactNumber(requestMap.get("contactNumber"));
		user.setEmail(requestMap.get("email"));
		user.setPassword(requestMap.get("password"));
		user.setStatus("false");
		user.setRole("user");
		return user;
		
		
	}

	@Override
	public ResponseEntity<String> login(Map<String, String> requestMap) {
		
		log.info("Inside Login");
		try {
			
			org.springframework.security.core.Authentication auth = authenticationManager.authenticate(
					
					new UsernamePasswordAuthenticationToken(requestMap.get("email"), requestMap.get("password"))
					
					
					);
			if(auth.isAuthenticated()) {
				if(customerUsersDetailsService.getUserDetail().getStatus().equalsIgnoreCase("true")) {
					
					return new ResponseEntity<String>("{\"token\":\""+
					      jwtUtil.generateToken(customerUsersDetailsService.getUserDetail().getEmail(),
					    		  customerUsersDetailsService.getUserDetail().getRole()) +"\"}",
							HttpStatus.OK);
					
				}else {
					return new ResponseEntity<String>("{\"massage\":\""+"Wait for Admin Approval."+"\"}",HttpStatus.BAD_REQUEST);
				}
			}
			
		} catch (Exception e) {
			
			log.error("{}",e);
			e.printStackTrace();
			


		}
		return new ResponseEntity<String>("{\"massage\":\""+"Bad Credentials."+"\"}",HttpStatus.BAD_REQUEST);
	}

	@Override
	public ResponseEntity<List<UserWrapper>> getAllUser() {
		try {
			
			if(jwtfilter.isAdmin()) {
				
				return new ResponseEntity<>(userDao.getAllUser(),HttpStatus.OK);				
			}else {
				return new ResponseEntity<>(new ArrayList<>(),HttpStatus.UNAUTHORIZED);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	public ResponseEntity<String> update(Map<String, String> requestMap) {
		try {
			
			if(jwtfilter.isAdmin()) {
				
				Optional<User> optional = userDao.findById(Integer.parseInt(requestMap.get("id")));
				if(!optional.isEmpty()) {
					
					userDao.updateStatus(requestMap.get("status"),Integer.parseInt(requestMap.get("id")));
					sendMailToAllAdmin(requestMap.get("status"),optional.get().getEmail(),userDao.getAllAdmin());
					return CafeUtils.grtResponseEntity("User Status Updated Successfully", HttpStatus.OK);
					
					
				}else {
					return CafeUtils.grtResponseEntity("User Id does not exist", HttpStatus.OK);
				}
				
			}else {
				return CafeUtils.grtResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CafeUtils.grtResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private void sendMailToAllAdmin(String status, String user, List<String> allAdmin) {
		
		allAdmin.remove(jwtfilter.getCurrentUser());
		if(status!=null && status.equalsIgnoreCase("true")) {
			emailUtils.sendSimpleMessage(jwtfilter.getCurrentUser(), "Account Approved", "USER:- "+user+"\n is approved by \nADMIN:-"+ jwtfilter.getCurrentUser(), allAdmin);
		}else {
			emailUtils.sendSimpleMessage(jwtfilter.getCurrentUser(), "Account Disabled", "USER:- "+user+"\n is Disabled by \nADMIN:-"+ jwtfilter.getCurrentUser(), allAdmin);
			
			
			
		}
		
		
		
	}

	@Override
	public ResponseEntity<String> checkToken() {
		
		return CafeUtils.grtResponseEntity("true", HttpStatus.OK);
	}

	@Override
	public ResponseEntity<String> changePassword(Map<String, String> requestMap) {
		try {
			
			User userObj = userDao.findByEmail(jwtfilter.getCurrentUser());
			if (!userObj.equals(null)) {
				
				if(userObj.getPassword().equals(requestMap.get("oldPassword"))) {
					
					userObj.setPassword(requestMap.get("newPassword"));
					userDao.save(userObj);
					return CafeUtils.grtResponseEntity("Password Updated Successfully", HttpStatus.OK);
					
				}
				return CafeUtils.grtResponseEntity("INCORRECT OLD PASSWORD", HttpStatus.BAD_REQUEST);
				
			}
			CafeUtils.grtResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CafeUtils.grtResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
		
	}

	@Override
	public ResponseEntity<String> forgotPassword(Map<String, String> requestMap) {
		try {
			
			User user = userDao.findByEmail(requestMap.get("email"));
			if(!Objects.isNull(user) && !Strings.isNullOrEmpty(user.getEmail()) )
				
				emailUtils.forgotMail(user.getEmail(), "Credentials by Inventory Manegement System", user.getPassword());
				
				
				
			return CafeUtils.grtResponseEntity("Check Your Email for Credentials.", HttpStatus.OK);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CafeUtils.grtResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	

}
