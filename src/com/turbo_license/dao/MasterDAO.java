package com.turbo_license.dao;

import java.io.Serializable;
import java.sql.Blob;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.turbo_license.common.ConnFactory;
import com.turbo_license.common.SqlConn;


public class MasterDAO implements DAO, Serializable {

	static Logger logger = Logger.getLogger(MasterDAO.class);
	static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	static SimpleDateFormat dfDate = new SimpleDateFormat("yyyy-MM-dd");

	protected String PK;
	protected String DB;
	protected String table;
	protected Map<String, Field> fields = new LinkedHashMap<String, Field>();

	public void delete() {
		SqlConn con = null;
		try {

			con = ConnFactory.getConnection(DB);
			con.prepareStatement("delete from " + table + " where " + PK + "=?");

			if (fields.get(PK).getType() == DAOutils.STRING_TYPE)
				con.setString(1, getValueAsString(PK));
			else
				con.setInt(1, getValueAsInt(PK));

			con.exPrep();

		} catch (SQLException e) {
			logger.error("Failed to delete dao with id " + getField(PK).getValueAsInt() + " from table " + table, e);
		} finally {
			if (con != null)
				con.close();
		}
	}

	private boolean existingRecord(SqlConn con) throws SQLException {

		con.prepareStatement("select 1 from " + table + " where " + PK + "=?");

		if (fields.get(PK).getType() == DAOutils.STRING_TYPE)
			con.setString(1, getValueAsString(PK));
		else
			con.setInt(1, getValueAsInt(PK));

		return con.exPrep(true).first();
	}

	public TreeMap<String, DAO> getChildren() {
		return null;
	}

	public Date getDateVal(String key) {

		if (fields.get(key) == null)
			return null;

		if (fields.get(key).getType() != DAOutils.DATE_TYPE)
			return null;

		return (Date) fields.get(key).getValue();
	}

	public String getDB() {
		return DB;
	}

	public Field getField(String fieldName) {
		return fields.get(fieldName);
	}

	public Map<String, Field> getFields() {
		return fields;
	}

	/*
	 * repeat of getValueAsInt(), just easier typing
	 */
	public int getIntVal(String key) {
		if (fields.get(key) == null)
			return -1;
		return fields.get(key).getValueAsInt();
	}

	public String getPKcol() {
		return PK;
	}

	/*
	 * repeat of getValueAsString(), just easier typing
	 */
	public String getStrVal(String key) {
		if (fields.get(key) == null)
			return "";
		return fields.get(key).getValueAsString();
	}

	public String getTable() {
		return table;
	}

	public Timestamp getTimeVal(String key) {

		if (fields.get(key) == null)
			return new Timestamp(System.currentTimeMillis());

		if (fields.get(key).getType() != DAOutils.TIMESTAMP_TYPE)
			return new Timestamp(System.currentTimeMillis());

		return (Timestamp) fields.get(key).getValue();
	}

	public Object getValue(String key) {
		if (fields.get(key) == null)
			return null;
		return fields.get(key).getValue();
	}

	public float getValueAsFloat(String key) {
		if (fields.get(key) == null)
			return -1;
		return fields.get(key).getValueAsFloat();
	}

	public int getValueAsInt(String key) {
		if (fields.get(key) == null)
			return -1;
		return fields.get(key).getValueAsInt();
	}

	public String getValueAsString(String key) {
		if (fields.get(key) == null)
			return "";
		return fields.get(key).getValueAsString();
	}

