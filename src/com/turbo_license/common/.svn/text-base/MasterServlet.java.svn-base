package com.turbo_license.common;

import java.io.PrintWriter;
import java.sql.ResultSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.jdom.output.XMLOutputter;

import com.turbo_license.dom.PageDom;
import com.turbo_license.dom.PageDomFactory;

public class MasterServlet extends HttpServlet {
	
	protected static Logger logger = Logger.getLogger(MasterServlet.class);
	
	public PageDomFactory originalDom;
	String path = "", ctx = "", downloadPath = "", server = "";
	
	public Pattern openCDATA, closeCDATA, tArea, alphaNum;
	

	public void init(ServletConfig config, String s) throws ServletException {
		super.init(config);

		// get the context object and use it load props
		ServletContext context = config.getServletContext();
		loadProps(context);
		// now make PageDOmFactory object
		logger.debug("\n\n\n\n making dom with " + path  + s);
		originalDom = new PageDomFactory(s, path);
		originalDom.makePageDom(true);
		
		openCDATA = Pattern.compile("<!\\[CDATA\\[");
		closeCDATA = Pattern.compile("\\]\\]>");
		tArea = Pattern.compile("<textarea.* />");
		alphaNum = Pattern.compile("[\\p{Alnum}]+");
	}

	
	public void loadProps(ServletContext context) {
		path = context.getInitParameter("path");
		ctx = context.getInitParameter("ctx");
		downloadPath = context.getInitParameter("download_path");
		server=context.getInitParameter("server");
	}


	public void destroy() {
		originalDom = null;
	}
	
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, java.io.IOException {
		response.sendRedirect("/MasterServlet");
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, java.io.IOException {
		processRequest(request, response);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, java.io.IOException {
		processRequest(request, response);
	}
	
	protected void sendToClient(HttpServletResponse response, PageDom pageDom) {

		try {

			// transform the page DOM into HTML
			String result = this.doXhtmlOutput(pageDom);

			// and send to browser
			response.setContentType("text/html");
			response.setHeader("Pragma", "no-cache");
			response.setHeader("Cache-Control", "no-cache");
			response.setDateHeader("Expires", 0);
			PrintWriter out = response.getWriter();
			out.print(result);
			out.close(); // close response
			pageDom = null;

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	public String doXhtmlOutput(PageDom dom) {

		// transform the page DOM into HTML
		XMLOutputter xml = new XMLOutputter();
		Matcher m1 = openCDATA.matcher(xml.outputString(dom.getRootElement()));
		Matcher m2 = closeCDATA.matcher(m1.replaceAll(""));
		Matcher m3 = tArea.matcher(m2.replaceAll(""));

		// take care of empty textarea tags
		StringBuffer sb = new StringBuffer();
		String s = "";
		while (m3.find()) {
			s = m3.group();
			m3.appendReplacement(sb, s.substring(0, s.length() - 2) + "></textarea>");
		}
		m3.appendTail(sb);
		return sb.toString();
	}
}
