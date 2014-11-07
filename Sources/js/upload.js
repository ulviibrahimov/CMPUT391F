/*
 * Handles all the client-side logic for the upload page.
 */

"use strict";  // Makes bad syntax into actual errors

// Ensure the DOM tree is loaded before we start executing
// if we want to wait for all images to load as well, use below line instead of $(document).ready
// window.onload = function () {
$(document).ready(function() {

	// Hides and shows the group selector
	$('input[name="permitted"]').change(function(e) {
		$('input[name="permitted"]').each(function(index, value) {
			if ($(this).is(':checked')) {
				if (this.value == "group") {
					// Expand the group selector
					expand($('tr.collapsible.group-selector'), $('table tr:first-child').height());
				} else {
					// Hide the group selector
					collapse($('tr.collapsible.group-selector'));
				}
			}
		});
	});

	// Handles submit for upload form
	$("#upload-form").on("submit", function(event) {
		
		// Validate!
		var passed = true;

		// Make sure a file is selected, and it's extension is correct.
		var fileName = $('input[name="selected-file"]').val();
		if (fileName.length <= 4) {
			// No file selected
    		$('.selected-file.validation').empty().append('* Please select a valid file.');
    		passed = false;
    	} else if (fileName.substring(fileName.length-4).toLowerCase() != ".jpg" && fileName.substring(fileName.length-4).toLowerCase() != ".gif") {
    		// Invalid extension
			$('.selected-file.validation').empty().append('* Only .jpg and .gif are accepted.');
    		passed = false;
    	} else {
    		$('.selected-file.validation').empty();	
    	}

    	// Check for valid permission
    	var permission_id = 0;
    	$('input[name="permitted"]').each(function(index, entry) {
			if ($(entry).is(':checked')) {
				permission_id = entry.value;
			}
		});
		if (permission_id == 0) {
			$('.permission.validation').empty().append('* Please select a permission level.');
			passed = false;
		} else if (permission_id == "group") {
			permission_id = $('select[name="group-id"]').val();
			if (permission_id == "no-groups") {
				$('.permission.validation').empty().append('* No groups found is not valid.');
				passed = false;
			} else {
				$('.permission.validation').empty();	
			}
		}

    	// Did we pass validation?
    	if (!passed) {
    		return false;
    	}

    	// Start upload
		$("#upload-results").empty().append('Uploading...');

		// Get all our data in a FormData object
		var data = new FormData();
		$('input').each(function(index, element) {
			var type = this.type;
			if (type == "group" || type == "radio") {
				// Don't add anything, just continue
			} else if (type == "file") {
				data.append(this.name, this.files[0]);
			} else {
				data.append(this.name, this.value);
			}
		});

		// Add the group id and description
		data.append("group-id", permission_id);
		data.append('description', $('textarea').val());
		// Add function so the RESTController knows what to do with the data
		data.append("function", "uploadOne");
		


		// TODO Need to send some security info with request to confirm legitimacy

	    $.ajax({
		    url: "/CMPUT391F/RestService",
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

function collapse($collapsible) {
	$collapsible.find('> *').hide();
	$collapsible.height('0px');
}

function expand($collapsible, height) {
	$collapsible.height(height);
	setTimeout(function() {
		$collapsible.find('> *').show();
	}, 200);
}