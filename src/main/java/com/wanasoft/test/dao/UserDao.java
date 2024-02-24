package com.wanasoft.test.dao;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

import com.wanasoft.test.POJO.User;
import com.wanasoft.test.wrapper.UserWrapper;

public interface UserDao extends JpaRepository<User, Integer> {
	
	User findByEmailId(@Param("email")String email);
	
	List<UserWrapper> getAllUser();
	
	List<String> getAllAdmin();
	
	@Transactional
	@Modifying
	Integer updateStatus(@Param("status")String status,@Param("id")Integer id);
	
	User findByEmail(String email);
	
	
	

}
