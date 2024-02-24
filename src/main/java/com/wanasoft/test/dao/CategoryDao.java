package com.wanasoft.test.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wanasoft.test.POJO.Category;

public interface CategoryDao extends JpaRepository<Category, Integer> {
	
	List<Category> getAllCategory();

}
