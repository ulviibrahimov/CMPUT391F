"use strict";  // Makes bad syntax into actual errors

$(document).ready(function() {
	$('body').prepend( 
		'<div id="header">'
			+'<div id="header-upper">'
				+'<a href="/CMPUT391F"><img class="logo" src="/CMPUT391F/Sources/images/lux.png"></a>'
				+'<a href="/CMPUT391F"><img class="logo" src="/CMPUT391F/Sources/images/ih.png"></a>'
			+'</div>'

			+'<div id="header-lower"><div class="nav-options">'
				+'<div class="nav-option"><a href="/CMPUT391F">Home</a></div>'
				+'<div class="nav-option"><a href="/CMPUT391F/myimages.html">My Images</a></div>'
				+'<div class="nav-option"><a href="/CMPUT391F/upload.html">Upload</a></div>'
				+'<div class="nav-option"><a href="/CMPUT391F/search.html">Search</a></div>'
				+'<div class="nav-option"><a href="/CMPUT391F/login.html">Login</a></div>'
		+'</div></div></div>'
	);
});