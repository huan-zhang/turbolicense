package com.turbo_license.dao;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.TreeMap;

import org.apache.log4j.Logger;

public class Field implements Serializable {

	static Logger logger = Logger.getLogger(Field.class);

	private boolean editable;
	private String label;
	private int type;
	private Object value;
	private int htmlType;
	private int defaultRows;
	private int defaultWidth;
	private TreeMap<String, String> listSource;

	String name;

	public Field(String name, int type, int htmlType, int defaultRows, String label, boolean editable) {
		this(name, type, htmlType, defaultRows, 65, label, editable);
	}

	public Field(String name, int type, String label, boolean editable) {
		this(name, type, DAOutils.TEXT_FIELD, 1, label, editable);
	}

	public Field(String name, int type, int defaultWidth, String label, boolean editable) {
		this(name, type, DAOutils.TEXT_FIELD, 1, defaultWidth, label, editable);
	}

	public Field(String name, int type, int htmlType, int defaultRows, int defaultWidth, String label, boolean editable) {
		this.editable = editable;
		this.label = label;
		this.name = name;
		this.type = type;
		this.htmlType = htmlType;
		this.defaultRows = defaultRows;
		this.defaultWidth = defaultWidth;
	}

	public Field(String name, int type, int htmlType, int defaultRows, int defaultWidth, String label, boolean editable,
			TreeMap<String, String> source) {
		this.editable = editable;
		this.label = label;
		this.name = name;
		this.type = type;
		this.htmlType = htmlType;
		this.defaultRows = defaultRows;
		this.defaultWidth = defaultWidth;
		this.listSource = source;
	}

	public String getValueAsString() {

		if (value == null)
			return null;

		logger.debug("field is " + this.getName() + " and val is |" + value + "|");

		switch (type) {

		case DAOutils.STRING_TYPE:
			if (this.getHtmlType() == DAOutils.Y_OR_N) {
				return ((String) value).trim().equalsIgnoreCase("Y") ? "Y" : "N";
			}
			return (String) value;

		case DAOutils.INT_TYPE:
			return ((Integer) value).toString();

		case DAOutils.TIMESTAMP_TYPE:
			return ((Timestamp) value).toString();

		case DAOutils.FLOAT_TYPE:
			return ((Float) value).toString();

		case DAOutils.DATE_TYPE:
			return ((Date) value).toString();
			
		case DAOutils.PASSWORD_TYPE:
			return (String) value;
		}

		logger.debug("should never be here");
		return "";
	}

	public int getValueAsInt() {

		if (value == null)
			return -1;

		if (this.type == DAOutils.STRING_TYPE) {
			try {
				return Integer.parseInt((String) value);
			} catch (Exception e) {
				logger.error("NumberFormat on ValAsInt " + value + " for field " + this.getName());
				return -1;
			}
		}

		return ((Integer) value).intValue();
	}

	public float getValueAsFloat() {
		if (value == null)
			return -1f;
		return ((Float) value).floatValue();
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public Object getValue() {
		return value;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getHtmlType() {
		return htmlType;
	}

	public int getDefaultRows() {
		return defaultRows;
	}

	public int getDefaultWidth() {
		return defaultWidth;
	}

	public TreeMap<String, String> getListSource() {
		return listSource;
	}

	public void setListSource(TreeMap<String, String> val) {
		this.listSource = val;
	}
}
