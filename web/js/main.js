$.fn.submitVendor = function () {
	var urlStr = "@root@/CommonAjax";
	var para = {};
	para["function"] = "updateVendor";
	para["vendor_name"] = $("#VendorName").val();
	para["contact"] = $("#V_Contact").val();
	para["address1"] = $("#V_Address1").val();
	para["address2"] = $("#V_Address2").val();
	para["city"] = $("#V_City").val();
	para["state"] = $("#V_State").val() ? $("#V_State").val() : "AL";
	para["country"] = $("#V_Country").val();
	para["zip"] = $("#V_Zip").val();
	para["phone"] = $("#V_Phone").val();
	$.ajax({
		url: urlStr,
		data: para,
		async: false,
		cache: false,
		success: function(data){
			var success = $(data).find("response").attr("success");
			if (success == "1") {
				alert("Update Vendor Information successed!");
				return false;
			} else if (success == "0") {
				alert($(data).find("response").attr("errmsg"));
				return false;
			} else if (success == "-1") {
				alert("Your session expired. Please login again.");
				window.location.href="/jrg/Login";
			}
		}
	});
}

$.fn.submitNewProduct = function () {
	var urlStr = "@root@/CommonAjax";
	var para = {};
	para["function"] = "updateProduct";
	para["name"] = $("#Product_Name").val();
	para["desc"] = $("#Product_Description").val();
	para["id"] = "";
	$.ajax({
		url: urlStr,
		data: para,
		async: false,
		cache: false,
		success: function(data){
			var success = $(data).find("response").attr("success");
			if (success == "1") {
				alert("Update Product Information successed!");
				$().addNewProduct($(data).find("response").attr("prod_id"), $(data).find("response").attr("prod_name"), $(data).find("response").attr("prod_desc"));
				return false;
			} else if (success == "0") {
				alert($(data).find("response").attr("errmsg"));
				return false;
			} else if (success == "-1") {
				alert("Your session expired. Please login again.");
				window.location.href="@root@/Login";
			}
		}
	});
}

$.fn.addNewProduct = function (id, name, desc) {
	var proRow = $("#ProductRow").clone();
	var count = parseInt($("#page_info").attr("product_count"));
	count = count + 1;
	$("#page_info").attr("product_count", count);
	proRow.find("#ProductRowTitle").text(name);
	proRow.find("#ProductRowDesc").text(desc);
	proRow.attr("product_id", id);
	proRow.show();
	proRow.attr("id", "ProductRow_" + count);
	proRow.find("*").each(function () {
		$(this).attr("id", $(this).attr("id") + "_" + count);
	});
	$("#ProductRow").after(proRow);
	$("#ProductTab").accordion("destroy").accordion({ header: "h3", autoHeight: false });
	$("#Product_Name").val("");
	$("#Product_Description").val("");
}


$.fn.submitNewFeature = function (productId) {
	var urlStr = "@root@/CommonAjax";
	var para = {};
	para["function"] = "updateFeature";
	para["product_id"] = productId;
	para["name"] = $("#FeatureName").val();
	para["desc"] = $("#FeatureDesc").val();
	para["id"] = "";
	$.ajax({
		url: urlStr,
		data: para,
		async: false,
		cache: false,
		success: function(data){
			var success = $(data).find("response").attr("success");
			if (success == "1") {
				alert("Update Product Information successed!");
				$().addNewProduct($(data).find("response").attr("prod_id"), $(data).find("response").attr("prod_name"), $(data).find("response").attr("prod_desc"));
				return false;
			} else if (success == "0") {
				alert($(data).find("response").attr("errmsg"));
				return false;
			} else if (success == "-1") {
				alert("Your session expired. Please login again.");
				window.location.href="@root@/Login";
			}
		}
	});
}

$.fn.addNewProduct = function (id, name, desc) {
	var proRow = $("#ProductRow").clone();
	var count = parseInt($("#page_info").attr("product_count"));
	count = count + 1;
	$("#page_info").attr("product_count", count);
	proRow.find("#ProductRowTitle").text(name);
	proRow.find("#ProductRowDesc").text(desc);
	proRow.attr("product_id", id);
	proRow.show();
	proRow.attr("id", "ProductRow_" + count);
	proRow.find("*").each(function () {
		$(this).attr("id", $(this).attr("id") + "_" + count);
	});
	$("#ProductRow").after(proRow);
	$("#ProductTab").accordion("destroy").accordion({ header: "h3", autoHeight: false });
	$("#Product_Name").val("");
	$("#Product_Description").val("");
}

$.fn.addNewFeature = function (line) {
	var urlStr = "@root@/CommonAjax";
	var para = {};
	para["function"] = "updateFeature";
	para["product_id"] = productId;
	para["name"] = $("#FeatureName").val();
	para["desc"] = $("#FeatureDesc").val();
	para["id"] = "";
	$.ajax({
		url: urlStr,
		data: para,
		async: false,
		cache: false,
		success: function(data){
			var success = $(data).find("response").attr("success");
			if (success == "1") {
				alert("Update Product Information successed!");
				$().addNewProduct($(data).find("response").attr("prod_id"), $(data).find("response").attr("prod_name"), $(data).find("response").attr("prod_desc"));
				return false;
			} else if (success == "0") {
				alert($(data).find("response").attr("errmsg"));
				return false;
			} else if (success == "-1") {
				alert("Your session expired. Please login again.");
				window.location.href="@root@/Login";
			}
		}
	});
}

