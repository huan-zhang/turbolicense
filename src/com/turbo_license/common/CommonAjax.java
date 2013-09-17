package com.turbo_license.common;

/*********************************************
 * Response to ajax request
 * Created by huan zhang, 09/04/2009
 * input:  request parameter: int currentPage, String item_id
 * output: ratings/comments as xml format
 * 
 */

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.sql.Blob;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import javax.crypto.Cipher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import com.turbo_license.dao.LicenseDAO;
import com.turbo_license.dao.LicenseInUseDAO;
import com.turbo_license.dao.UserDAO;
import com.turbo_license.license.License;
import com.turbo_license.utilities.Base64;
import com.turbo_license.utilities.Validator;

public class CommonAjax extends MasterServlet {
	
	Logger logger = Logger.getLogger(CommonAjax.class);
	static final int DEFAULT_LICENSE_ACTIVE_TIME = 30000;

	@Override
	public void init(ServletConfig config) throws ServletException {
		logger.info("this is init");
		super.init(config);
		ServletContext context = config.getServletContext();
		loadProps(context);
	}

	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.init();
		String func = request.getParameter("function");
		Element elResult = new Element("result");
		Method mthd;
		try {
			mthd = this.getClass().getDeclaredMethod(func, Class.forName("javax.servlet.http.HttpServletRequest"), elResult.getClass());
			elResult = (Element) mthd.invoke(this, request, elResult);
		} catch (Exception e) {
			logger.error("WebposAjax invoke error: " + e, e);
		}

