// SqlConn.java
// joe caron
// makes a single connection to a JDBC compliant DB
// encapsulates the executeUpdate and exceuteQuery methods for client classes
// 05.02

package com.turbo_license.common;

import java.io.Serializable;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

public class SqlConn implements Serializable {

	static Logger logger = Logger.getLogger(SqlConn.class);

	Connection con;
	PreparedStatement pstmt;
	Statement stmt;
	String dsClassName = "";

	public SqlConn() {
	}

	public SqlConn(DataSource ds) throws SQLException {
		logger.debug("getting conn to DataSource " + ds.getClass().getName());
		con = ds.getConnection();
		con.setAutoCommit(true);
		stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		dsClassName = ds.getClass().getName();
		logger.debug("opening connection " + con.hashCode());
	}

	public void addBatch() throws SQLException {
		pstmt.addBatch();
	}

	public void clearBatch() throws SQLException {
		pstmt.clearBatch();
	}

	public void clearPrep() throws SQLException {
		pstmt.clearParameters();
	}

	public void close() {
		try {

			if (pstmt != null)
				try {
					pstmt.close();
				} catch (Exception e) {
					logger.error("Error closing DB connection.", e);
				}

			if (stmt != null)
				try {
					stmt.close();
				} catch (Exception e) {
					logger.error("Error closing DB connection.", e);
				}

			if (con != null)
				try {
					logger.debug("closing connection " + con.hashCode());
					con.close();
				} catch (Exception e) {
					logger.error("Error closing DB connection.", e);
				}

			pstmt = null;
			stmt = null;
			con = null;

		} catch (Exception e) {
			logger.error("Error closing DB connection.", e);
		}
	}

	public void exBatch() throws SQLException {
		pstmt.executeBatch();
	}

	public void exPrep() throws SQLException {
		logger.debug("executing prepared statement");
		pstmt.execute();
	}

	public ResultSet exPrep(boolean rs) throws SQLException {
		logger.debug("executing prepared statement");
		return pstmt.executeQuery();
	}

	public void prepareStatement(String prep) throws SQLException {
		logger.debug("preparing stmt: " + prep);
		if (pstmt != null)
			try {
				pstmt.close();
			} catch (Exception e) {
			}
		pstmt = con.prepareStatement(prep, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
	}

	public ResultSet query(String s) throws SQLException {
		logger.debug("executing query: " + s);
		return stmt.executeQuery(s);
	}

	public void setDate(int pos, Date date) throws SQLException {
		pstmt.setDate(pos, date);
	}

	public void setFloat(int pos, float f) throws SQLException {
		pstmt.setFloat(pos, f);
	}

	public void setInt(int pos, int val) throws SQLException {
		pstmt.setInt(pos, val);
	}

	public void setNull(int pos, int type) throws SQLException {
		pstmt.setNull(pos, type);
	}

	public void setString(int pos, String val) throws SQLException {
		pstmt.setString(pos, val);
	}

	public void setTimestamp(int pos, Timestamp t) throws SQLException {
		pstmt.setTimestamp(pos, t);
	}

	public void setObject(int pos, Object obj) throws SQLException {
		pstmt.setObject(pos, obj);
	}

	public void setBytes(int pos, byte[] bytes) throws SQLException {
		pstmt.setBytes(pos, bytes);
	}
	
	public void setBlob(int pos, Blob blob) throws SQLException {
		pstmt.setBlob(pos, blob);
	}

	public int update(String s) throws SQLException {
		logger.debug("Executing update: " + s);
		int i = -1;
		i = stmt.executeUpdate(s);
		return i;
	}

	public int getLastSerial(String tableName, String seqCol) throws SQLException {

		String q = "";
		if (dsClassName.contains("mysql")) {
			q = "select last_insert_id() from " + tableName;
		} else {
			// POSTGRES 8.1 doesn't have seqnames in the information_schema
			//ResultSet rs = query("SELECT sequence_name FROM information_schema.sequences WHERE sequence_schema='public' and sequence_name like '"
			//		+ tableName + "%'");
			//rs.first();
			//String seqName = rs.getString(1);
			String seqName = tableName + "_" + seqCol + "_seq";
			q = "select currval('" + seqName + "')";

		}

		ResultSet rs = query(q);
		if (rs.first())
			return rs.getInt(1);
		else
			return -1;
	}

}