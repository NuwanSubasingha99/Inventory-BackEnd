package com.wanasoft.test.restimpl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.wanasoft.test.POJO.Bill;
import com.wanasoft.test.constents.CafeConstants;
import com.wanasoft.test.rest.BillRest;
import com.wanasoft.test.service.BillService;
import com.wanasoft.test.utils.CafeUtils;

@RestController
public class BillRestImpl implements BillRest {
	
	@Autowired
	BillService billService;

	@Override
	public ResponseEntity<String> generateReport(Map<String, Object> requestMap) {
		try {
			return billService.generateReport(requestMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CafeUtils.grtResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	public ResponseEntity<List<Bill>> getBills() {
		
		try {
			
			return billService.getBills();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public ResponseEntity<byte[]> getPdf(Map<String, Object> requestMap) {
		try {
			
			return billService.getPdf(requestMap);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public ResponseEntity<String> deleteBill(Integer id) {
		try {
			
			return billService.deleteBill(id);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CafeUtils.grtResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}