package com.turbo_license.dao;

/*
 * dao for page-level collection of gallery regions
 * will need title (for <title> tag), possibly CSS filename, 
 * main feature zone, and child regions in sequence
 */



public class UserDAO extends MasterDAO {
	
	public UserDAO() {

		super();
		PK = "id";
		DB = "license";
		table = "user";
		fields.put("id", new Field("id", DAOutils.INT_TYPE, "Slider List ID", false));
		fields.put("username", new Field("username", DAOutils.STRING_TYPE, "User Name", true));
		fields.put("password", new Field("password", DAOutils.PASSWORD_TYPE, "Password", false));
		fields.put("email", new Field("email",  DAOutils.STRING_TYPE, "email", true));
		fields.put("first_name", new Field("first_name",  DAOutils.STRING_TYPE, "First Name", true));
		fields.put("last_name", new Field("last_name",  DAOutils.STRING_TYPE, "Last Name", true));
	}
}
