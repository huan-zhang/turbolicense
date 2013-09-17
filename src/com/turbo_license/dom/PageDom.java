/*
 * DomDoc.java
 *
 * Created on February 19, 2003, 3:46 AM
 */

package com.turbo_license.dom;

import java.io.IOException;
import java.io.Writer;

import org.apache.log4j.Logger;
import org.jdom.output.XMLOutputter;

/*******************************************************************************************************************************************
 * 
 * @author jcaron
 ******************************************************************************************************************************************/

public class PageDom extends MappedDom implements Cloneable {

	static Logger logger = Logger.getLogger(MappedDom.class);

	/** Creates a new instance of DomDoc */
	public PageDom(String fileName) {
		super(fileName);
	}

	public void toWriter(Writer out) throws IOException {
		XMLOutputter xml = new XMLOutputter();
		xml.output(doc, out);

	}

}
