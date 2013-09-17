package com.turbo_license.dom;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.log4j.Logger;

/**
 * DomDoc.java
 *
 * Created on February 19, 2003, 3:46 AM
 *
 * @author  jcaron
 */

public class PageDomFactory {

	static Logger logger = Logger.getLogger(PageDomFactory.class);

	String fileName;
	String path;
	byte[] serialDom = null;

	/**
	   Initializes a factory with a filename and a path.
	*/
	public PageDomFactory(String fileName, String path) {
		this.fileName = fileName;
		// normalize possible trailing slash.
		if (path.endsWith("/"))
			this.path = path.substring(0, path.length() - 1);
		else
			this.path = path;
	}

	/**
	   Creates a MappedDom, reading from the underlying file if there's nothing in memory
	*/
	public PageDom getPageDom() throws IOException {
		PageDom newDom = null;
		try {
			if (serialDom == null) {
				if (!makePageDom(true)) {
					logger.error("couldn't make dom from file "+path+"/"+fileName);
					return null;
				}
			}
			ObjectInputStream objectIn = new ObjectInputStream(new ByteArrayInputStream(serialDom));
			newDom = (PageDom) objectIn.readObject();
			objectIn.close();
		} catch (ClassNotFoundException cnfe) {
			logger.error(cnfe.getMessage(),cnfe);
		} catch (Exception e) {
			logger.warn("Error reading local byte array: "+e.getMessage(),e);
		}
		return newDom;
	}

	/**
	   Reads a potential MappedDom from the filesystem into memory.
	*/
	public boolean makePageDom(boolean t) {

		try {
			PageDom originalDom = new PageDom(path + "/" + fileName);
			ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(byteOut);
			out.writeObject(originalDom);
			out.close();
			serialDom = byteOut.toByteArray();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return false;
		}
		return true;
	}

}