$.fn.addNewLicense =  function (data) {
	var el = $(data).find("response");
	var lrObj = $("#LicenseRow").clone();
	var count = new Number($("#LicenseTab").attr("license_count")) + 1;
	lrObj.attr("license_id", $(data).find("response").attr("license_id"));
	lrObj.find("#LicenseRowTitle").text(el.attr("license_name") + " " + el.attr("keycode"));
	lrObj.find("#LicenseName").val(el.attr("license_name"));
	lrObj.find("#LicenseStartDate").val(el.attr("start_date"));
	lrObj.find("#LicenseExpireDate").val(el.attr("expire_date"));
	lrObj.find("#LicenseMaxNumber").val(el.attr("max_number"));
	lrObj.find("#LicenseAttr1").val(el.attr("attr1"));
	lrObj.find("#LicenseAttr2").val(el.attr("attr2"));
	lrObj.find("#LicenseAttr3").val(el.attr("attr3"));
	lrObj.find("#LicenseAttr4").val(el.attr("attr4"));
	lrObj.find("#LicenseAttr5").val(el.attr("attr5"));
	lrObj.find("*").each(function () {
		$(this).attr("id", $(this).attr("id") + "_" + count);
		if ($(this).is("input")) {
			$(this).attr("name", $(this).attr("name") + "_" + count);
		}
	});
	lrObj.attr("id", "LicenseRow_" + count);
	lrObj.show();
	$("#LicenseTab").attr("license_count", count);
	$("#LicenseRow").after(lrObj);
	$("#LicenseTab").accordion('destroy').accordion({ header: "h3"});
}

$.fn.submitNewLicense = function () {
	var urlStr = "@root@/CommonAjax";
	var para = {};
	para["function"] = "createNewLicense";
	para["license_name"] = $("#NewLicenseName").val();
	para["start_date"] = $("#NewLicenseStartDate").val();
	para["expire_date"] = $("#NewLicenseExpireDate").val();
	para["max_number"] = $("#NewLicenseMaxNumber").val();
	para["attr1"] = $("#NewLicenseAttr1").val();
	para["attr2"] = $("#NewLicenseAttr2").val();
	para["attr3"] = $("#NewLicenseAttr3").val();
	para["attr4"] = $("#NewLicenseAttr4").val();
	para["attr5"] = $("#NewLicenseAttr5").val();
	
	$.ajax({
		url: urlStr,
		data: para,
		async: false,
		cache: false,
		success: function(data){
			var success = $(data).find("response").attr("success");
			if (success == "1") {
				alert("Submitting new license successed!");
				$().addNewLicense(data);
				return false;
			} else {
				alert($(data).find("response").attr("errmsg"));
				return false;
			} 
		}
	});
	
}

$.fn.updateLicense = function (line) {
	var urlStr = "@root@/CommonAjax";
	var para = {};
	para["function"] = "updateLicense";
	para["license_id"] = $("#LicenseRow_" + line).attr("license_id");
	para["license_name"] = $("#LicenseName_" + line).val();
	para["start_date"] = $("#LicenseStartDate_" + line).val();
	para["expire_date"] = $("#LicenseExpireDate_" + line).val();
	para["max_number"] = $("#LicenseMaxNumber_" + line).val();
	para["attr1"] = $("#LicenseAttr1_" + line).val();
	para["attr2"] = $("#LicenseAttr2_" + line).val();
	para["attr3"] = $("#LicenseAttr3_" + line).val();
	para["attr4"] = $("#LicenseAttr4_" + line).val();
	para["attr5"] = $("#LicenseAttr5_" + line).val();
	
	$.ajax({
		url: urlStr,
		data: para,
		async: false,
		cache: false,
		success: function(data){
			var success = $(data).find("response").attr("success");
			if (success == "1") {
				$("LicenseRowTitle_" + line).text($(data).find("response").attr("license_name") + " " + $(data).find("response").attr("keycode"));
				alert("Updating  license successed!");
				return false;
			} else {
				alert($(data).find("response").attr("errmsg"));
				return false;
			} 
		}
	});
	
}

$.fn.encryptLicense = function (line) {
	var urlStr = "@root@/CommonAjax";
	var para = {};
	para["function"] = "encryptLicense";
	para["license_id"] = $("#LicenseRow_" + line).attr("license_id");
	
	$.ajax({
		url: urlStr,
		data: para,
		async: false,
		cache: false,
		success: function(data){
			var success = $(data).find("response").attr("success");
			if (success == "1") {
				$("#LicensePublicKey_" + line).attr("href", $(data).find("response").attr("public_key_url"));
				$("#LicenseEncrypted_" + line).attr("href", $(data).find("response").attr("encrypted_license_url"));
				$("#LicensePublicKey_" + line).text("Public key file");
				$("#LicenseEncrypted_" + line).text("Encrypted license file");
				$("#LicensePublicKey_" + line).show();
				$("#LicenseEncrypted_" + line).show();
				alert("Encrypting  license successed!");
				
				return false;
			} else {
				alert($(data).find("response").attr("errmsg"));
				return false;
			} 
		}
	});
	
}

$(document).ready(function () {
	
	$("#ProductTab").accordion({ header: "h3"});
	$("#LicenseTab").accordion({ header: "h3"});
	$('#tabs').tabs();
	$("#ProductRow").hide();
	var prodId = "";
	$("div[id^=ProductRow_]").each(function () {
		//var obj = $(this);
		//prodId = obj.attr("product_id");
		//obj.find("#")
	});
	var licenseCount = $("#LicenseTab").attr("license_count");
	for (var i = 1; i <= licenseCount; i ++) {
		$("#LicenseStartDate_" + i).datepicker({ dateFormat: 'yy-mm-dd' });
		$("#LicenseExpireDate_" + i).datepicker({ dateFormat: 'yy-mm-dd' });
	}
	$("#NewLicenseStartDate").datepicker({ dateFormat: 'yy-mm-dd' });
	$("#NewLicenseExpireDate").datepicker({ dateFormat: 'yy-mm-dd' });

});