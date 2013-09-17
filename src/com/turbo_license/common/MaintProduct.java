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
public class MaintProduct extends MasterServlet {

	
	
	/**
	 * Initializes the servlet.
	 */
	public void init(ServletConfig config) throws ServletException {
		super.init(config, "core/main.html");
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
		HttpSession session = request.getSession(true);
		String username = (String) session.getAttribute("username");
		if (username == null || "".equals(username)) {
			response.sendRedirect(response.encodeRedirectURL(ctx + "/Login"));
		}
		this.sendToClient(response, pageDom);
	}
}
