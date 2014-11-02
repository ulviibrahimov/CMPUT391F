/*
 * Handles all the client-side logic for the upload page.
 */

"use strict";  // Makes bad syntax into actual errors

// Ensure the DOM tree is loaded before we start executing
// if we want to wait for all images to load as well, use below line instead of $(document).ready
// window.onload = function () {
$(document).ready(function() {


	// Handles submit for upload form
	$("#upload-form").on("submit", function(event) {
		$("#upload-results").empty().append("Uploading...");
		
		// Validate!  Make sure a file is selected, and it's extension is correct.

		//if (fileName.length() == 0) {
		//         		return "No file Selected.";
		//         	}
		//         	String extension = fileName.substring(fileName.length()-4).toLowerCase();
		//         	//r+="&nbsp;&nbspExtension: "+extension+"<br>";
		//         	if (!extension.equals(".jpg") && !extension.equals(".gif")) {
		//         		return "Invalid file.  Only image files (.jpg and .gif) are accepted.";
		//         	}


		var data = new FormData();
		data.append("function", "uploadOne");
		data.append("selected-file", $("#selected-file")[0].files[0]);
		data.append("texty", $("#texty").val());


		// TODO Need to send some security info with request to confirm legitimacy

	    $.ajax({
		    url: "../RestService",
		    data: data,
		    type: 'POST',
		    xhr: function() {  // Custom XMLHttpRequest
	            var myXhr = $.ajaxSettings.xhr();
	            if(myXhr.upload){ // Check if upload property exists
	                // myXhr.upload.addEventListener('progress',progressHandlingFunction, false); // For handling the progress of the upload
	            }
	            return myXhr;
	        },

		    success: function(response){
		        console.log(response);
		        $("#upload-results").append(response);
		    },
		    //Options to tell jQuery not to process data or worry about content-type.
	        cache: false,
	        contentType: false,
	        processData: false
		});

	    return false;
	});
	
});

function progressHandlingFunction(e){
    if(e.lengthComputable){
        $('progress').attr({value:e.loaded,max:e.total});
    }
}
	