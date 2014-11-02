/*
 * Handles all the client-side logic for the search page.
 */

"use strict";  // Makes bad syntax into actual errors

// Ensure the DOM tree is loaded before we start executing
// if we want to wait for all images to load as well, use below line instead of $(document).ready
// window.onload = function () {
$(document).ready(function() {


	$("#someID").append("<br>(Added from myImages.js)");
	

	// Create form data to send to servlet
		// var data = new FormData();
		// data.append("function", "uploadOne");
		// data.append("selected-file", $("#selected-file")[0].files[0]);
		// data.append("texty", $("#texty").val());

	// Request to servelt
	 //    $.ajax({
		//     url: "/CMPUT391F/RestService",
		//     data: data,
		//     type: 'POST',
		//     xhr: function() {  // Custom XMLHttpRequest
	 //            var myXhr = $.ajaxSettings.xhr();
	 //            if(myXhr.upload){ // Check if upload property exists
	 //                // myXhr.upload.addEventListener('progress',progressHandlingFunction, false); // For handling the progress of the upload
	 //            }
	 //            return myXhr;
	 //        },

		//     success: function(response){
		//         console.log(response);
		//         $(".section p").append(response);
		//     },
		//     //Options to tell jQuery not to process data or worry about content-type.
	 //        cache: false,
	 //        contentType: false,
	 //        processData: false
		// });	
});

	