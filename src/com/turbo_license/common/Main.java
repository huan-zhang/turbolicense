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
import com.turbo_license.utilities.DOMAutoFiller;

public class Main extends MasterServlet {

	
	
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
		String userId = (String) session.getAttribute("user_id");
		if (username == null || "".equals(username)) {
			response.sendRedirect(response.encodeRedirectURL(ctx + "/Login"));
		}
		renderVendor(pageDom, userId);
		renderProduct(pageDom, userId);
		renderLicense(pageDom, userId);
		this.sendToClient(response, pageDom);
	}
	
	private void renderVendor(PageDom pageDom, String userId) {
		SqlConn con = null;
		ResultSet rs = null;
		try {
			con = ConnFactory.getConnection("license");
			String sql = "select * from vendor where user_id = '" + userId + "' order by name";
			rs = con.query(sql);
			if (rs != null && rs.next()) {
				pageDom.getNode("VendorId").setText(rs.getString("id"));
				pageDom.getNode("VendorName").setAttribute("value", rs.getString("name"));
				pageDom.getNode("V_Contact").setAttribute("value", rs.getString("contact"));
				pageDom.getNode("V_Address1").setAttribute("value", rs.getString("addr1"));
				pageDom.getNode("V_Address2").setAttribute("value", rs.getString("addr2"));
				pageDom.getNode("V_City").setAttribute("value", rs.getString("city"));
				String curState = rs.getString("state");
				DOMAutoFiller.fillStates(pageDom, "V_State", curState);
				pageDom.getNode("V_Zip").setAttribute("value", rs.getString("zip"));
				pageDom.getNode("V_Country").setAttribute("value", rs.getString("country"));
				pageDom.getNode("V_Phone").setAttribute("value", rs.getString("phone"));
				
			} else {
				DOMAutoFiller.fillStates(pageDom, "V_State", "AL");

			}
		} catch (Exception e) { 
			logger.error(e.getMessage());
		} finally {
			con.close();
		}
	}
	
	private void renderProduct(PageDom pageDom, String userId) {
		SqlConn con = null;
		ResultSet rs = null;
		String sql = "";
		try {
			con = ConnFactory.getConnection("license");
			sql = "select * from product where vendor_id = '" + userId + "'";
			rs = con.query(sql);
			Element elProductRow = pageDom.getNode("ProductRow");
			Element elParent = elProductRow.getParentElement();
			elProductRow.detach();
			elParent.addContent((Element) elProductRow.clone());
			int count = 0;
			String productId = "";
			while (rs != null && rs.next()) {
				count ++;
				productId = rs.getString("id");
				elProductRow.setAttribute("product_id", productId);
				elProductRow.setAttribute("line_number", "" + count);
				pageDom.getNode("ProductRowTitle").setText(rs.getString("name"));
				pageDom.getNode("ProductRowDesc").setText(rs.getString("description"));
				pageDom.getNode("ProductRowTitle").setAttribute("id", "ProductRowTitle_" + count);
				pageDom.getNode("ProductRowDesc").setAttribute("id", "ProductRowDesc_" + count);
				renderFeature(pageDom, count + "");
				elProductRow.setAttribute("id", "ProductRow_" + count);
				
				elParent.addContent((Element) elProductRow.clone());
			}
			pageDom.setNodeAttr("page_info", "product_count", "" + count);
		} catch (Exception e) {
			logger.error("Getting products for user " + userId + " failed: " + e.getMessage());
		} finally {
			con.close();
		}
	}
	
	private void renderFeature(PageDom pageDom, String count) {
		pageDom.getNode("ProductRowCell").setAttribute("id", "ProductRowCell_" + count);
		pageDom.getNode("NewFeatureRow").setAttribute("id", "NewFeatureRow_" + count);
		pageDom.getNode("FeatureName").setAttribute("id", "FeatureName_" + count);
		pageDom.getNode("FeatureName").setAttribute("name", "FeatureName_" + count);
		pageDom.getNode("FeatureDesc").setAttribute("id", "FeatureDesc_" + count);
		pageDom.getNode("FeatureDesc").setAttribute("name", "FeatureDesc_" + count);
		pageDom.getNode("SubmitNewFeatureBtn").setAttribute("id", "SubmitNewFeatureBtn_" + count);
		pageDom.getNode("SubmitNewFeatureBtn").setAttribute("name", "SubmitNewFeatureBtn_" + count);
		pageDom.getNode("SubmitNewFeatureBtn").setAttribute("onclick", "$().addNewFeature('" + count + "');");
		pageDom.getNode("FeatureTable").setAttribute("id", "FeatureTable_" + count);
		pageDom.getNode("FeatureTableHeader").setAttribute("id", "FeatureTableHeader_" + count);
		pageDom.getNode("FeatureRow").setAttribute("id", "FeatureRow_" + count);
		pageDom.getNode("FeatureRowId").setAttribute("id", "FeatureRowId_" + count);
		pageDom.getNode("FeatureRowName").setAttribute("id", "FeatureRowName_" + count);
		pageDom.getNode("FeatureRowDesc").setAttribute("id", "FeatureRowDesc_" + count);
	}
	
	private void renderLicense(PageDom pageDom, String userId) {
		SqlConn con = null;
		ResultSet rs = null;
		String sql = "";
		try {
			con = ConnFactory.getConnection("license");
			sql = "select * from license where user_id = '" + userId + "' order by created_date desc";
			rs = con.query(sql);
			Element elLicenseRow = pageDom.getNode("LicenseRow");
			Element elParent = elLicenseRow.getParentElement();
			elLicenseRow.detach();
			int count = 0;
			elParent.addContent((Element) elLicenseRow.clone());
			String licenseId = "", keycode = "", startDate = "", expireDate = "", licenseName = "";
			while (rs != null && rs.next()) {
				count ++;
				licenseId = rs.getString("id");
				keycode = rs.getString("keycode");
				licenseName = rs.getString("name") == null ? "" : rs.getString("name");
				startDate = rs.getString("start_date").substring(0, 10);
				expireDate = rs.getString("expire_date").substring(0, 10);
				elLicenseRow.setAttribute("license_id", licenseId);
				elLicenseRow.setAttribute("line_number", "" + count);
				pageDom.getNode("LicenseRowTitle").setText(licenseName + "   " + keycode );
				pageDom.getNode("LicenseRowTitle").setAttribute("id", "LicenseRowTitle_" + count);
				pageDom.getNode("LicenseRowCell").setAttribute("id", "LicenseRowCell_" + count);
				pageDom.getNode("LicenseName").setAttribute("value", licenseName);
				pageDom.getNode("LicenseName").setAttribute("id", "LicenseName_" + count);
				pageDom.getNode("LicenseStartDate").setAttribute("value", startDate);
				pageDom.getNode("LicenseStartDate").setAttribute("id", "LicenseStartDate_" + count);
				pageDom.getNode("LicenseExpireDate").setAttribute("value", expireDate);
				pageDom.getNode("LicenseExpireDate").setAttribute("id", "LicenseExpireDate_" + count);
				pageDom.getNode("LicenseMaxNumber").setAttribute("value", rs.getString("max_number"));
				pageDom.getNode("LicenseMaxNumber").setAttribute("id", "LicenseMaxNumber_" + count);
				pageDom.getNode("LicenseAttr1").setAttribute("value", rs.getString("attr1"));
				pageDom.getNode("LicenseAttr1").setAttribute("id", "LicenseAttr1_" + count);
				pageDom.getNode("LicenseAttr2").setAttribute("value", rs.getString("attr2"));
				pageDom.getNode("LicenseAttr2").setAttribute("id", "LicenseAttr2_" + count);
				pageDom.getNode("LicenseAttr3").setAttribute("value", rs.getString("attr3"));
				pageDom.getNode("LicenseAttr3").setAttribute("id", "LicenseAttr3_" + count);
				pageDom.getNode("LicenseAttr4").setAttribute("value", rs.getString("attr4"));
				pageDom.getNode("LicenseAttr4").setAttribute("id", "LicenseAttr4_" + count);
				pageDom.getNode("LicenseAttr5").setAttribute("value", rs.getString("attr5"));
				pageDom.getNode("LicenseAttr5").setAttribute("id", "LicenseAttr5_" + count);
				pageDom.getNode("EditLicenseBtn").setAttribute("onclick", "$().updateLicense('" + count + "');");
				pageDom.getNode("EditLicenseBtn").setAttribute("id", "EditLicenseBtn_" + count);
				pageDom.getNode("EncryptLicenseBtn").setAttribute("onclick", "$().encryptLicense('" + count + "');");
				pageDom.getNode("EncryptLicenseBtn").setAttribute("id", "EncryptLicenseBtn_" + count);
				logger.info("file name is " + rs.getString("file_name"));
				logger.info("is file name null? " +  (rs.getString("file_name") == null ? "Yes" : "No"));
				if (rs.getString("file_name") == null || "".equals(rs.getString("file_name"))) {
					pageDom.getNode("LicensePublicKey").setAttribute("style", "display: none;");
					pageDom.getNode("LicenseEncrypted").setAttribute("style", "display: none;");
					pageDom.getNode("LicensePublicKey").setAttribute("href", "#");
					pageDom.getNode("LicenseEncrypted").setAttribute("href", "#");
				} else {
					pageDom.getNode("LicensePublicKey").removeAttribute("style");
					pageDom.getNode("LicenseEncrypted").removeAttribute("style");
					pageDom.getNode("LicensePublicKey").setAttribute("href", server + ctx + downloadPath + "/" + rs.getString("file_name") + ".public.key");
					pageDom.getNode("LicenseEncrypted").setAttribute("href", server + ctx + downloadPath + "/" + rs.getString("file_name") + ".license");
				}
				pageDom.getNode("LicensePublicKey").setAttribute("id", "LicensePublicKey_" + count);
				pageDom.getNode("LicenseEncrypted").setAttribute("id", "LicenseEncrypted_" + count);
				elLicenseRow.removeAttribute("style");
				elLicenseRow.setAttribute("id", "LicenseRow_" + count);
				elParent.addContent((Element) elLicenseRow.clone());
			}
			pageDom.setNodeAttr("LicenseTab", "license_count", "" + count);
		} catch (Exception e) {
			logger.error("Getting license for user " + userId + " failed: " + e.getMessage());
		} finally {
			con.close();
		}
	}
}
