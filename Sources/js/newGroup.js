/*
 * Handles all the client-side logic for the newGroup page.
 */

"use strict";  // Makes bad syntax into actual errors

// Ensure the DOM tree is loaded before we start executing
// if we want to wait for all images to load as well, use below line instead of $(document).ready
// window.onload = function () {
$(document).ready(function() {

	$('#save').on('click', function(event) {
		handleSave();
	    return false;
	});
	
});

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

function handleSave() {
	$('.validation').empty();

	// Get all our data in a FormData object
	var data = new FormData();
	data.append('group-name', $('input[name="group-name"]').val());
	data.append('notice', $('textarea[name="notice"]').val());
	data.append('date', getCurrentDate());

	// Add function so the RESTController knows what to do with the data
	data.append("function", "createGroup");

	// Try saving group	
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
	    success: function(response) {
	    	if ($.isNumeric(response)) {
	    		// Group created successfully, redirect to manage page
	    		var cur = window.location.href;
				var baseAdd = cur.substring(0, cur.indexOf('CMPUT391F/')+10);
				window.location.href = baseAdd + "manageGroup.html?" + response;
	    	} else {
	    		// An error was encountered
	    		$('.name.validation').append(response);
	    	}
	    },
	    //Options to tell jQuery not to process data or worry about content-type.
        cache: false,
        contentType: false,
        processData: false
	});
}

function getCurrentDate() {	
	var d = new Date();

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
	return val;
}