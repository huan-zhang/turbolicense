package com.turbo_license.common;

import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.turbo_license.dao.LicenseDAO;
import com.turbo_license.dao.LicenseInUseDAO;


public class CommonCache {
	static TreeMap<String, LicenseDAO> licenseMap = new TreeMap<String, LicenseDAO> ();
	static Logger logger = Logger.getLogger(CommonCache.class);

	
	public static void clearLicenseList () {
		licenseMap = new TreeMap<String, LicenseDAO> ();
	}
	
	public static void clearLicense(String keycode) {
		licenseMap.remove(keycode);
	}
	
	public static LicenseDAO getLicense (String keycode) {
		LicenseDAO ld = licenseMap.get(keycode);
		if (ld == null) {
			SqlConn con = null;
			ResultSet rs = null;
			String sql = "";
			ld = new LicenseDAO ();
			try {
				con = ConnFactory.getConnection("license");
				sql = "select id from license where (start_date < CURRENT_TIMESTAMP or start_date is NULL) " +
					"and (expire_date > CURRENT_TIMESTAMP or expire_date is NULL) " + 
					"and keycode = '" + keycode.trim().toUpperCase() + "'";
				logger.info("sql is " + sql);
				rs = con.query(sql);
				while (rs != null && rs.next()) {
					ld.setField("id", rs.getInt("id"));
					ld.load();
					licenseMap.put(keycode, ld);
				}		
			} catch (Exception e) {
				logger.error("Get License List Failed. " + e.getMessage(), e);
			} finally {
				con.close();
			}
		}
		return licenseMap.get(keycode);
	}
	
	public static HashMap<String, LicenseInUseDAO> getLicenseInUseList (int licenseId) {
		SqlConn con = null;
		ResultSet rs = null;
		String sql = "";
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		long now = (new Date()).getTime();

		HashMap <String, LicenseInUseDAO> liudList = new HashMap<String, LicenseInUseDAO>();
		try {
			con = ConnFactory.getConnection("license");
			sql = "select id from license_in_use where license_id = " + licenseId ;
			logger.info("sql is " + sql);
			rs = con.query(sql);
			while (rs != null && rs.next()) {
				LicenseInUseDAO liud = new LicenseInUseDAO();
				liud.setField("id", rs.getInt("id"));
				liud.load();
				try {
					String lastAccess = liud.getStrVal("last_access");
					Date lastAccessD = dateFormat.parse(lastAccess);
					long lastAccessL = lastAccessD.getTime();
					logger.info("now is " + now + "/// lastAccess is " + lastAccessL);
					logger.info("now - last is " + (now - lastAccessL));
					logger.info("Default is " + CommonAjax.DEFAULT_LICENSE_ACTIVE_TIME);
					if ((now - lastAccessL) > (CommonAjax.DEFAULT_LICENSE_ACTIVE_TIME )) { // delete invalid entrance
						liud.delete();
						continue;
					} 
				} catch (Exception e) {
					logger.error("Getting license failed: " + e.getMessage(), e);
				}
				liudList.put(liud.getStrVal("client_identifier"), liud);
			}		
		} catch (Exception e) {
			logger.error("Get License List Failed. " + e.getMessage(), e);
		} finally {
			con.close();
		}
		return liudList;
	}
}
