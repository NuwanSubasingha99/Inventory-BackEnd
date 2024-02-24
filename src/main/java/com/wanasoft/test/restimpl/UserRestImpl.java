package com.wanasoft.test.restimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.wanasoft.test.constents.CafeConstants;

import com.wanasoft.test.rest.UserRest;
import com.wanasoft.test.service.UserService;
import com.wanasoft.test.utils.CafeUtils;
import com.wanasoft.test.wrapper.UserWrapper;


@RestController
public class UserRestImpl implements UserRest {
	
	
	@Autowired
	UserService userservice;
	@Override
	public ResponseEntity<String> signUp(Map<String, String> requestMap) {
		// TODO Auto-generated method stub
		
		try {
			
			
			return userservice.signUp(requestMap);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			
		}
		return CafeUtils.grtResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
		
	}

	@Override
	public ResponseEntity<String> login(Map<String, String> requestMap) {

		try {
			
			return userservice.login(requestMap);

		} catch (Exception e) {
			
			e.printStackTrace();

		}
		return CafeUtils.grtResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR); 

	}

	@Override
	public ResponseEntity<List<UserWrapper>> getAllUser() {

		try {
			
			return userservice.getAllUser();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<List<UserWrapper>>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	public ResponseEntity<String> update(Map<String, String> requestMap) {
		try {
			return userservice.update(requestMap);
			
		} catch (Exception e) {
			e.printStackTrace();
		
		}
		return CafeUtils.grtResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	public ResponseEntity<String> checkToken() {
		try {
			
			return userservice.checkToken();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CafeUtils.grtResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	public ResponseEntity<String> changePassword(Map<String, String> requestMap) {
		
		try {
			
			return userservice.changePassword(requestMap);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CafeUtils.grtResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);


	}

	@Override
	public ResponseEntity<String> forgotPassword(Map<String, String> requestMap) {
		try {
			
			return userservice.forgotPassword(requestMap);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CafeUtils.grtResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	

}
