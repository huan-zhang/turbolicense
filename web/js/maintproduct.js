$.fn.login = function () {
	var urlStr = "@root@/CommonAjax";
	var para = {};
	para["function"] = "login";
	para["username"] = $("#username").val();
	para["password"] = $("#password").val();
	$.ajax({
		url: urlStr,
		data: para,
		async: false,
		cache: false,
		success: function(data){
			var success = $(data).find("response").attr("success");
			if (success == 1) {
				// change to user main page
				return false;
			} else {
				$("#username").val("");
				$("#password").val("");
				alert($(data).find("response").attr("errmsg"));
				return false;
			}
		}
	});
}

$(document).ready(function () {
});