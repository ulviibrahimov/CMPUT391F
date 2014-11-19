/*
 * Handles all the client-side logic for the upload page.
 */

"use strict";  // Makes bad syntax into actual errors

// Ensure the DOM tree is loaded before we start executing
// if we want to wait for all images to load as well, use below line instead of $(document).ready
// window.onload = function () {
$(document).ready(function() {

	populateOwned();
	populateBelong();

	$('#create').on('click', function(event) {
		var cur = window.location.href;
		var baseAdd = cur.substring(0, cur.indexOf('CMPUT391F/')+10);
		window.location.href = baseAdd + "newGroup.html";
	});

	$('body').on('click', 'button.manage', function(event) {
		var cur = window.location.href;
		var baseAdd = cur.substring(0, cur.indexOf('CMPUT391F/')+10);
		var id = $(this).data('id');
		window.location.href = baseAdd + "manageGroup.html?" + id;
	});

	$('body').on('click', 'button.leave', function(event) {
		var id = $(this).data('id');
		handleLeave(id);
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

function populateOwned() {
	$.ajax({
	    url: "/CMPUT391F/RestService",
	    data: 'function=groupOwned',
	    type: 'GET',

	    success: function(response){
	        var result = JSON.parse(response);
	        if (result.result[0] == "fail") {
	        	// No groups returned
	        	$('#owned-groups').append('<tr><td>' + '<b>Failed to get groups.</b>' + '</td></tr>');
	        	$('#owned-groups').append('<tr><td>' + result.reason[0] + '</td></tr>');
	        } else {
	        	delete result.result;
	        	for (var k in result) {
	        		$('#owned-groups').append('<tr><td>' + result[k][0] + '</td><td><button class="manage" type="button" data-id="' + k + '">Manage</button></td></tr>');	
	        	}
	        }
	    },
	    //Options to tell jQuery not to process data or worry about content-type.
        cache: false,
        contentType: false,
        processData: false
	});

}

function populateBelong() {
	$.ajax({
	    url: "/CMPUT391F/RestService",
	    data: 'function=groupBelongNotOwn',
	    type: 'GET',

	    success: function(response){
	        var result = JSON.parse(response);
	        if (result.result[0] == "fail") {
	        	// No groups returned
	        	$('#belong-groups').append('<tr><td>' + '<b>Failed to get groups.</b>' + '</td></tr>');
	        	$('#belong-groups').append('<tr><td>' + result.reason[0] + '</td></tr>');
	        } else {
	        	delete result.result;
	        	for (var k in result) {
	        		$('#belong-groups').append('<tr><td>' + result[k][0] + '</td><td><button class="leave" type="button" data-id="' + k + '">Leave</button></td></tr>');	
	        	}
	        }
	    },
	    //Options to tell jQuery not to process data or worry about content-type.
        cache: false,
        contentType: false,
        processData: false
	});
}

function handleLeave(groupId) {

	// Get all our data in a FormData object
	var data = new FormData();
	data.append("groupId", groupId);
	// Add function so the RESTController knows what to do with the data
	data.append("function", "leaveGroup");

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
	        var $pressedButton = $('button.leave[data-id="' + groupId + '"]');
	        if ($.isNumeric(response)) {
	        	//Success
	        	$pressedButton.hide();
	        	$pressedButton.parent().append('Succesfully left group!');
	        } else {
	        	$pressedButton.parent().parent().after('<tr class="validation"><td>' + "Failed to Leave Group." + '</td><td>' + response + '</td></tr>');
	        }
	    },
	    //Options to tell jQuery not to process data or worry about content-type.
        cache: false,
        contentType: false,
        processData: false
	});
}