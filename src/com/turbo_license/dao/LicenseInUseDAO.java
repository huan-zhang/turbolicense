package com.turbo_license.dao;

import java.sql.ResultSet;

import com.turbo_license.common.ConnFactory;
import com.turbo_license.common.SqlConn;

/*
 * dao for page-level collection of gallery regions
 * will need title (for <title> tag), possibly CSS filename, 
 * main feature zone, and child regions in sequence
 */



public class LicenseInUseDAO extends MasterDAO {
	
	public LicenseInUseDAO() {

		super();
		PK = "id";
		DB = "license";
		table = "license_in_use";
		fields.put("id", new Field("id", DAOutils.INT_TYPE, "License In Use ID", false));
		fields.put("license_id", new Field("license_id", DAOutils.INT_TYPE, "License ID", true));
		fields.put("client_identifier", new Field("client_identifier", DAOutils.STRING_TYPE, "Client Identifier", false));
		fields.put("last_access", new Field("last_access",  DAOutils.TIMESTAMP_TYPE, "Last Access", true));
	}
}