		// OUTPUT TO CLIENT
		try {
			String msg = new XMLOutputter().outputString(new Document(elResult));
			response.setContentType("text/xml");
			response.setCharacterEncoding("UTF-8");
			OutputStream out = response.getOutputStream();
			out.write(msg.getBytes());
			out.close();
		} catch (Exception e) {
			logger.error("Failed to update DAO via AJAX " + e.getMessage(), e);
		} // end for
	} // end if

	private Element signup(HttpServletRequest request, Element elResult) {
		Element elResponse = new Element ("response");
		String username = request.getParameter("username");
		String password1 = request.getParameter("password1");
		String password2 = request.getParameter("password2");
		String email = request.getParameter("email");
		String fName = request.getParameter("f_name");
		String lName = request.getParameter("l_name");
		
		if (username == null || "".equals(username)) {
			elResponse.setAttribute("success", "0");
			elResponse.setAttribute("errmsg", "Username shouldn't be blank.");
			elResult.addContent(elResponse);
			return elResult;
		}
		if (password1 == null || "".equals(password1)) {
			elResponse.setAttribute("success", "0");
			elResponse.setAttribute("errmsg", "Password shouldn't be blank.");
			elResult.addContent(elResponse);
			return elResult;
		}
		if (password2 == null || "".equals(password2)) {
			elResponse.setAttribute("success", "0");
			elResponse.setAttribute("errmsg", "The repeated password shouldn't be blank.");
			elResult.addContent(elResponse);
			return elResult;
		}
		if (!password2.equals(password1)) {
			elResponse.setAttribute("success", "0");
			elResponse.setAttribute("errmsg", "asswords are not match.");
			elResult.addContent(elResponse);
			return elResult;
		}
		if (email == null || "".equals(email)) {
			elResponse.setAttribute("success", "0");
			elResponse.setAttribute("errmsg", "Email shouldn't be blank.");
			elResult.addContent(elResponse);
			return elResult;
		}
		Validator validator = new Validator(Validator.EMAIL_PATTERN);
		if(!validator.validate(email)) {
			elResponse.setAttribute("success", "0");
			elResponse.setAttribute("errmsg", "Email's format is not correct.");
			elResult.addContent(elResponse);
			return elResult;
		}
		SqlConn con = null;
		String sql = "";
		ResultSet rs = null;
		try {
			con = ConnFactory.getConnection("license");
			sql = "select count(*) as count from user where username = '" + username + "'";
			rs = con.query(sql);
			if (rs != null && rs.next()) {
				if (rs.getInt("count") > 0) {
					elResponse.setAttribute("success", "0");
					elResponse.setAttribute("errmsg", "The username exists.");
					elResult.addContent(elResponse);
					return elResult;
				}
			}
			rs = null;
			sql = "select count(*) as count from user where email = '" + email + "'";
			rs = con.query(sql);
			if (rs != null && rs.next()) {
				if (rs.getInt("count") > 0) {
					elResponse.setAttribute("success", "0");
					elResponse.setAttribute("errmsg", "The email address exists.");
					elResult.addContent(elResponse);
					return elResult;
				}
			}
			UserDAO userDao = new UserDAO();
			userDao.setField("username", username);
			userDao.setField("password", password1);
			userDao.setField("email", email);
			userDao.setField("first_name", fName);
			userDao.setField("last_name", lName);
			userDao.save();
			elResponse.setAttribute("success", "1");
			
		} catch (Exception e) {
			logger.error(e.getMessage());
			elResponse.setAttribute("success", "0");
			elResponse.setAttribute("errmsg", "inserting into db failed: " + e.getMessage());
		} finally {
			con.close();
		}
		elResult.addContent(elResponse);
		return elResult;
	}
	
	private Element login(HttpServletRequest request, Element elResult) {
		HttpSession session = request.getSession(true);

		Element elResponse = new Element ("response");
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		
		if (username == null || "".equals(username)) {
			elResponse.setAttribute("success", "0");
			elResponse.setAttribute("errmsg", "Username shouldn't be blank.");
			elResult.addContent(elResponse);
			return elResult;
		}
		if (password == null || "".equals(password)) {
			elResponse.setAttribute("success", "0");
			elResponse.setAttribute("errmsg", "Password shouldn't be blank.");
			elResult.addContent(elResponse);
			return elResult;
		}
		SqlConn con = null;
		String sql = "";
		ResultSet rs = null;
		try {
			con = ConnFactory.getConnection("license");
			sql = "select username, id from user where username = '" + username + "' and password = password('" + password + "')";
			logger.info("sql is " + sql);
			rs = con.query(sql);
			if (rs != null && rs.next() && rs.getInt("id") > 0) {
				session.setAttribute("username", username);
				session.setAttribute("user_id", rs.getString("id"));
				elResponse.setAttribute("success", "1");
			} else {
				elResponse.setAttribute("success", "0");
				elResponse.setAttribute("errmsg", "The username/password doesn't match any record in our database.");
			}
		} catch (Exception e) {
			elResponse.setAttribute("errmsg", "login failed: " + e.getMessage());
			elResponse.setAttribute("success", "0");
			logger.error(e.getMessage());
		} finally {
			con.close();
		}
		elResult.addContent(elResponse);
		return elResult;
	}
	
	private Element test(HttpServletRequest request, Element elResult) {
		Element elResponse = new Element ("response");
		elResponse.setAttribute("success", "1");
		elResult.addContent(elResponse);
		try {
			KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
			kpg.initialize(2048);
			KeyPair kp = kpg.genKeyPair();
			Key publicKey = kp.getPublic();
			Key privateKey = kp.getPrivate();
			logger.info("public key is " + publicKey);
			logger.info("private key is " + privateKey);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return elResult;
	}
	
	private Element updateVendor(HttpServletRequest request, Element elResult) {
			
		HttpSession session = request.getSession(true);
		Element elResponse = new Element ("response");
		
		if (!verifySession(request)) {
			elResponse.setAttribute("success", "-1");
			elResult.addContent(elResponse);
			return elResult;
		}
		
		String name = request.getParameter("vendor_name");
		String contact = request.getParameter("contact");
		String addr1 = request.getParameter("address1");
		String addr2 = request.getParameter("address2");
		String city = request.getParameter("city");
		String state = request.getParameter("state");
		String zip = request.getParameter("zip");
		String country = request.getParameter("country");
		String phone = request.getParameter("phone");
		String userId= (String) session.getAttribute("user_id");
		
		SqlConn con = null;
		ResultSet rs = null;
		try {
			con = ConnFactory.getConnection("license");
			String sql = "select * from vendor where user_id = '" + userId + "'";
			rs = con.query(sql);
			if (rs != null && rs.next()) {
				sql = "update vendor set " +
						" name = '" + name + "', " +
						" contact = '" + contact + "', " +
						" addr1 = '" + addr1 + "', " +
						" addr1 = '" + addr2 + "', " +
						" city = '" + city + "', " +
						" state = '" + state + "', " +
						" zip = '" + zip + "', " +
						" country = '" + country + "', " +
						" phone = '" + phone + "' " +
						" where user_id = '" + userId + "'";
			} else {
				sql = "insert into vendor values ('', " +
						"'" + name + "', " +
						"'" + addr1 + "', " +
						"'" + addr2 + "', " +
						"'" + contact + "', " +
						"'" + city + "', " +
						"'" + state + "', " +
						"'', " +
						"'" + zip + "', " +
						"'" + country + "'," +
						"'" + userId + "')";
			}
			logger.info("sql is " + sql);
			con.update(sql);
			elResponse.setAttribute("success", "1");
		} catch (Exception e) {
			elResponse.setAttribute("errmsg", e.getMessage());
			elResponse.setAttribute("success", "0");
			logger.error(e.getMessage());
		} finally {
			con.close();
		}
		elResult.addContent(elResponse);
		return elResult;
	}
	
	private Element updateProduct(HttpServletRequest request, Element elResult) {
		
		HttpSession session = request.getSession(true);
		Element elResponse = new Element ("response");
		
		if (!verifySession(request)) {
			elResponse.setAttribute("success", "-1");
			elResult.addContent(elResponse);
			return elResult;
		}
		
		
		String name = request.getParameter("name");
		String desc = request.getParameter("desc");
		String id = request.getParameter("id");
		String vendor_id = (String) session.getAttribute("user_id");
		
		SqlConn con = null;
		ResultSet rs = null;
		String sql = "";
		try {
			con = ConnFactory.getConnection("license");

			sql = "select count(*) as count from product where name = '" + name + "'";
			rs = con.query(sql);
			if (rs != null && rs.next()) {
				if (rs.getInt("count") > 0) {
					elResponse.setAttribute("success", "0");
					elResponse.setAttribute("errmsg", "The product exists.");
					elResult.addContent(elResponse);
					return elResult;
				}
			}
			if (!"".equals(id))
				sql = "update product set " +
						" name = '" + name + "', " +
						" description = '" + desc + "' " +
						" where id = '" + id + "'";
			else {
				sql = "insert into product values ('', " +
						"'" + name + "', " +
						"'" + desc + "', " +
						"'" + vendor_id + "')";
			}
			logger.info("sql is " + sql);
			con.update(sql);
			rs = null;
			sql = "select * from product where name = '" + name + "'";
			rs = con.query(sql);
			if(rs != null && rs.next()) {
				elResponse.setAttribute("prod_id", rs.getString("id"));
				elResponse.setAttribute("prod_name", name);
				elResponse.setAttribute("prod_desc", desc);
			}
			elResponse.setAttribute("success", "1");
		} catch (Exception e) {
			elResponse.setAttribute("errmsg", e.getMessage());
			elResponse.setAttribute("success", "0");
			logger.error(e.getMessage());
		} finally {
			con.close();
		}
		elResult.addContent(elResponse);
		return elResult;
	}
	
	private boolean verifySession(HttpServletRequest request) {
		HttpSession session = request.getSession(true);
		String userId = (String) session.getAttribute("user_id");
		return (userId != null && Integer.parseInt(userId) > 0);
	}

	private Element getFeatures(HttpServletRequest request, Element elResult) {
		Element elResponse = new Element ("response");
		String prodId = request.getParameter("prod_id");
		SqlConn con = null;
		ResultSet rs = null;
		String sql = "";
		try {
			con = ConnFactory.getConnection("license");
			sql = "select * from feature where product_id = '" + prodId + "' order by name";
			rs = con.query(sql);
			while (rs != null && rs.next()) {
				Element elFeature = new Element ("feature");
				elFeature.setAttribute("feature_id", rs.getString("id"));
				elFeature.setAttribute("feature_name", rs.getString("name"));
				elFeature.setAttribute("desc", rs.getString("description"));
				elResponse.addContent(elFeature);
			}
		} catch (Exception e) {
			logger.error("Failed to get features for product " + prodId + ": " + e.getMessage());
		} finally {
			con.close();
		}
		elResult.addContent(elResponse);
		return elResult;
	}
	
	private Element testJsonRpc(HttpServletRequest request, Element elResult) {
		Element elResponse = new Element ("response");
		String lat = request.getParameter("lat");
		String lng = request.getParameter("lng");
		String email = request.getParameter("email");
		String storeName = request.getParameter("store_name");
		String prod1 = request.getParameter("prod1");
		String prod2 = request.getParameter("prod2");
		String prod3 = request.getParameter("prod3");
		String prod4 = request.getParameter("prod4");
		String prod5 = request.getParameter("prod5");
		
		// make json request
		logger.info("Starting to send request to ecomm2");
		
		try {
			 
			URL url = new URL("http://www.kingarthurflour.com/services-test/resources/ProductService");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
	 
			String input = "{\"lng\":\"" + lng + "\"," 
			              + "\"lat\":\"" + lat + "\","
			              + "\"email\":\"" + email + "\","
			              + "\"store_name\":\"" + storeName + "\","
			              + "\"product1\":\"" + prod1 + "\","
			              + "\"product2\":\"" + prod2 + "\","
			              + "\"product3\":\"" + prod3 + "\","
			              + "\"product4\":\"" + prod4 + "\","
			              + "\"product5\":\"" + prod5 + "\"}";
			logger.info("input is " + input);
			OutputStream os = conn.getOutputStream();
			os.write(input.getBytes());
			os.flush();
	 
			/*if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
				throw new RuntimeException("Failed : HTTP error code : "
					+ conn.getResponseCode());
			}*/
	 
			BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));
	 
			String output;
			while ((output = br.readLine()) != null) {
				logger.info(output);
			}
	 
			conn.disconnect();
			elResponse.setAttribute("success", "1");
	 
		} catch (MalformedURLException e) {
			elResponse.setAttribute("success", "0");
			elResponse.setAttribute("errmsg", e.getMessage());
			logger.error("Failed:", e);
		} catch (IOException e) {
			elResponse.setAttribute("success", "0");
			elResponse.setAttribute("errmsg", e.getMessage());
			logger.error("Failed:", e);
		}
		elResult.addContent(elResponse);
		return elResult;
	}
	
	private Element verifyLicense(HttpServletRequest request, Element elResult) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		Element elResponse = new Element ("response");
		long now = (new Date()).getTime();
		String method = request.getMethod();
		logger.info("connection method is " + method);
		String license = request.getParameter("license");
		String identifier = request.getParameter("identifier");
		
		LicenseDAO ld = CommonCache.getLicense(license);
		if (license == null || identifier == null || "".equals(license) || "".equals(identifier)) {
			elResponse.setAttribute("success", "0");
			elResponse.setAttribute("errmsg", "Verify license failed: Parameter(s) are not valid.");
		} else if (ld == null || ld.getIntVal("id") <= 0) {
			elResponse.setAttribute("success", "0");
			elResponse.setAttribute("errmsg", "Verify license failed: The license is not valid.");
		} else {
			int maxNumber = ld.getIntVal("max_number");
			HashMap<String, LicenseInUseDAO> liudList = CommonCache.getLicenseInUseList(ld.getIntVal("id"));
			LicenseInUseDAO liud = liudList.get(identifier);
			if (liud == null) { // the client is not in use
				if (liudList.size() >= maxNumber) { // no more valid license
					elResponse.setAttribute("success", "0");
					elResponse.setAttribute("errmsg", "Verify license failed: The License has reached its limitation.");
				} else {
					liud = new LicenseInUseDAO();
					liud.setField("license_id", ld.getIntVal("id"));
					liud.setField("client_identifier", identifier);
					liud.setField("last_access", dateFormat.format(now));
					liud.save();
					elResponse.setAttribute("license", license);
					elResponse.setAttribute("last_access", liud.getStrVal("last_access"));
					elResponse.setAttribute("success", "1");
				}
			} else {
				liud.setField("last_access", dateFormat.format(now));
				liud.save();
				elResponse.setAttribute("license", license);
				elResponse.setAttribute("last_access", liud.getStrVal("last_access"));
				elResponse.setAttribute("success", "1");
			} 
		}
		/*
		String errMsg = "";
		LicenseDAO ld = null;
		for(int i = 0; i < ll.size(); i ++) {
			ld = ll.get(i);
			String lastAccess = ld.getStrVal("last_access");
			logger.info("last access is " + lastAccess);
			if (lastAccess == null) { // first time use
				ld.setField("last_access", now);
				ld.save();
				elResponse.setAttribute("license", license);
				elResponse.setAttribute("last_access", ld.getStrVal("last_access"));
				elResponse.setAttribute("success", "1");
				valid = true;
				break;
			} else {
				try {
					Date lastAccessD = dateFormat.parse(lastAccess);
					long lastAccessL = lastAccessD.getTime();
					if ((now - lastAccessL) > (CommonAjax.DEFAULT_LICENSE_ACTIVE_TIME )) {
						ld.setField("last_access", dateFormat.format(now));
						ld.save();
						elResponse.setAttribute("license", license);
						elResponse.setAttribute("last_access", ld.getStrVal("last_access"));
						elResponse.setAttribute("success", "1");
						valid = true;
						break;
					}
				
				} catch (Exception e) {
					logger.error("Verify license failed: " + e.getMessage(), e);
				} 
			}
		}  // end for loop
		if (!valid) {
			elResponse.setAttribute("success", "0");
			elResponse.setAttribute("errmsg", "Verify license failed: The license is not valid.");
		}
		*/
		elResult.addContent(elResponse);
		return elResult;
	}
	
	private Element testDatabase(HttpServletRequest request, Element elResult) {
		Element elResponse = new Element ("response");
		SqlConn con = null;
		ResultSet rs = null;
		String sql = "";
		try {
			con = ConnFactory.getConnection("license");
			sql = "select * from test";
			rs = con.query(sql);
			while (rs != null && rs.next()) {
				Element elTest = new Element ("test");
				elTest.setAttribute("db_id", rs.getString("id"));
				elResponse.addContent(elTest);
			}
		} catch (Exception e) {
			logger.error("Failed to get Test: " + e.getMessage(), e);
		} finally {
			con.close();
		}
		elResult.addContent(elResponse);
		return elResult;
	}
	
	private Element updateLicense(HttpServletRequest request, Element elResult) {
		HttpSession session = request.getSession(true);
		String userId = (String) session.getAttribute("user_id");
		String licenseId = request.getParameter("license_id");
		Element elResponse = new Element ("response");
		String licenseName = request.getParameter("license_name");
		String startDate = request.getParameter("start_date");
		String expireDate = request.getParameter("expire_date");
		String attr1 = request.getParameter("attr1");
		String attr2 = request.getParameter("attr2");
		String attr3 = request.getParameter("attr3");
		String attr4 = request.getParameter("attr4");
		String attr5 = request.getParameter("attr5");
		String maxNumber = request.getParameter("max_number");
		
		License license = new License();
		license.setId(Integer.parseInt(licenseId));
		license.load();
		license.setName(licenseName);
		license.setStartDate(startDate);
		license.setExpireDate(expireDate);
		license.setAttr1(attr1);
		license.setAttr2(attr2);
		license.setAttr3(attr3);
		license.setAttr4(attr4);
		license.setAttr5(attr5);
		license.setMaxNumber(Integer.parseInt(maxNumber));
		license.save();
		elResponse.setAttribute("success", "1");
		elResponse.setAttribute("license_name", license.getName());
		elResponse.setAttribute("start_date", license.getStartDate());
		elResponse.setAttribute("expire_date", license.getExpireDate());
		elResponse.setAttribute("keycode", license.getKeycode());
		elResponse.setAttribute("attr1", license.getAttr1());
		elResponse.setAttribute("attr2", license.getAttr2());
		elResponse.setAttribute("attr3", license.getAttr3());
		elResponse.setAttribute("attr4", license.getAttr4());
		elResponse.setAttribute("attr5", license.getAttr5());
		elResponse.setAttribute("max_number", "" + license.getMaxNumber());
		elResult.addContent(elResponse);
		return elResult;
	}
	private Element createNewLicense(HttpServletRequest request, Element elResult) {
		HttpSession session = request.getSession(true);
		String userId = (String) session.getAttribute("user_id");
		Element elResponse = new Element ("response");
		String licenseName = request.getParameter("license_name");
		String startDate = request.getParameter("start_date");
		String expireDate = request.getParameter("expire_date");
		String attr1 = request.getParameter("attr1");
		String attr2 = request.getParameter("attr2");
		String attr3 = request.getParameter("attr3");
		String attr4 = request.getParameter("attr4");
		String attr5 = request.getParameter("attr5");
		String maxNumber = request.getParameter("max_number");

		License license = new License();
		license.setName(licenseName);
		license.setKeycode(generateKeycode());
		license.setStartDate(startDate);
		license.setExpireDate(expireDate);
		license.setAttr1(attr1);
		license.setAttr2(attr2);
		license.setAttr3(attr3);
		license.setAttr4(attr4);
		license.setAttr5(attr5);
		license.setMaxNumber(Integer.parseInt(maxNumber));
		license.setUserId(userId);
		license.save();
		elResponse.setAttribute("success", "1");
		elResponse.setAttribute("license_id", "" + license.getId());
		elResponse.setAttribute("license_name", license.getName());
		elResponse.setAttribute("start_date", license.getStartDate());
		elResponse.setAttribute("expire_date", license.getExpireDate());
		elResponse.setAttribute("keycode", license.getKeycode());
		elResponse.setAttribute("attr1", license.getAttr1());
		elResponse.setAttribute("attr2", license.getAttr2());
		elResponse.setAttribute("attr3", license.getAttr3());
		elResponse.setAttribute("attr4", license.getAttr4());
		elResponse.setAttribute("attr5", license.getAttr5());
		elResponse.setAttribute("max_number", "" + license.getMaxNumber());
		elResult.addContent(elResponse);
		return elResult;
	}
	
	private Element generateLicense(HttpServletRequest request, Element elResult) {
		Element elResponse = new Element("response");
		String currentFile = System.currentTimeMillis() + "";
		try {
			String keycode = request.getParameter("license");
			License license = new License();
			license.loadByKeycode(keycode);
			if (license.getId() > 0) {
				String startDate = license.getCreatedDate() == null ? "" : license.getCreatedDate();
				String updatedExpireDate = license.getUpdatedExpireDate() == license.getExpireDate() ? "" : license.getUpdatedExpireDate();
				String attr1 = license.getAttr1() == null ? "" : license.getAttr1();
				String attr2 = license.getAttr2() == null ? "" : license.getAttr2();
				String attr3 = license.getAttr3() == null ? "" : license.getAttr3();
				String attr4 = license.getAttr4() == null ? "" : license.getAttr4();
				String attr5 = license.getAttr5() == null ? "" : license.getAttr5();
				int    maxNumber = license.getMaxNumber();
				String data = 	keycode + "|" +
								startDate + "|" +
								updatedExpireDate + "|" +
								attr1 + "|" + 
								attr2 + "|" + 
								attr3 + "|" + 
								attr4 + "|" + 
								attr5 + "|" +
								maxNumber;
				byte[] dataToEncrypt = data.getBytes("UTF-8");
				// get public key and secret key 
				//byte[] dataToEncrypt = "TURBO-LICENSE TEST 20130105".getBytes("UTF-16LE");
			    byte[] cipherData = null;
			    byte[] decryptedData;
	
	
			    KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
			    kpg.initialize(1024);
			    KeyPair kp = kpg.genKeyPair();
			    Key publicKey = kp.getPublic();
			    //String pubStr = new String(publicKey.getEncoded());
			    Blob pubBlob = new javax.sql.rowset.serial.SerialBlob(publicKey.getEncoded());
			    license.setPublicKey(pubBlob);
			    
			    Key privateKey = kp.getPrivate();
			    //String privateStr = new String(privateKey.getEncoded());
			    Blob privateBlob = new javax.sql.rowset.serial.SerialBlob(privateKey.getEncoded());
			    license.setPrivateKey(privateBlob);
			    
			    KeyFactory fact = KeyFactory.getInstance("RSA");
	
			    //RSAPublicKeySpec pub = (RSAPublicKeySpec) fact.getKeySpec(publicKey, RSAPublicKeySpec.class);
			    //RSAPublicKeySpec spec = new RSAPublicKeySpec(pub.getModulus(), pub.getPublicExponent());
			    RSAPrivateKeySpec prvt = (RSAPrivateKeySpec) fact.getKeySpec(privateKey, RSAPrivateKeySpec.class);
			    RSAPrivateKeySpec spec = new RSAPrivateKeySpec(prvt.getModulus(), prvt.getPrivateExponent());
			    
			    KeyFactory factory = KeyFactory.getInstance("RSA");
	
			    //PublicKey publicKeyRSA = factory.generatePublic(spec);
			    PrivateKey privateKeyRSA = factory.generatePrivate(spec);
			    Cipher cipher = Cipher.getInstance("RSA");
			    String cipherStr = "";
			    try {
		            cipher.init(Cipher.ENCRYPT_MODE, privateKeyRSA);
		            cipherData = cipher.doFinal(dataToEncrypt);
		            //cipherStr = new String(cipherData);
		            Blob cipherBlob = new javax.sql.rowset.serial.SerialBlob(cipherData);
		            license.setEncryptedLicense(cipherBlob);
		            String oldFile = license.getFileName();
		            license.setFileName(currentFile);
		            license.save();
		            // save to file
		            String fileName = currentFile + ".public.key";
		            byte[] publicBytes = pubBlob.getBytes(1, (int)pubBlob.length());
		            saveToFile(fileName, publicBytes);
		            fileName = currentFile + ".license";
		            saveToFile(fileName, cipherData);
		            removeOldFiles(oldFile);
		            elResponse.setAttribute("success", "1");
		            elResponse.setAttribute("public_key_url", server + ctx + downloadPath + "/" + currentFile + ".public.key");
		            elResponse.setAttribute("encrypted_license_url", server + ctx + downloadPath + "/" + currentFile + ".license");
		            logger.info("Encrypted license is " + new String(cipherData));
		        } catch (Exception e1) {
		            logger.error("Encrypt error: " + e1.getMessage(), e1);
		            elResponse.setAttribute("success", "0");
		            elResponse.setAttribute("errmsg", "Encrypt license error");
		        }
		        
		        // decrypt
		        // known pubStr, privateStr, and cipherData
		        
		        license = new License();
		        license.loadByKeycode(keycode);
		        
		        byte[] DCipherData = license.getEncryptedLicense().getBytes(1, (int)license.getEncryptedLicense().length());
		        PublicKey DPublicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(license.getPublicKey().getBytes(1, (int) license.getPublicKey().length())));
		        // byte[] b = Base64.base64Encode(cipherData);
		        byte[] b = Base64.base64Encode(DCipherData);
		        //cipher.init(Cipher.DECRYPT_MODE, privateKey);
		        cipher.init(Cipher.DECRYPT_MODE, DPublicKey);
		        try {
		            decryptedData = cipher.doFinal(Base64.base64Decode(b));
		            logger.info("Decrypted: " + new String(decryptedData, "UTF-8"));
		        } catch (Exception e1) {
		            logger.error("Decrypt error: " + e1.getMessage(), e1 );
		        }
	/*
			    int k = 0;
			    for (int i = 0; i < 100; i++) {
			        try {
			            cipher.init(Cipher.ENCRYPT_MODE, publicKeyRSA);
			            cipherData = cipher.doFinal(dataToEncrypt);
			        } catch (Exception e1) {
			            System.out.println("Encrypt error");
			        }
	
			        byte[] b = Base64.base64Encode(cipherData);
			        cipher.init(Cipher.DECRYPT_MODE, privateKey);
	
			        try {
			            decryptedData = cipher.doFinal(Base64.base64Decode(b));
			            logger.info("Decrypted: "
			                    + new String(decryptedData, "UTF-16LE"));
			            k += 1;
			        } catch (Exception e1) {
			            System.out.println("Decrypt error");
			        }
			    }
			   logger.info("Number of correct decryptions is: " + k);
			   */
				
			} else {
				elResponse.setAttribute("errmsg", "The license does not exist!");
				elResponse.setAttribute("success", "0");
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		elResult.addContent(elResponse);
		return elResult;
	}

	public String getKeyString(BigInteger mod, BigInteger exp) throws IOException {
		return (mod + "") + (exp + "");
	}
	
	public void saveToFile(String fileName, byte[] bytes) {
		fileName = path + downloadPath + "/" + fileName;
		//fileName = "/var/lib/tomcat5/webapps/turbo-license/download/909090.public.key";
		logger.info("filename is " + fileName);
		FileOutputStream output;
		try {
			output = new FileOutputStream(new File(fileName));
			output.write(bytes);
			output.close();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("output to file failed: " + e.getMessage(), e);
		}
	}
	
	public void saveToFile(String fileName, BigInteger mod, BigInteger exp) throws IOException {
		  ObjectOutputStream oout = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(fileName)));
		  try {
		    oout.writeObject(mod);
		    oout.writeObject(exp);
		  } catch (Exception e) {
		    throw new IOException("Unexpected error", e);
		  } finally {
		    oout.close();
		  }
	}
	
	private void removeOldFiles(String fileName) {
		if (!"".equals(fileName)) {
			String fileName1 = path + downloadPath + "/" + fileName + ".public.key";
			String fileName2 = path + downloadPath + "/" + fileName + ".license";
			try{
				 
	    		File file1 = new File(fileName1);
	    		File file2 = new File(fileName2);
	    		file1.delete();
	    		file2.delete();
	       	} catch(Exception e) {
	    		logger.error("delete file error" + e.getMessage(), e);
	    	}
		}
	}
	
	private String generateKeycode() {
		String keycode = "";
		SqlConn con = null;
		ResultSet rs = null;
		String sql = "";
		ArrayList<String> currentKeyCodes = new ArrayList<String>();
		try {
			con = ConnFactory.getConnection("license");
			sql = "select keycode from license";
			rs = con.query(sql);
			while (rs != null && rs.next()) {
				currentKeyCodes.add(rs.getString("keycode"));
			}
		} catch (Exception e) {
			logger.error("Failed to get existing keycodes: " + e.getMessage(), e);
		} finally {
			con.close();
		}
		do {
			long seed = System.currentTimeMillis();
			Random random = new Random();
			random.setSeed(seed);
			long tmp = 1000000000000000L;
			tmp = (random.nextLong() % tmp) + tmp;
			keycode = "TL" + random.nextLong();
		} while (currentKeyCodes.contains(keycode));
		logger.info("generated keycode is " + keycode);
		return keycode;
		
	}
	
	
	private Element encryptLicense(HttpServletRequest request, Element elResult) {
		Element elResponse = new Element("response");
		String currentFile = System.currentTimeMillis() + "";
		try {
			String licenseId = request.getParameter("license_id");
			License license = new License();
			license.setId(Integer.parseInt(licenseId));
			license.load();
			if (license.getId() > 0) {
				String keycode = license.getKeycode();
				String startDate = license.getCreatedDate() == null ? "" : license.getCreatedDate();
				String updatedExpireDate = license.getUpdatedExpireDate() == license.getExpireDate() ? "" : license.getUpdatedExpireDate();
				String attr1 = license.getAttr1() == null ? "" : license.getAttr1();
				String attr2 = license.getAttr2() == null ? "" : license.getAttr2();
				String attr3 = license.getAttr3() == null ? "" : license.getAttr3();
				String attr4 = license.getAttr4() == null ? "" : license.getAttr4();
				String attr5 = license.getAttr5() == null ? "" : license.getAttr5();
				int    maxNumber = license.getMaxNumber();
				String data = 	keycode + "|" +
								startDate + "|" +
								updatedExpireDate + "|" +
								attr1 + "|" + 
								attr2 + "|" + 
								attr3 + "|" + 
								attr4 + "|" + 
								attr5 + "|" +
								maxNumber;
				byte[] dataToEncrypt = data.getBytes("UTF-8");
				// get public key and secret key 
				//byte[] dataToEncrypt = "TURBO-LICENSE TEST 20130105".getBytes("UTF-16LE");
			    byte[] cipherData = null;
			    byte[] decryptedData;
	
	
			    KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
			    kpg.initialize(1024);
			    KeyPair kp = kpg.genKeyPair();
			    Key publicKey = kp.getPublic();
			    //String pubStr = new String(publicKey.getEncoded());
			    Blob pubBlob = new javax.sql.rowset.serial.SerialBlob(publicKey.getEncoded());
			    license.setPublicKey(pubBlob);
			    
			    Key privateKey = kp.getPrivate();
			    //String privateStr = new String(privateKey.getEncoded());
			    Blob privateBlob = new javax.sql.rowset.serial.SerialBlob(privateKey.getEncoded());
			    license.setPrivateKey(privateBlob);
			    
			    KeyFactory fact = KeyFactory.getInstance("RSA");
	
			    //RSAPublicKeySpec pub = (RSAPublicKeySpec) fact.getKeySpec(publicKey, RSAPublicKeySpec.class);
			    //RSAPublicKeySpec spec = new RSAPublicKeySpec(pub.getModulus(), pub.getPublicExponent());
			    RSAPrivateKeySpec prvt = (RSAPrivateKeySpec) fact.getKeySpec(privateKey, RSAPrivateKeySpec.class);
			    RSAPrivateKeySpec spec = new RSAPrivateKeySpec(prvt.getModulus(), prvt.getPrivateExponent());
			    
			    KeyFactory factory = KeyFactory.getInstance("RSA");
	
			    //PublicKey publicKeyRSA = factory.generatePublic(spec);
			    PrivateKey privateKeyRSA = factory.generatePrivate(spec);
			    Cipher cipher = Cipher.getInstance("RSA");
			    String cipherStr = "";
			    try {
		            cipher.init(Cipher.ENCRYPT_MODE, privateKeyRSA);
		            cipherData = cipher.doFinal(dataToEncrypt);
		            //cipherStr = new String(cipherData);
		            Blob cipherBlob = new javax.sql.rowset.serial.SerialBlob(cipherData);
		            license.setEncryptedLicense(cipherBlob);
		            String oldFile = license.getFileName();
		            license.setFileName(currentFile);
		            license.save();
		            // save to file
		            String fileName = currentFile + ".public.key";
		            byte[] publicBytes = pubBlob.getBytes(1, (int)pubBlob.length());
		            saveToFile(fileName, publicBytes);
		            fileName = currentFile + ".license";
		            saveToFile(fileName, cipherData);
		            removeOldFiles(oldFile);
		            elResponse.setAttribute("success", "1");
		            elResponse.setAttribute("public_key_url", server + ctx + downloadPath + "/" + currentFile + ".public.key");
		            elResponse.setAttribute("encrypted_license_url", server + ctx + downloadPath + "/" + currentFile + ".license");
		            logger.info("Encrypted license is " + new String(cipherData));
		        } catch (Exception e1) {
		            logger.error("Encrypt error: " + e1.getMessage(), e1);
		            elResponse.setAttribute("success", "0");
		            elResponse.setAttribute("errmsg", "Encrypt license error");
		        }
				
			} else {
				elResponse.setAttribute("errmsg", "The license does not exist!");
				elResponse.setAttribute("success", "0");
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		elResult.addContent(elResponse);
		return elResult;
	}

}
