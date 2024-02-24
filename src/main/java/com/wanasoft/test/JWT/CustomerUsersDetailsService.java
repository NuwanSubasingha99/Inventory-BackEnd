package com.wanasoft.test.JWT;

import java.util.ArrayList;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.User;

import com.wanasoft.test.dao.UserDao;
import com.wanasoft.test.seviceimple.UserServiceImple;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CustomerUsersDetailsService implements UserDetailsService {

	private static final Logger log = LoggerFactory.getLogger(UserServiceImple.class);
	
	@Autowired
	UserDao userDao;
	private com.wanasoft.test.POJO.User userDetail;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		log.info("loadUserByUsername {}",username);
		userDetail = userDao.findByEmailId(username);
		if(!Objects.isNull(userDetail))
			return new User(userDetail.getEmail(),userDetail.getPassword(),new ArrayList<>());
		else 
			throw new UsernameNotFoundException("User Not Found");
	}
	
	public com.wanasoft.test.POJO.User getUserDetail(){
		return userDetail;
	}

}
