package com.turbo_license.dao;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.jdom.CDATA;
import org.jdom.Element;

public class FieldAsHTML {

	static Logger logger = Logger.getLogger(FieldAsHTML.class);
	static DateFormat df = new SimpleDateFormat("MM/dd/yyyy");

	private Element formElement;
	private int width;

	public Element getFormElement() {
		return formElement;
	}

	public FieldAsHTML(Field field, boolean hidden) {
		this(field);
		formElement.setAttribute("type", "hidden");
		formElement.removeAttribute("size");
		formElement.removeAttribute("cols");
		formElement.removeAttribute("rows");
		formElement.setName("input");
	}

	public FieldAsHTML(Field field) {

		String val = field.getValueAsString();

		if (val == null)
			val = "";

		logger.debug("field name is " + field.getName() + " and field type is " + field.getType());

		if (field.getHtmlType() == DAOutils.TEXT_FIELD || field.getHtmlType() == DAOutils.Y_OR_N) {

			formElement = new Element("input");
			formElement.setAttribute("type", "text");
			formElement.setAttribute("autocomplete", "OFF");
			width = field.getDefaultWidth();
			formElement.setAttribute("size", String.valueOf(width));
			formElement.setAttribute("value", val);
			formElement.setAttribute("name", field.getName() + "-field");
			if (field.getHtmlType() == DAOutils.Y_OR_N) {
				formElement.setAttribute("size", "1");
				width = 1;
				if (!"Y".equalsIgnoreCase(val))
					formElement.setAttribute("value", "N");
			}

			if (field.getType() == DAOutils.DATE_TYPE) {
				formElement.setAttribute("class", "datepicker");
				Date sqlDate = (Date) field.getValue();
				if (sqlDate == null)
					sqlDate = new Date(System.currentTimeMillis());
				formElement.setAttribute("value", df.format(new java.util.Date(sqlDate.getTime())));
			}

		} else if (field.getHtmlType() == DAOutils.MULTI_SELECT) {
			logger.info("the value for this multi select is " + val);
			TreeMap<String, String> listSource = field.getListSource();
			formElement = new Element("select");
			formElement.setAttribute("multiple", "multiple");
			formElement.setAttribute("name", field.getName() + "-field");
			String[] tmp = val.split(",");
			for (String key : listSource.keySet()) {
				Element optionElement = new Element("option");
				optionElement.setAttribute("value", key);
				optionElement.setText(listSource.get(key));
				for (int i = 0; i < tmp.length; i++) {
					if (key.equals(tmp[i])) {
						optionElement.setAttribute("selected", "selected");
						break;
					}
				}
				formElement.addContent(optionElement);
			}

		}

		else if (field.getHtmlType() == DAOutils.SINGLE_SELECT) {
			TreeMap<String, String> listSource = field.getListSource();
			formElement = new Element("select");
			formElement.setAttribute("name", field.getName() + "-field");
			String[] tmp = val.split(",");
			for (String key : listSource.keySet()) {
				Element optionElement = new Element("option");
				optionElement.setAttribute("value", key);
				optionElement.setText(listSource.get(key));
				for (int i = 0; i < tmp.length; i++) {
					if (key.equals(tmp[i])) {
						optionElement.setAttribute("selected", "selected");
						break;
					}
				}
				formElement.addContent(optionElement);
			}

		}

		else {
			formElement = new Element("textarea");
			width = field.getDefaultWidth();
			formElement.setAttribute("cols", String.valueOf(width));
			formElement.setAttribute("rows", String.valueOf(determineRows(field)));
			val = val.replace("&", "&amp;");
			formElement.setContent(new CDATA(val));
			formElement.setAttribute("name", field.getName() + "-field");
		}
	}

	private int determineRows(Field field) {
		String val = field.getValueAsString();
		if (val == null)
			val = "";
		int textAreaRows = (int) Math.ceil((val.length() + 1) / 95f);
		textAreaRows = textAreaRows > field.getDefaultRows() ? textAreaRows : field.getDefaultRows();
		return textAreaRows;
	}

	public int getWidth() {
		return width;
	}

}
