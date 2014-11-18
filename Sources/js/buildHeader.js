"use strict";  // Makes bad syntax into actual errors

$(document).ready(function() {
	$('body').prepend( 
		'<div id="header">'
			+'<div id="header-upper">'
				+'<div class="left">'
				+'<a href="/CMPUT391F"><img class="logo" src="/CMPUT391F/Sources/images/lux.png"></a>'
				+'<a href="/CMPUT391F"><img class="logo" src="/CMPUT391F/Sources/images/ih.png"></a>'
				+'</div>'
				+'<div class="login-bubble right"></div>'
			+'</div>'

			+'<div id="header-lower"><div class="nav-options">'
				+'<div class="nav-option left"><a href="/CMPUT391F">Home</a></div>'
				+'<div class="nav-option left"><a href="/CMPUT391F/Sources/jsp/myimages.jsp?public">My Images</a></div>'
				+'<div class="nav-option left"><a href="/CMPUT391F/upload.html">Upload</a></div>'
				+'<div class="nav-option left"><a href="/CMPUT391F/search.html">Search</a></div>'
		+'</div></div></div>'
	);

	// Find out if logged in
	$.ajax({
	    url: "/CMPUT391F/RestService",
	    data: 'function=userName',
	    type: 'GET',

	    success: function(response){
	        console.log(response);
	        $(".login-bubble").empty();
		$(".nav-options").empty();
	        if (response == "") {
	        	// Not logged in
	        	redirect();
		        $(".login-bubble").append('<a href="/CMPUT391F/login.html">Login</a>' + '<a class="vertical-divider"></a>' + '<a href="/CMPUT391F/signup.html">Register</a>');
			$(".nav-options").append('<div class="nav-option left"><a href="/CMPUT391F">Home</a></div>'
				+'<div class="nav-option left"><a href="/CMPUT391F/Sources/jsp/myimages.jsp?public">View Images</a></div>'
				+'<div class="nav-option left"><a href="/CMPUT391F/upload.html">Upload</a></div>'
				+'<div class="nav-option left"><a href="/CMPUT391F/search.html">Search</a></div>');
	        } else {
	        	$(".login-bubble").append('Welcome:' + response +'<a class="vertical-divider"></a>' + '<a href="/CMPUT391F/Sources/jsp/logout.jsp">Log Out</a>');
			$(".nav-options").append('<div class="nav-option left"><a href="/CMPUT391F">Home</a></div>'
				+'<div class="nav-option left"><a href="/CMPUT391F/Sources/jsp/myimages.jsp?'+response+'">My Images</a></div>'
				+'<div class="nav-option left"><a href="/CMPUT391F/upload.html">Upload</a></div>'
				+'<div class="nav-option left"><a href="/CMPUT391F/search.html">Search</a></div>');
	        	$('div.section').show();
	        }
	    },
	    //Options to tell jQuery not to process data or worry about content-type.
        cache: false,
        contentType: false,
        processData: false
	});

	function redirect() {
		var cur = window.location.href;
		var page = cur.substring(cur.indexOf('CMPUT391F/')+10);
		var pageNoQuery = page.substring(0, page.indexOf('?'));
		var redirectPages = new Array(
		"upload.html",
		"Sources/jsp/myimages.jsp",
		"myPicBrowse",
		"publicPicBrowse",
		"groupPicBrowse"
		);
		if (redirectPages.indexOf(pageNoQuery) >= 0) {
			// Create redirect cookie first
			$.cookie("redirect", page, {path: "/CMPUT391F"});
			window.location.href = cur.substring(0, cur.indexOf("CMPUT391F/")+9) + "/login.html";
		}
	}
});
