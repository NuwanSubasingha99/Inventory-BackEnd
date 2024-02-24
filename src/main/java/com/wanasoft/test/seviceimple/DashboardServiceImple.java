package com.wanasoft.test.seviceimple;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.wanasoft.test.dao.BillDao;
import com.wanasoft.test.dao.CategoryDao;
import com.wanasoft.test.dao.ProductDao;
import com.wanasoft.test.service.DashboardService;


@Service
public class DashboardServiceImple implements DashboardService{
	
	@Autowired
	CategoryDao categoryDao;
	
	@Autowired
	ProductDao productDao;
	
	@Autowired
	BillDao billDao;

	@Override
	public ResponseEntity<Map<String, Object>> getCount() {
		
		Map<String, Object> map = new HashMap<>();
		map.put("category", categoryDao.count());
		map.put("product", productDao.count());
		map.put("bill", billDao.count());
		
		return new ResponseEntity<>(map,HttpStatus.OK);
	}

}
