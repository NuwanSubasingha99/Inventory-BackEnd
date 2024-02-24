package com.wanasoft.test.seviceimple;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;
import com.wanasoft.test.JWT.JwtFilter;
import com.wanasoft.test.POJO.Category;
import com.wanasoft.test.constents.CafeConstants;
import com.wanasoft.test.dao.CategoryDao;
import com.wanasoft.test.service.CategoryService;
import com.wanasoft.test.utils.CafeUtils;

@Service
public class CategorySreviceImpl implements CategoryService {
	
	@Autowired
	CategoryDao categoryDao;
	
	@Autowired
	JwtFilter jwtFilter;

	@Override
	public ResponseEntity<String> addNewCategory(Map<String, String> requestMap) {
		try {
			
			if(jwtFilter.isAdmin()) {
				
				if(validateCatrgoryMap(requestMap, false)) {
					
					categoryDao.save(getCategoryFromMap(requestMap, false));
					return CafeUtils.grtResponseEntity("Category Added Successfully", HttpStatus.OK);
					
				}
				
			}else {
				return CafeUtils.grtResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CafeUtils.grtResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private boolean validateCatrgoryMap(Map<String, String> requestMap, boolean validateId) {

		if(requestMap.containsKey("name")) {
			if(requestMap.containsKey("id") && validateId) {
				return true;
			}else if(!validateId){
				return true;
			}
				
		}
		return false;
	}
	
	
	private Category getCategoryFromMap(Map<String, String> requestMap, Boolean isAdd) {
		
		Category category = new Category();
		if(isAdd) {
			category.setId(Integer.parseInt(requestMap.get("id")));
		}
		category.setName(requestMap.get("name"));
		return category;
	}

	@Override
	public ResponseEntity<List<Category>> getAllCategory(String filterValue) {
		try {
			
			if(!Strings.isNullOrEmpty(filterValue) && filterValue.equalsIgnoreCase("true")) {
				return new ResponseEntity<List<Category>>(categoryDao.getAllCategory(),HttpStatus.OK);
			}
			return new ResponseEntity<>(categoryDao.findAll(),HttpStatus.OK);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<List<Category>>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	public ResponseEntity<String> updateCategory(Map<String, String> requestMap) {
		try {
			
			if(jwtFilter.isAdmin()) {
				
				if(validateCatrgoryMap(requestMap, true)) {
					Optional optional = categoryDao.findById(Integer.parseInt(requestMap.get("id")));
					if(!optional.isEmpty()) {
						
						categoryDao.save(getCategoryFromMap(requestMap, true));
						return CafeUtils.grtResponseEntity("Catrgory Updated Successfully", HttpStatus.OK);
						
					}else {
						return CafeUtils.grtResponseEntity("Category ID doesn't exist", HttpStatus.OK);
					}
				}
				return CafeUtils.grtResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
				
			}
			else {
				return CafeUtils.grtResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CafeUtils.grtResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	

}
