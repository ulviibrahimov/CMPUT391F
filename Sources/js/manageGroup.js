/*
 * Handles all the client-side logic for the upload page.
 */

"use strict";  // Makes bad syntax into actual errors

// Ensure the DOM tree is loaded before we start executing
// if we want to wait for all images to load as well, use below line instead of $(document).ready
// window.onload = function () {
$(document).ready(function() {
	var cur = window.location.href;
	var requestedGroup = cur.substring(cur.indexOf('?')+1);

	populate(requestedGroup);

	$('#transfer').on('click', function(event) {
		var $this = $(this);
		if ($this.hasClass('expanded')) {
			$this.removeClass('expanded');
			collapse($('.collapsible.transfer'));
		} else {
			$this.addClass('expanded');
			expand($('.collapsible.transfer'), $('button').height() * 3);	
		}
	});

	$('#confirm-transfer').on('click', function(event) {
		handleTransfer();
	});

	$('#disband').on('click', function(event) {
		var $this = $(this);
		if ($this.hasClass('expanded')) {
			$this.removeClass('expanded');
			collapse($('.collapsible.disband'));
		} else {
			$this.addClass('expanded');
			expand($('.collapsible.disband'), $('button').height() * 3);
		}
	});

	$('#cancel-disband').on('click', function(event) {
		$('#disband').removeClass('expanded');
		collapse($('.collapsible.disband'));
	});

	$('#confirm-disband').on('click', function(event) {
		handleDisband();
	});

	$('#confirm-add').on('click', function(event) {
		handleAdd();
	});

	$(".member").on("click", function(event) {
		handleRemove();
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

function populate(groupId) {
	$.ajax({
	    url: "/CMPUT391F/RestService",
	    data: 'function=getGroup&groupId='+groupId,
	    type: 'GET',

	    success: function(response){
	        var results = JSON.parse(response)
	        if (results.result[0] == "fail") {
	        	$('.section').empty().append('<h1><center>Group Management Denied</center></h1>');
	        	$('.section').append('<p class="hcenter">' + results.reason[0] + '</p>');
	        } else {
	        	populateFields(results);
	        }
	    },
	    //Options to tell jQuery not to process data or worry about content-type.
        cache: false,
        contentType: false,
        processData: false
	});	
}

function populateFields(group) {
	console.log(group);
	$('#group-name').append(group.groupName[0]);

	for (var i in group.members) {
		$('#members').append('<div class="hdivider"></div>'
		+ '<div><b>' + group.members[i].user + '</b>'
		+ '<button class="member medium-button hright" type="button" data-member-id="' + group.members[i].user + '">Remove</button>'
		+ '<p class="notice">' + group.members[i].notice + '</p></div>');
	}
}

function handleTransfer() {
}

function handleDisband() {
}

function handleAdd() {
}

function handleRemove() {
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