"use strict";  // Makes bad syntax into actual errors

$(document).ready(function() {
	$('body').prepend('<div id="header"></div>');

	$("#header").append(
		'<a href="/CMPUT391F"><img class="logo" src="/CMPUT391F/Sources/images/lux.png"></a>'
		+'<a href="/CMPUT391F"><img class="logo" src="/CMPUT391F/Sources/images/ih.png"></a>'
		);
});