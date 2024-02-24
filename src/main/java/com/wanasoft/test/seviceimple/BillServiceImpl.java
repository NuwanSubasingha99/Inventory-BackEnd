package com.wanasoft.test.seviceimple;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.pdfbox.io.IOUtils;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.wanasoft.test.JWT.JwtFilter;
import com.wanasoft.test.POJO.Bill;
import com.wanasoft.test.constents.CafeConstants;
import com.wanasoft.test.dao.BillDao;
import com.wanasoft.test.service.BillService;
import com.wanasoft.test.utils.CafeUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BillServiceImpl implements BillService {
	
	@Autowired
	JwtFilter jwtFilter;
	
	@Autowired
	BillDao billDao;
	
	
	private static final Logger log = LoggerFactory.getLogger(UserServiceImple.class);

	@Override
	public ResponseEntity<String> generateReport(Map<String, Object> requestMap) {
		log.info("Inside GenerateReport");
		try {
			
			String fileName;
			if(validateRequestMap(requestMap)) {
				if(requestMap.containsKey("isGenerate") && !(Boolean) requestMap.get("isGenerate")) {
					fileName =(String) requestMap.get("uuid");
				}else {
					fileName = CafeUtils.getUUID();
					requestMap.put("uuid", fileName);
					insertBill(requestMap);
				}
				
				String data = "Name: "+requestMap.get("name")+"/n"+"Contact Number: "+requestMap.get("contactNumber")+
						"/n"+"Email: "+requestMap.get("email")+"/n"+"Payment Method: "+requestMap.get("paymentMethod");
				
				Document document = new Document();
				PdfWriter.getInstance(document, new FileOutputStream(CafeConstants.STORE_LOCATION+"//"+fileName+".pdf"));
				
				
				document.open();
				setRectangalInPdf(document);
				
				Paragraph chunk = new Paragraph("Inventory Management System",getFont("Header"));
				chunk.setAlignment(Element.ALIGN_CENTER);
				document.add(chunk);
				
				Paragraph paragraph = new Paragraph(data+"\n \n",getFont("Data"));
				document.add(paragraph);
				
				PdfPTable table = new PdfPTable(5);
				table.setWidthPercentage(100);
				addTableHeader(table);
				
				JSONArray jsonArray = CafeUtils.getJsonArrayFromString((String)requestMap.get("productDetails"));
				for(int i = 0; i < jsonArray.length(); i++) {
					addRows(table, CafeUtils.grtMapFromJson(jsonArray.getString(i)));
					
				}
				document.add(table);
				Paragraph footer = new Paragraph("Total : "+requestMap.get("totalAmount")+"\n"
						+"Thank You for Visiting ,Come Again!",getFont("Data"));
				document.add(footer);
				document.close();
				return new ResponseEntity<>("{\"uuid\":\""+fileName+"\"}",HttpStatus.OK);
					
				
			}
			return CafeUtils.grtResponseEntity("Required Data not Found", HttpStatus.BAD_REQUEST);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CafeUtils.grtResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private void addRows(PdfPTable table, Map<String, Object> data) {
		log.info("Inside addRows");
		table.addCell((String)data.get("name"));
		table.addCell((String) data.get("category"));
		table.addCell((String) data.get("quantity"));
		table.addCell(Double.toString((Double)data.get("price")));
		table.addCell(Double.toString((Double)data.get("total")));
		
	}

	private void addTableHeader(PdfPTable table) {
		log.info("inside addTableHeader");
		Stream.of("Name","Category","Quantity","Price","Sub Total").forEach(columnTitle ->{
			PdfPCell header = new PdfPCell();
			header.setBackgroundColor(BaseColor.LIGHT_GRAY);
			header.setBorderWidth(2);
			header.setPhrase(new Phrase(columnTitle));
			header.setBackgroundColor(BaseColor.YELLOW);
			header.setHorizontalAlignment(Element.ALIGN_CENTER);
			header.setVerticalAlignment(Element.ALIGN_CENTER);
			table.addCell(header);
		});
		
		
		
		
	}

	private Font getFont(String type) {
		log.info("Inside getfont");
		switch (type) {
		case "Header":
			Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLDOBLIQUE ,18,BaseColor.BLACK);
			headerFont.setStyle(Font.BOLD);
			return headerFont;
			
		case "Data":
			Font dataFont = FontFactory.getFont(FontFactory.TIMES_ROMAN,11,BaseColor.BLACK);
			dataFont.setStyle(Font.BOLD);
			return dataFont;
			
		default :
			return new Font();
		}
	}

	private void setRectangalInPdf(Document document) throws DocumentException {
		log.info("Inside setRectangalInPdf");
		Rectangle rect = new Rectangle(577,825,18,15);
		rect.enableBorderSide(1);
		rect.enableBorderSide(2);
		rect.enableBorderSide(4);
		rect.enableBorderSide(8);
		rect.setBorderColor(BaseColor.BLACK);
		rect.setBorderWidth(1);
		document.add(rect);
		
		
	}

	private void insertBill(Map<String, Object> requestMap) {
		try {
			
			Bill bill = new Bill();
			bill.setUuid((String) requestMap.get("uuid"));
			bill.setName((String)requestMap.get("name"));
			bill.setEmail((String)requestMap.get("email"));
			bill.setContactNumber((String)requestMap.get("contactNumber"));
			bill.setPaymentMethod((String)requestMap.get("paymentMethod"));
			bill.setTotal(Integer.parseInt((String)requestMap.get("totalAmount")));
			bill.setProductDetails((String)requestMap.get("productDetails"));
			bill.setCreatedBy(jwtFilter.getCurrentUser());
			billDao.save(bill);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	private boolean validateRequestMap(Map<String, Object> requestMap) {
		
		return requestMap.containsKey("name") && 
				requestMap.containsKey("contactNumber") && 
				requestMap.containsKey("email") && 
				requestMap.containsKey("paymentMethod") &&
				requestMap.containsKey("productDetails") &&
				requestMap.containsKey("totalAmount");
	}

	@Override
	public ResponseEntity<List<Bill>> getBills() {
		
		List<Bill> list = new ArrayList<>();
		if(jwtFilter.isAdmin()) {
			
			list = billDao.getAllBills();
			
			
			
		}else {
			
			list = billDao.getBillByUserName(jwtFilter.getCurrentUser());
			
		}
		
		return new ResponseEntity<>(list, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<byte[]> getPdf(Map<String, Object> requestMap) {
		
		log.info("Inside requestMap{}",requestMap);
		
		try {
			
			byte[] byteArray = new byte[0];
			if(!requestMap.containsKey("uuid") && validateRequestMap(requestMap)) {
				
				return new ResponseEntity<>(byteArray, HttpStatus.BAD_REQUEST);
				
			}
			String filePath = CafeConstants.STORE_LOCATION+"\\"+(String) requestMap.get("uuid")+".pdf" ;
			
			if (CafeUtils.isFileExist(filePath)) {
				byteArray = getByteArray(filePath);
				return new ResponseEntity<>(byteArray,HttpStatus.OK);
			}else {
				requestMap.put("isGenarate", false);
				generateReport(requestMap);
				byteArray = getByteArray(filePath);
				return new ResponseEntity<>(byteArray,HttpStatus.OK);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	private byte[] getByteArray(String filePath) throws Exception {
		File initialFile = new File(filePath);
		InputStream targetStream = new FileInputStream(initialFile);
		byte[] byteArray = IOUtils.toByteArray(targetStream);
		targetStream.close();
		return byteArray;
		
		
		
	}

	@Override
	public ResponseEntity<String> deleteBill(Integer id) {
		try {
			
			Optional optional = billDao.findById(id);
			if(!optional.isEmpty()) {
				
				billDao.deleteById(id);
				return CafeUtils.grtResponseEntity("Bill Deleted Successfully", HttpStatus.OK);
				
			}
			return CafeUtils.grtResponseEntity("Bill Id Does not exist", HttpStatus.OK);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CafeUtils.grtResponseEntity(CafeConstants.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
