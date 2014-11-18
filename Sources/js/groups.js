/*
 * Handles all the client-side logic for the upload page.
 */

"use strict";  // Makes bad syntax into actual errors

// Ensure the DOM tree is loaded before we start executing
// if we want to wait for all images to load as well, use below line instead of $(document).ready
// window.onload = function () {
$(document).ready(function() {
	$('#create').on('click', function(event) {
		var cur = window.location.href;
		var baseAdd = cur.substring(0, cur.indexOf('CMPUT391F/')+10);
		window.location.href = baseAdd + "newGroup.html";
	});

	// $('button.manage').on('click', function(event) {
	// 	var cur = window.location.href;
	// 	var baseAdd = cur.substring(0, cur.indexOf('CMPUT391F/')+10);
	// 	window.location.href = baseAdd + "manageGroups.html?";
	// });

	// // Handles submit for upload form
	// $("#upload-form").on("submit", function(event) {
	// 	handleSubmit();
	//     return false;
	// });
	
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

// Populates the group dropdown
function populateGroups() {
	$.ajax({
	    url: "/CMPUT391F/RestService",
	    data: 'function=groups',
	    type: 'GET',

	    success: function(response){
	        console.log("Groups: "+response);
	        if (response == "") {
	        	// No groups returned
	        	$('.permission-validation').append('* No groups found');
	        } else {
	        	$('select[name="group-id"]').empty();
	        	$('select[name="group-id"]').append(response);
	        	var $radGroup = $('input[type="radio"][value="group"]');
	        	$radGroup[0].disabled = false;
	        }
	    },
	    //Options to tell jQuery not to process data or worry about content-type.
        cache: false,
        contentType: false,
        processData: false
	});
}

function handleSubmit() {
		
	// Validate!
	var passed = true;
	$('.validation').empty();	

	// Make sure a file is selected, and it's extension is correct.
	var fileName = $('input[name="selected-file"]').val();
	if (fileName.length <= 4) {
		// No file selected
		$('.selected-file.validation').append('* Please select a valid file.');
		passed = false;
	} else if (fileName.substring(fileName.length-4).toLowerCase() != ".jpg" && fileName.substring(fileName.length-4).toLowerCase() != ".gif") {
		// Invalid extension
		$('.selected-file.validation').append('* Only .jpg and .gif are accepted.');
		passed = false;
	}

	// Check for valid permission
	var permission_id = 0;
	$('input[name="permitted"]').each(function(index, entry) {
		if ($(entry).is(':checked')) {
			permission_id = entry.value;
		}
	});
	if (permission_id == 'group') {
		permission_id = $('select[name="group-id"]').val();
	} else if (permission_id == 0) {
		$('.permission.validation').append('* Please select a permission level.');
		passed = false;
	}

	// Get all our data in a FormData object
	var data = new FormData();
	$('input').each(function(index, element) {
		var type = this.type;
		if (type == "group" || type == "radio") {
			// Don't add anything, just continue
		} else if (type == "file") {
			data.append(this.name, this.files[0]);
		} else if (this.name == "date") {
			if (this.value == "") {
				// Empty / null date
				data.append(this.name, this.value);
			} else {
				// There is input on date, check for validity
				var d = new Date(this.value);
				if (d == "Invalid Date") {
					$('.date.validation').append('* Invalid date.');
					passed = false;
				} else {
					// Transform date to correct format
					var month = (d.getMonth()+1);
					if ((month+"").length < 2) {
						month = "0" + month;
					}
					var date = d.getDate();
					if ((date+"").length < 2) {
						date = "0" + date;
					}
					var val = d.getFullYear() + "-" + month + "-" + date;
					data.append(this.name, val);
				}
			}
		} else {
			data.append(this.name, this.value);
		}
	});

	// Did we pass validation?
	if (!passed) {
		return false;
	}

	// Start upload
	$("#upload-results").empty().append('Uploading...');

	// Add the group id and description
	data.append("group-id", permission_id);
	data.append('description', $('textarea').val());
	// Add function so the RESTController knows what to do with the data
	data.append("function", "uploadOne");

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
}