	public void load() {

		SqlConn con = null;
		logger.debug("dao load method");

		try {
			con = ConnFactory.getConnection(DB);
			String s = "select * from " + table + " where " + PK + "=:::";

			if (fields.get(PK).getType() == DAOutils.INT_TYPE)
				s = s.replace(":::", String.valueOf(fields.get(PK).getValueAsInt()));

			if (fields.get(PK).getType() == DAOutils.STRING_TYPE)
				s = s.replace(":::", "'" + fields.get(PK).getValueAsString() + "'");

			ResultSet rs = con.query(s);

			while (rs.next()) {
				for (Field field : fields.values()) {

					// for field names not in the DB that we want to use
					// programatically, we start the field name with "EMPTY_"
					if (field.name.startsWith("EMPTY_"))
						continue;

					logger.debug("setting field " + field.name + " with value " + rs.getObject(field.name));

					switch (field.getType()) {
					case DAOutils.STRING_TYPE:
						field.setValue(rs.getString(field.name));
						break;
					case DAOutils.INT_TYPE:
						field.setValue(-1);
						field.setValue(rs.getInt(field.name));
						break;
					case DAOutils.TIMESTAMP_TYPE:
						field.setValue(rs.getTimestamp(field.name));
						break;
					case DAOutils.FLOAT_TYPE:
						field.setValue(rs.getFloat(field.name));
						break;
					case DAOutils.DATE_TYPE:
						field.setValue(rs.getDate(field.name));
						break;
					case DAOutils.PASSWORD_TYPE:
						field.setValue(rs.getString(field.name));
						break;
					case DAOutils.BLOB_TYPE:
						field.setValue(rs.getBlob(field.name));
						break;
					}
				}
			}
		} catch (SQLException e) {
			logger.error("failed to load object " + this.getClass().getName() + " from table " + table + " with id " + fields.get(PK).getValueAsString() + " -- " + e.getMessage(),
					e);
		} finally {
			if (con != null)
				con.close();
		}

	}

	public int save() {

		logger.debug("in dao save for " + this.getTable() + " " + this.getPKcol());

		int id = 0;
		SqlConn con = null;
		try {

			con = ConnFactory.getConnection(DB);

			if (existingRecord(con)) {
				id = saveExistingRecord(con);
				logger.debug("should have saved existing with id " + id);
			} else {
				id = saveNewRecord(con);
				logger.debug("should have saved new with id " + id);
			}

			if (getField(getPKcol()).getType() == DAOutils.INT_TYPE)
				setField(getPKcol(), id);

		} catch (Exception e) {
			logger.error("error saving dao into " + table + " with id " + fields.get(PK).getValue() + " -- " + e.getMessage(), e);
			id = -1;
		} finally {
			if (con != null)
				con.close();
		}
		return id;
	}

	private int saveExistingRecord(SqlConn con) throws SQLException {

		// CREATE STMT TO UPDATE ENTRY IN DB
		StringBuilder sb = new StringBuilder("update " + table + " set ");

		for (Field field : fields.values()) {
			if (field.getValue() == null)
				continue;
			if (field.getType() == DAOutils.PASSWORD_TYPE) {
				sb.append(field.name + "=password(?),");
			} else {
				sb.append(field.name + "=?,");
			}
		}

		sb.deleteCharAt(sb.length() - 1);
		if (fields.get(PK).getType() == DAOutils.STRING_TYPE)
			sb.append(" where " + PK + "='" + fields.get(PK).getValueAsString() + "'");
		else
			sb.append(" where " + PK + "=" + fields.get(PK).getValueAsInt());

		// FILL STMT TO UPDATE ENTRY IN DB
		int ind = 1;
		con.prepareStatement(sb.toString());
		for (Field field : fields.values()) {

			logger.debug(" saving field " + field.getName() + " with val " + field.getValueAsString() + " in table " + this.table +" with doa hash "+this.hashCode());
			if (field.getValue() == null)
				continue;

			switch (field.getType()) {
			case DAOutils.STRING_TYPE:
				con.setString(ind++, field.getValueAsString());
				break;
			case DAOutils.INT_TYPE:
				con.setInt(ind++, field.getValueAsInt());
				break;
			case DAOutils.TIMESTAMP_TYPE:
				con.setTimestamp(ind++, (Timestamp) field.getValue());
				break;
			case DAOutils.FLOAT_TYPE:
				con.setFloat(ind++, ((Float) field.getValue()).floatValue());
				break;
			case DAOutils.DATE_TYPE:
				con.setDate(ind++, (Date) field.getValue());
				break;
			case DAOutils.PASSWORD_TYPE:
				con.setString(ind++, field.getValueAsString());
				break;
			case DAOutils.BLOB_TYPE:
				con.setBlob(ind++, (Blob) field.getValue());
			}
		}

		// EXECUTE UPDATE
		con.exPrep();

		// IF STRING TYPE PK, WE KNOW WE'RE NOT DOING AUTO-INCREMENT,
		// SO IGNORE RETURNING A REAL INT.
		if (fields.get(PK).getType() == DAOutils.STRING_TYPE)
			return 0;

		return getValueAsInt(PK);
	}

