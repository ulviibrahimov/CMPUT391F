/*
 * Handles all the client-side logic for the upload page.
 */

"use strict";  // Makes bad syntax into actual errors

// Ensure the DOM tree is loaded before we start executing
// if we want to wait for all images to load as well, use below line instead of $(document).ready
// window.onload = function () {
$(document).ready(function() {

	// Sets date picker on all browsers
	$('#date-taken').datepicker();

	// Only set up uploadify on non-firefox
	if (navigator.userAgent.toLowerCase().indexOf('firefox') == -1) {
		// Set uploadify on browse button
		$('#upload-here').uploadify({
			'auto'     : false,
	        'swf'      : '/CMPUT391F/Sources/uploadify/uploadify.swf',
	        'uploader' : '/CMPUT391F/RestService',
	        'fileTypeExts' : '*.gif; *.jpg',
	        'onUploadStart' : function(file) {
				if (file.type.toLowerCase() != ".jpg" && file.type.toLowerCase() != ".gif") {
					// Invalid extension
					$('.Filedata.validation').append('* Only .jpg and .gif are accepted.');
					$('#upload-here').uploadify('stop');
				}
			},
	        'onUploadSuccess' : function(file, data, response) {
	        	$("#upload-results").append('<br>' + file.name + ': ' + data);
	    	},
	    	'onUploadError' : function(file, errorCode, errorMsg, errorString) {
	            $("#upload-results").append('<br>' + file.name + ': could not be uploaded. ' + errorString);
	        }
	    });
	}

	// Populates the group dropdown
	populateGroups();

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
		if ($('input[name="Filedata"]').length > 0) {
			handleSubmit();
		} else {
			var data = validateAndGather();
		    if(data.pass) {
		    	delete data.pass;
		    	$('#upload-here').uploadify("settings", "formData", data);
		    	$("#upload-results").empty();
				$('#upload-here').uploadify('upload','*')
		    }
		}
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

// Populates the group dropdown
function populateGroups() {
	$.ajax({
	    url: "/CMPUT391F/RestService",
	    data: 'function=groupOptions',
	    type: 'GET',

	    success: function(response){
	        if (response == "") {
	        	// No groups returned
	        	$('.permission-validation').append('* No groups found');
	        } else {
	        	$('select[name="groupid"]').empty();
	        	$('select[name="groupid"]').append(response);
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
		
	var formdata = new FormData();
	var data = validateAndGather();
	if (!data.pass) {
		return false;
	} else {
		delete data.pass;
		for (var k in data) {
			if (data[k] == undefined) {
				formdata.append(k, null);
			} else {
				formdata.append(k, data[k]);	
			}
		}
		var fileEl = $('input[type="file"]')[0];
		formdata.append(fileEl.name, fileEl.files[0]);
	}

	// Start upload
	$("#upload-results").empty().append('Uploading...');

    $.ajax({
	    url: "/CMPUT391F/RestService",
	    data: formdata,
	    type: 'POST',
	    success: function(response){
	        $("#upload-results").append('<br>' + response);
	    },
	    //Options to tell jQuery not to process data or worry about content-type.
        cache: false,
        contentType: false,
        processData: false
	});
}

function validateAndGather() {
	var data = {}

	// Validate!
	var passed = true;
	$('.validation').empty();

	// Check for valid permission
	var permission_id = 0;
	$('input[name="permitted"]').each(function(index, entry) {
		if ($(entry).is(':checked')) {
			permission_id = entry.value;
		}
	});
	if (permission_id == 'group') {
		permission_id = $('select[name="groupid"]').val();
	} else if (permission_id == 0) {
		$('.permission.validation').append('* Please select a permission level.');
		passed = false;
	}

	// Check for valid date
	var dateEl = $('input[name="date"]')[0];
	if (dateEl.value == "") {
		// Empty / null date
		data[dateEl.name] = dateEl.value;
	} else {
		// There is input on date, check for validity
		var d = new Date(dateEl.value);
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
			data[dateEl.name] = val;
		}
	}

	// Did we pass validation?
	if (!passed) {
		data.pass = false;
		return data;
	} else {
		data.pass = true;
	}

	// Add subject
	var subEl = $('input[name="subject"]')[0];
	data[subEl.name] = subEl.value;

	// Add location
	var locEl = $('input[name="location"]')[0];
	data[locEl.name] = locEl.value;

	// Add others
	data['groupid'] = permission_id;
	data['description'] = $('textarea').val();
	data['function'] = "uploadOne";

	return data;
}