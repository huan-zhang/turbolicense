package com.turbo_license.utilities;

import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;
import org.jdom.Element;

import com.turbo_license.dom.PageDom;

public class DOMAutoFiller extends HttpServlet {
	
	private static final String[] STATE_ABBREVIATIONS = {
		  "AL", "AK", "AR", "AZ", 
		  "CA", "CO", "CT", "DC",  
	      "DE", "FL", "GA", "HI", 
	      "IA", "ID", "IL", "IN", 
	      "KS", "KY", "LA", "MA", 
	      "MD", "ME", "MI", "MN", 
	      "MO", "MS", "MT", "NC", 
	      "ND", "NE", "NH", "NJ", 
	      "NM", "NV", "NY", "OH", 
	      "OK", "OR", "PA", "RI", 
	      "SC", "SD", "TN", "TX", 
	      "UT", "VA", "VT", "WA", 
	      "WI", "WV", "WY"
	}; 
	private static final String[] STATES = {
		"Alabama", "Alaska", "Arkansas", "Arizona",
		"California", "Colorado", "Connecticut", "District of Columbia", 
		"Delaware", "Florida", "Georgia", "Hawaii", 
		"Iowa", "Idaho", "Illinois", "Indiana", 
		"Kansas", "Kentucky", "Louisiana", "Massachusetts", 
		"Maryland", "Maine", "Michigan", "Minnesota",
		"Missouri", "Mississippi", "Montana", "North Carolina", 
		"North Dakota", "Nebraska", "New Hampshire", "New Jersey", 
		"New Mexico", "Nevada", "New York", "Ohio", 
		"Oklahoma", "Oregon", "Pennsylvania", "Rhode Island",
		"South Carolina", "South Dakota", "Tennessee", "Texas", 
		"Utah", "Virginia", "Vermont", "Washington",
		"Wisconsin", "West Virginia", "Wyoming" 
	};
	protected static Logger logger = Logger.getLogger(DOMAutoFiller.class);

	public DOMAutoFiller() {
		
	}

	/**
	* Validate hex with regular expression
	* @param hex hex for validation
	* @return true valid hex, false invalid hex
	*/
	public static void fillStates (PageDom pageDom, String name, String curState){
		if ("".equals(curState)) {
			curState = "AL";
		}
		Element elSelect = pageDom.getNode(name);
		elSelect.setAttribute("value", curState);
		for (int i = 0; i < 51; i ++) {
			Element elOption = new Element("option");
			elOption.setAttribute("value", STATE_ABBREVIATIONS[i]);
			elOption.setText(STATES[i]);
			if (STATE_ABBREVIATIONS[i].equals(curState))
				elOption.setAttribute("selected", "selected");
			else
				elOption.removeAttribute("selected");
			elSelect.addContent(elOption);
		}
	}
}
