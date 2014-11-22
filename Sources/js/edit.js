/*
 * Handles all the client-side logic for the upload page.
 */

"use strict";  // Makes bad syntax into actual errors

// Ensure the DOM tree is loaded before we start executing
// if we want to wait for all images to load as well, use below line instead of $(document).ready
// window.onload = function () {
$(document).ready(function() {

	// Extract pic id.
	var id;
	var query = window.location.search;
	var idToEnd = query.substring(query.indexOf('id=')+3);
	var nextAnd = idToEnd.indexOf('&');
	if (nextAnd >= 0) {
		id = idToEnd.substring(0, nextAnd);
	} else {
		id = idToEnd;
	}
	
	// Find out privledge level
	$.ajax({
	    url: "/CMPUT391F/RestService",
	    data: 'function=singleImage&id=' + id,
	    type: 'GET',
	    success: function(response){
	    	var result = JSON.parse(response);
	        if (result.result == "success") {
				setupEdit(result.pic[0]);
			} else {
				setupDenied(result);
			}	
	    },
	    //Options to tell jQuery not to process data or worry about content-type.
        cache: false,
        contentType: false,
        processData: false
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

function expand($collapsible) {
	var totalHeight = 0;
    $collapsible.find('> *').each(function(){
        totalHeight += $(this).height();
    });

	$collapsible.height(totalHeight);
	setTimeout(function() {
		$collapsible.find('> *').show();
	}, 200);
}


function setupEdit(data) {
	// Sets date picker on all browsers
	$('#date-taken').datepicker();

	// Populates the group dropdown
	populateGroups(function() {
		// Populates the form data
		populateForm(data);

		// Handles submit for upload form
		$("#edit-form").on("submit", function(event) {
			handleSubmit(data.photo_id[0]);
		    return false;
		});
	});
}

function setupDenied(data) {
	$('.section').empty();
	$('.section').append('<br><h1 class="hcenter">' + data.reason[0] + '</h1>');
}

// Populates the group dropdown
function populateGroups(callback) {
	$.ajax({
	    url: "/CMPUT391F/RestService",
	    data: 'function=groupOptions',
	    type: 'GET',

	    success: function(response){
	        if (response == "") {
	        	// No groups returned
	        	$('.permission-validation').append('* No groups found');
	        } else {
	        	$('select[name="group-id"]').empty();
	        	$('select[name="group-id"]').append(response);
	        	var $radGroup = $('input[type="radio"][value="group"]');
	        	$radGroup[0].disabled = false;

	        	// Hides and shows the group selector
				$('input[name="permitted"]').change(function(e) {
					$('input[name="permitted"]').each(function(index, value) {
						if ($(this).is(':checked')) {
							if (this.value == "group") {
								// Expand the group selector
								expand($('tr.collapsible.group-selector'));
							} else {
								// Hide the group selector
								collapse($('tr.collapsible.group-selector'));
							}
						}
					});
				});
	        }
	        if (callback) {
				callback();
			}
	    },
	    //Options to tell jQuery not to process data or worry about content-type.
        cache: false,
        contentType: false,
        processData: false
	});
}

function populateForm(pic) {
	// Show picture
	$('#picToDisplay')[0].src="GetOnePic?big"+pic.photo_id[0];

	// Permission
	var perm = pic.permitted[0];
	if (perm == 1) {
		$('input[type="radio"][name="permitted"][value="1"]').click();
	} else if (perm == 2) {
		$('input[type="radio"][name="permitted"][value="2"]').click();
	} else {
		$('input[type="radio"][name="permitted"][value="group"]').click();
		$('select option[value="' + perm + '"]').prop({selected: true});

	}

	// Subject 
	$('input[name="subject"]').val(pic.subject[0]);

	// Date Taken
	if (pic.timing[0] != null) {
		$('#date-taken').datepicker("setDate", new Date(pic.timing[0]));	
	}

	// Location
	$('input[name="location"]').val(pic.place[0]);

	// Description
	$('textarea[name="description"]').val(pic.description[0]);
}

function handleSubmit(picId) {
		
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

	// Add the group id and description
	data.append("photo-id", picId);
	data.append("group-id", permission_id);
	data.append('description', $('textarea').val());
	// Add function so the RESTController knows what to do with the data
	data.append("function", "editPic");

	// Start upload
	$("#edit-results").empty().append('Saving...<br>');

    $.ajax({
	    url: "/CMPUT391F/RestService",
	    data: data,
	    type: 'POST',
	    success: function(response){
	        $("#edit-results").append(response);
	    },
	    //Options to tell jQuery not to process data or worry about content-type.
        cache: false,
        contentType: false,
        processData: false
	});
}