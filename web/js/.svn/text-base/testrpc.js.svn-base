$.fn.testJsonRpc = function () {
	var urlStr = "/jrg/CommonAjax";
	var para = {};
	para["function"] = "testJsonRpc";
	para["lng"] = "44";
	para["lat"] = "26";
	para["email"] = "test@foo.com";
	para["store_name"] = "all food";
	para["prod1"] = "tp1";
	para["prod2"] = "tp2";
	para["prod3"] = "tp3";
	para["prod4"] = "";
	para["prod5"] = "";
	alert("Starting...");
	$.ajax({
		url: urlStr,
		data: para,
		async: false,
		cache: false,
		success: function(data){
			var success = $(data).find("response").attr("success");
			if (success == "1") {
				alert("Getting JSON Response successed!");
				return false;
			} else {
				alert("Getting JSON Response failed! Reason: " + $(data).find("response").attr("errmsg"));
				return false;
			}
		}
	});
}

