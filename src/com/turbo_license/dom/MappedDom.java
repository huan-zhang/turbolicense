/*
 * Created on Feb 11, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.turbo_license.dom;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

/**
 * @author jcaron
 *
 * Generic implementation of DomSource
 * Maps the elements in an xml document 
 * to a hashtable, using the ids of the elements
 * as the keys in the map
 */
public class MappedDom implements DomSource, Serializable {

	private static Logger logger = Logger.getLogger(MappedDom.class);

	Document doc;
	Vector dummyNodes = new Vector();
	String errorMessage;

	Hashtable<String, Element> nodeMap; //map (id attributes as keys, elements as values)
	Hashtable extNodes; //map of elements imported from other pages

	/** Creates a new instance of AbstractMappedDom */
	public MappedDom() {
		nodeMap = new Hashtable();
	}

	/**
	 * Creates MappedDom from DOM
	 */
	public MappedDom(Document doc) {
		this();
		this.doc = doc;
		this.findIDAttributes(doc.getRootElement(), nodeMap);
	}

	/**
	 * Creates MappedDom from file
	 */
	public MappedDom(String fileName) {
		this(fileName,null);
	}

	/**
	   Creates MappedDom from InputStream
	*/
	public MappedDom(InputStream stream) {
		this(null,stream);
	}

	/**
	   Private workhorse constructor.
	*/
	private MappedDom(String fileName, InputStream stream) {

		this();

		// Allow us to look in classpath for local DTDs.
		logger.debug("about to set entity resolver");
		EntityResolver resolver = new EntityResolver() {
			public InputSource resolveEntity(String publicId, String systemId) {
				if (systemId != null && systemId.startsWith("file:/")) {
					
					String resourceName = systemId.substring(systemId.lastIndexOf("/") + 1);
					InputStream in = getClass().getClassLoader().getResourceAsStream(resourceName);
					InputSource source = new InputSource(in);
					String url = getClass().getClassLoader().getResource(resourceName).toString();
					source.setSystemId(url);
					return new InputSource(in);
					
				} else {
					logger.error("Unsupported: remote DTDs. ("+systemId+") Use SYSTEM doc id.");
				}
				return null;
			}
		};

		SAXBuilder builder = new SAXBuilder(false);
		builder.setEntityResolver(resolver);
		

		
		try {
			logger.debug("Constructing MappedDom from file: "+fileName);
			if (stream == null)
				stream = new FileInputStream(fileName);
			doc = builder.build(stream);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
		markDummyNodes(doc.getRootElement());
		removeDummyNodes();
		findIDAttributes(doc.getRootElement(), nodeMap);
	}

	/** recursive method to find all tags with "ID" attributes
	 * @param Element Parent Element whose childred to search for ID attributes
	 */
	protected void findIDAttributes(Element parent, Hashtable map) {

		Iterator i = parent.getChildren().iterator();
		while (i.hasNext()) {
			Element elem = (Element) i.next();
			if (elem.getAttribute("id") != null) {
				map.put(elem.getAttributeValue("id"), elem);
				//logger.debug("id of "+elem.getAttributeValue("id"));
			}
			findIDAttributes(elem, map);
		}
	}

	/* get root elemnt for XML output */
	public org.jdom.Document getDocument() {
		return doc;
	}

	/* get reference to an element that matched the id attribute */
	public Element getExtNode(String id) {
		return (Element) extNodes.get(id);
	}

	/* get reference to an element that matched the id attribute */
	public Element getNode(String id) {
		if(id==null) return null;
		return (Element) nodeMap.get(id);

	}

	/**
	   Sets the attribute value of a node if it exists, otherwise does nothing.
	*/
	public void setNodeAttr(String nodeId, String attrId, String attrValue) {
		Element elem = getNode(nodeId);
		if (elem != null && attrValue!=null)
			elem.setAttribute(attrId,attrValue);
	}

	/**
	   Sets the text value of a node if it exists, otherwise does nothing.
	*/
	public void setNodeText(String nodeId, String nodeText) {
		Element elem = getNode(nodeId);
		if (elem != null)
			elem.setText(nodeText);
	}

	public Hashtable<String,Element> getNodeMap() {
		return nodeMap;
	}

	/* get root elemnt for XML output */
	public org.jdom.Element getRootElement() {
		return doc.getRootElement();
	}

	public String getString() throws JDOMException, TransformerException {
		Document myDoc = getDocument();
		DocType xhtmlDocType = new DocType("html","-//W3C//DTD XHTML 1.0 Transitional//EN",
										   "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd");
		myDoc.setDocType(xhtmlDocType);

		Format f = Format.getRawFormat();
		f.setEncoding("iso-8859-1");
		f.setOmitDeclaration(true);

		XMLOutputter xmlout = new XMLOutputter(f);
		return xmlout.outputString(myDoc);
	}

	/** recursive method to mark all nodes with "class=deleteme" attributes
	 * @param Element --Parent Element whose children will be search for such attributes
	 */
	protected void markDummyNodes(org.jdom.Element parent) {

		Iterator i = parent.getChildren().iterator();
		while (i.hasNext()) {
			org.jdom.Element elem = (Element) i.next();
			if ("deleteme".equals(elem.getAttributeValue("class"))
				|| "deleteMe".equals(elem.getAttributeValue("class"))
				|| (elem.getAttributeValue("id") != null && elem.getAttributeValue("id").startsWith("deleteMe"))) {
				elem.setName("deleteyou");
				dummyNodes.add(elem.getParent());
			} else {
				markDummyNodes(elem);
			}

		}

	}

	/** recursive method to remove all nodes with name of "deleteme"
	 * @param Element --Parent Element whose children will be search for such attributes
	 */
	protected void removeDummyNodes() {

		Enumeration e = dummyNodes.elements();
		while (e.hasMoreElements()) {
			Element elem = (Element) e.nextElement();
			elem.removeChildren("deleteyou", elem.getNamespace());
		}
		dummyNodes = null;
	}

	public void removeNodes(String[] nodes) {
		for (int i = 0; i < nodes.length; i++) {
			if ((Element) nodeMap.get(nodes[i]) != null)
				 ((Element) nodeMap.get(nodes[i])).detach();
		}
	}

	public void setExtNode(String fileName, String id) throws Exception {

		Document doc = null;
		Hashtable tmp = new Hashtable();
		SAXBuilder builder = new SAXBuilder();

		try {
			doc = builder.build(new FileInputStream(fileName));
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}

		findIDAttributes(doc.getRootElement(), tmp);
		extNodes.put(id, tmp.get(id));
	}

	/**
	   Returns the first element found beneath the parent element that
	   contains an 'id' attribute matching the value passed in.
	   @param e the parent element
	   @param id the value of the 'id' attribute to match
	   @return the first matching element found, or null if none found
	*/
	public static Element childOfElement(Element e, String id) {
		Iterator<Element> i = e.getDescendants(new IDFilter(id));
		if (i != null && i.hasNext())
			return i.next();
		else
			return null;
	}

}
