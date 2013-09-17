$(document).ready(function () {
	alert("cc");
	var url = "/jrg/CommonAjax";
	var parameters = {};
	parameters["function"] = "test";
	$.post(
			url,
			parameters,
			function(data) {
				alert($(data).find("response").attr("success"));
			}, "xml"
		);
	alert("dd");
});