package com.wanasoft.test.restimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.wanasoft.test.POJO.Category;
import com.wanasoft.test.constents.CafeConstants;
import com.wanasoft.test.rest.CategoryRest;
import com.wanasoft.test.service.CategoryService;
import com.wanasoft.test.utils.CafeUtils;


@RestController
public class CategoryRestImpl implements CategoryRest {
	
	@Autowired
	CategoryService categoryService;

	@Override
	public ResponseEntity<String> addNewCategory(Map<String, String> requestMap) {
		try {
			
			return categoryService.addNewCategory(requestMap);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CafeUtils.grtResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	public ResponseEntity<List<Category>> getAllCategory(String filterValue) {
		try {
			
			return categoryService.getAllCategory(filterValue);
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		return new ResponseEntity<>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	public ResponseEntity<String> updateCategory(Map<String, String> requestMap) {
		try {
			
			return categoryService.updateCategory(requestMap);
			
		} catch (Exception e) {
			e.printStackTrace();
		}return CafeUtils.grtResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
