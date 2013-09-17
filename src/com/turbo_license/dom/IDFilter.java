package com.turbo_license.dom;

import org.jdom.Element;
import org.jdom.filter.Filter;

public class IDFilter implements Filter {
	String idValue;

	public IDFilter(String idValue) {
		this.idValue = idValue;
	}

	/**
	   Matches if the object is an element that has an 'id' attribute
	   equal to the idValue of the filter.
	*/
	public boolean matches(Object obj) {
		return obj instanceof Element 
			&& idValue.equals(((Element)obj).getAttributeValue("id"));
	}
}