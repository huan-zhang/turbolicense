package com.turbo_license.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.turbo_license.common.SqlConn;

public class DAOutils {

	public static final int STRING_TYPE = 0;
	public static final int INT_TYPE = 1;
	public static final int DATE_TYPE = 2;
	public static final int TIMESTAMP_TYPE = 3;
	public static final int FLOAT_TYPE = 4;
	public static final int PASSWORD_TYPE = 5;
	public static final int BLOB_TYPE = 6;

	public static final int TEXT_FIELD = 0;
	public static final int TEXT_AREA = 1;
	public static final int Y_OR_N = 2;
	public static final int MULTI_SELECT = 3;
	public static final int SINGLE_SELECT = 4;

	public static void joinDrupalAuthor(SqlConn drupCon, DAO dao) throws SQLException {
		int drupalId = dao.getValueAsInt("drupal_id");
		if (drupalId > 0) {
			ResultSet drupalRs = drupCon.query("select mail, name, status from drupal_users where uid=" + drupalId);
			if (drupalRs.first()) {
				dao.setField("drupal_name", drupalRs.getString("name"));
				dao.setField("drupal_status", drupalRs.getInt("status"));
			}
		}
	}

}