	private int saveNewRecord(SqlConn con) throws SQLException {

		// CREATE STMT TO INSERT NEW ENTRY INTO DB
		StringBuilder sb = new StringBuilder("insert into " + table + "(");
		for (Field field : fields.values()) {
			if (field.getValue() == null)
				continue;
			logger.info("adding field to insert clause: " + field.name + " val: " + field.getValueAsString());
			sb.append(field.name + ",");
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append(") values (");

		for (Field field : fields.values()) {
			if (field.getValue() == null)
				continue;
			if (field.getType() == DAOutils.PASSWORD_TYPE) {
				sb.append("password(?),");
			} else {
				sb.append("?,");
			}
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append(")");

		// FILL STMT TO INSERT NEW ENTRY INTO DB
		int ind = 1;
		logger.info("save dao new " + sb.toString());
		con.prepareStatement(sb.toString());
		for (Field field : fields.values()) {

			if (field.getValue() == null)
				continue;
			switch (field.getType()) {
			case DAOutils.STRING_TYPE:
				con.setString(ind++, field.getValueAsString());
				break;
			case DAOutils.INT_TYPE:
				con.setInt(ind++, field.getValueAsInt());
				break;
			case DAOutils.TIMESTAMP_TYPE:
				con.setTimestamp(ind++, (Timestamp) field.getValue());
				break;
			case DAOutils.FLOAT_TYPE:
				con.setFloat(ind++, ((Float) field.getValue()).floatValue());
				break;
			case DAOutils.DATE_TYPE:
				con.setDate(ind++, (Date) field.getValue());
				break;
			case DAOutils.PASSWORD_TYPE:
				logger.info("password is " + field.getValueAsString());
				con.setString(ind++, field.getValueAsString());
				break;
			case DAOutils.BLOB_TYPE:
				con.setBlob(ind++, (Blob) field.getValue());
				break;
			}
		}

		// EXECUTE UPDATE
		con.exPrep();

		// IF NEW INSERTION OF AUTO-INCREMENT PRIMARY KEY, RETURN THE AUTO-ID
		if (fields.get(PK).getType() == DAOutils.INT_TYPE && fields.get(PK).getValue() == null) {
			int id = con.getLastSerial(table, PK);
			return id;
		}

		return -1;
	}

	public void setField(String fieldName, float value) {
		fields.get(fieldName).setValue(new Float(value));
	}

	public void setField(String fieldName, int value) {
		fields.get(fieldName).setValue(new Integer(value));
	}

	public void setField(String fieldName, Object value) {
		fields.get(fieldName).setValue(value);
	}

	public void setField(String fieldName, String value) {
		
		logger.debug("about to set "+fieldName+" with value "+value+" on hash# "+this.hashCode());

		Field field = fields.get(fieldName);
		if (field == null){
			logger.debug(fieldName +" not exists on "+this.getClass().getName());
			return;
		}

		switch (field.getType()) {

		case DAOutils.STRING_TYPE:
			field.setValue(value);
			logger.debug("set AS STRING "+fieldName+" with value "+value+" on hash# "+this.hashCode());
			break;

		case DAOutils.INT_TYPE:
			field.setValue(new Integer(value));
			logger.debug("set AS INT "+fieldName+" with value "+value+" on hash# "+this.hashCode());
			break;

		case DAOutils.TIMESTAMP_TYPE:
			if (value.length() == 13) // unix time
				field.setValue(new Timestamp(Long.parseLong(value)));
			else { // date format
				try {
					field.setValue(new Timestamp(df.parse(value).getTime()));
				} catch (ParseException e) {
					logger.error(e.getMessage(), e);
				}
			}
			logger.debug("set AS TIMESTAMP "+fieldName+" with value "+value+" on hash# "+this.hashCode());
			break;

		case DAOutils.FLOAT_TYPE:
			field.setValue(new Float(value));
			logger.debug("set AS FLOAT "+fieldName+" with value "+value+" on hash# "+this.hashCode());
			break;

		case DAOutils.DATE_TYPE:
			if (value.length() == 13) // unix time
				field.setValue(new Date(Long.parseLong(value)));
			else { // date format
				try {
					field.setValue(new Date(dfDate.parse(value).getTime()));
				} catch (ParseException e) {
					logger.error(e.getMessage(), e);
				}
			}
			logger.debug("set AS DATE "+fieldName+" with value "+value+" on hash# "+this.hashCode());
			break;
		case DAOutils.PASSWORD_TYPE:
			field.setValue(value);
			logger.debug("set AS STRING "+fieldName+" with value "+value+" on hash# "+this.hashCode());
			break;
	 	}
		
	}

}
