package com.turbo_license.common;


import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jdom.Element;

import com.turbo_license.dom.PageDom;



/**
 * Home.java presentation servlet for catalogue home page revised for April 2009
 * 
 * @author jcaron
 * @version
 */
public class TestServlet extends MasterServlet {

	
	
	/**
	 * Initializes the servlet.
	 */
	public void init(ServletConfig config) throws ServletException {
		super.init(config, "core/testservlet.html");
	}

	/**
	 * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
	 * 
	 * @param request
	 *            servlet request
	 * @param response
	 *            servlet response
	 */
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, java.io.IOException {

		/***********************************************************************************************************************************
		 * Get a copy of the template DOM, fill common areas
		 **********************************************************************************************************************************/
		PageDom pageDom = originalDom.getPageDom();
		Element row = pageDom.getNode("TestRow");
		Element table = row.getParentElement();
		row.detach();
		
		SqlConn con = null;
		ResultSet rs = null;
		String sql = "";
		String id = "";
		String text = "";
		try {
			con = ConnFactory.getConnection("jrg");
			sql = "select * from test";
			rs = con.query(sql);
			while (rs != null && rs.next()) {				
				id = rs.getString("id");
				text = rs.getString("text");
				logger.info("result is " + id + " " + text);
				pageDom.getNode("TestRow_Id").setText(id);
				pageDom.getNode("TestRow_Text").setText(text);
				table.addContent((Element) row.clone());
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			con.close();
		}
		
		this.sendToClient(response, pageDom);
	}
}
