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
		var newMember = $('input[name="new-member"]').val();
		var notice = $('textarea[name="notice"]').val();
		handleAdd(newMember, requestedGroup, notice);
	});

	$('body').on('click', 'button.transfer', function(event) {
		var $this = $(this);
		if ($this.hasClass('expanded')) {
			$this.removeClass('expanded');
			collapse($this.parent().find('.collapsible.transfer'));
		} else {
			$this.addClass('expanded');
			expand($this.parent().find('.collapsible.transfer'), $('button').height() * 3);	
		}
	});

	$('body').on('click', '.cancel-transfer', function(event) {
		var $this = $(this);
		$this.parent().parent().find('button.transfer').removeClass('expanded');
		collapse($this.parent());
	});

	$('body').on('click', '.confirm-transfer', function(event) {
		handleTransfer();
	});

	$('body').on('click', 'button.remove', function(event) {
		var $this = $(this);
		if ($this.hasClass('expanded')) {
			$this.removeClass('expanded');
			collapse($this.parent().find('.collapsible.remove'));
		} else {
			$this.addClass('expanded');
			expand($this.parent().find('.collapsible.remove'), $('button').height() * 3);	
		}
	});

	$('body').on('click', '.cancel-remove', function(event) {
		var $this = $(this);
		$this.parent().parent().find('button.remove').removeClass('expanded');
		collapse($this.parent());
	});

	$('body').on('click', '.confirm-remove', function(event) {
		var user = $(this).parent().parent().data('member-id');
		handleRemove(user, requestedGroup);
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

	if (!group.members) {
		$('.transfer').hide();
	} else {
		for (var i in group.members) {
			if (group.members[i].owner) {
				$('#members').prepend('<div class="hdivider"></div>'
					+ '<div><b>' + group.members[i].user + '</b>'
					+ '<p class="notice">' + group.members[i].notice + '</p></div>');
			} else {
				$('#members').append('<div class="hdivider"></div>'
					+ '<div data-member-id="' + group.members[i].user + '"><b>' + group.members[i].user + '</b>'
					+ '<button class="remove medium-button hright" type="button">Remove</button>'
					+ '<button class="hright transfer" type="button">Transfer Leadership</button>'
					+ '<p class="notice">' + group.members[i].notice + '</p>'
					+ '<div class="collapsible hspan transfer"><br><span>Transfer Leadership to ' + group.members[i].user + '?</span>'
					+ '<button class="confirm-transfer small-button" type="button">Yes</button>'
					+ '<button class="cancel-transfer small-button" type="button">No</button></div>'
					+ '<div class="collapsible hspan remove"><br><span>Remove ' + group.members[i].user + '?</span>'
					+ '<button class="confirm-remove small-button" type="button">Yes</button>'
					+ '<button class="cancel-remove small-button" type="button">No</button></div>'
					+ '</div>');
			}
		}
	}
}

function handleTransfer() {
}

function handleDisband() {
}

function handleAdd(user, group, notice) {
	// Get all our data in a FormData object
	var data = new FormData();
	data.append('user', user);
	data.append('group', group);
	data.append('notice', notice);
	data.append('date', getCurrentDate());
	
	// Add function so the RESTController knows what to do with the data
	data.append("function", "addUserToGroup");

    $.ajax({
	    url: "/CMPUT391F/RestService",
	    data: data,
	    type: 'POST',
	    success: function(response){
	        if (response == 'success') {
	        	expand($('div.collapsible.add'), $('button').height());
	        	$('div.add-result').removeClass('validation');
	        	$('div.add-result').empty().append('User, ' + user + ', added successfully!');

	        	$('#members').append('<div class="hdivider"></div>'
					+ '<div data-member-id="' + user + '"><b>' + user + '</b>'
					+ '<button class="remove medium-button hright" type="button">Remove</button>'
					+ '<button class="hright transfer" type="button">Transfer Leadership</button>'
					+ '<p class="notice">' + notice + '</p>'
					+ '<div class="collapsible hspan transfer"><br><span>Transfer Leadership to ' + user + '?</span>'
					+ '<button class="confirm-transfer small-button" type="button">Yes</button>'
					+ '<button class="cancel-transfer small-button" type="button">No</button></div>'
					+ '<div class="collapsible hspan remove"><br><span>Remove ' + user + '?</span>'
					+ '<button class="confirm-remove small-button" type="button">Yes</button>'
					+ '<button class="cancel-remove small-button" type="button">No</button></div>'
					+ '</div>');	
	        } else {
	        	$('div.add-result').addClass('validation');
	        	$('div.add-result').empty().append(response);
	        	expand($('div.collapsible.add'), $('div.add-result').height());
	        }
	    },
	    //Options to tell jQuery not to process data or worry about content-type.
        cache: false,
        contentType: false,
        processData: false
	});
}

function handleRemove(user, group) {
	// Get all our data in a FormData object
	var data = new FormData();
	data.append('user', user);
	data.append("group", group);
	
	// Add function so the RESTController knows what to do with the data
	data.append("function", "removeUserFromGroup");

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