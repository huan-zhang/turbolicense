package com.turbo_license.dao;

import java.sql.ResultSet;

import org.jdom.Element;

import com.turbo_license.common.ConnFactory;
import com.turbo_license.common.SqlConn;

/*
 * dao for page-level collection of gallery regions
 * will need title (for <title> tag), possibly CSS filename, 
 * main feature zone, and child regions in sequence
 */



public class LicenseDAO extends MasterDAO {
	
	public LicenseDAO() {

		super();
		PK = "id";
		DB = "license";
		table = "license";
		fields.put("id", new Field("id", DAOutils.INT_TYPE, "License ID", false));
		fields.put("keycode", new Field("keycode", DAOutils.STRING_TYPE, "Keycode", true));
		fields.put("name", new Field("name", DAOutils.STRING_TYPE, "Name", true));
		fields.put("created_date", new Field("created_date", DAOutils.DATE_TYPE, "Created Date", false));
		fields.put("start_date", new Field("start_date",  DAOutils.DATE_TYPE, "Start Date", true));
		fields.put("expire_date", new Field("expire_date",  DAOutils.DATE_TYPE, "Expire Date", true));
		fields.put("updated_expire_date", new Field("updated_expire_date",  DAOutils.DATE_TYPE, "Updated Expire Date", true));
		fields.put("max_number", new Field("max_number", DAOutils.INT_TYPE, "Max Number", true));
		fields.put("private_key", new Field("private_key", DAOutils.BLOB_TYPE, "Private Key", true));
		fields.put("public_key", new Field("public_key", DAOutils.BLOB_TYPE, "Public Key", true));
		fields.put("encrypted_license", new Field("encrypted_license", DAOutils.BLOB_TYPE, "Encrypted License", true));
		fields.put("file_name", new Field("file_name", DAOutils.STRING_TYPE, "File Name", true));		
		fields.put("attr1", new Field("attr1", DAOutils.STRING_TYPE, "Attr1", true));
		fields.put("attr2", new Field("attr2", DAOutils.STRING_TYPE, "Attr2", true));
		fields.put("attr3", new Field("attr3", DAOutils.STRING_TYPE, "Attr3", true));
		fields.put("attr4", new Field("attr4", DAOutils.STRING_TYPE, "Attr4", true));
		fields.put("attr5", new Field("attr5", DAOutils.STRING_TYPE, "Attr5", true));
		fields.put("user_id", new Field("user_id", DAOutils.INT_TYPE, "User ID", true));

	}
	
	public void loadByKeycode(String keycode) {
		SqlConn con = null;
		ResultSet rs = null;
		String sql = "";
		try {
			con = ConnFactory.getConnection("license");
			sql = "select id from license where keycode = '" + keycode + "'";
			rs = con.query(sql);
			if (rs != null && rs.next()) {
				this.setField("id", rs.getInt("id"));
				this.load();
			} 
				
		} catch (Exception e) {
			logger.error("Failed to get Test: " + e.getMessage(), e);
		} finally {
			con.close();
		}
	}
	
}
