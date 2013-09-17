package com.turbo_license.dao;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Map;
import java.util.TreeMap;

public interface DAO {

	public void load();

	public int save();

	public void delete();

	public void setField(String fieldName, String value);

	public void setField(String fieldName, Object value);

	public void setField(String fieldName, int value);

	public void setField(String fieldName, float value);

	public Field getField(String fieldName);

	public Map<String, Field> getFields();

	public float getValueAsFloat(String key);

	public String getValueAsString(String key);

	public int getValueAsInt(String key);

	public Object getValue(String key);

	public String getPKcol();

	public String getTable();

	public String getDB();

	public TreeMap<String, DAO> getChildren();

	public String getStrVal(String key);

	public int getIntVal(String key);

	public Timestamp getTimeVal(String key);
	
	public Date getDateVal(String key);

}
