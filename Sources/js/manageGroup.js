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

	$('#change').on('click', function() {
		var newName = $('input[name="group-name"]').val();
		handleChange(newName, requestedGroup);
	});

	$('#disband').on('click', function(event) {
		var $this = $(this);
		if ($this.hasClass('expanded')) {
			$this.removeClass('expanded');
			collapse($('.collapsible.disband'));
		} else {
			$this.addClass('expanded');
			expand($('.collapsible.disband'));
		}
	});

	$('#cancel-disband').on('click', function(event) {
		$('#disband').removeClass('expanded');
		collapse($('.collapsible.disband'));
	});

	$('#confirm-disband').on('click', function(event) {
		handleDisband(requestedGroup);
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
			expand($this.parent().find('.collapsible.transfer'));	
		}
	});

	$('body').on('click', '.cancel-transfer', function(event) {
		var $this = $(this);
		$this.parent().parent().find('button.transfer').removeClass('expanded');
		collapse($this.parent());
	});

	$('body').on('click', '.confirm-transfer', function(event) {
		var user = $(this).parent().parent().data('member-id');
		handleTransfer(user, requestedGroup);
	});

	$('body').on('click', 'button.remove', function(event) {
		var $this = $(this);
		if ($this.hasClass('expanded')) {
			$this.removeClass('expanded');
			collapse($this.parent().find('.collapsible.remove'));
		} else {
			$this.addClass('expanded');
			expand($this.parent().find('.collapsible.remove'));	
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
					+ '<button class="cancel-transfer small-button" type="button">No</button>'
					+ '<p class="validation transfer"></p></div>'
					+ '<div class="collapsible hspan remove"><br><span>Remove ' + group.members[i].user + '?</span>'
					+ '<button class="confirm-remove small-button" type="button">Yes</button>'
					+ '<button class="cancel-remove small-button" type="button">No</button>'
					+ '<p class="validation remove"></p></div>'
					+ '</div>');
			}
		}
	}
}


function handleChange(newName, group) {
	// Get all our data in a FormData object
	var data = new FormData();
	data.append('name', newName);
	data.append('group', group);
	// Add function so the RESTController knows what to do with the data
	data.append("function", "changeGroupName");

    $.ajax({
	    url: "/CMPUT391F/RestService",
	    data: data,
	    type: 'POST',
	    success: function(response){
	        var $out = $('p.change');
	        if (response == 'success') {
	        	$('#group-name').empty().append(newName);
	        	$out.removeClass('validation');
	        	$out.empty().append('Group name successfully changed.')
				expand($('div.collapsible.change'));
	        } else {
	        	$out.addClass('validation');
	        	$out.empty().append(response);
	        	expand($('div.collapsible.change'));
	        }
	    },
	    //Options to tell jQuery not to process data or worry about content-type.
        cache: false,
        contentType: false,
        processData: false
	});
}

function handleTransfer(user, group) {
	// Get all our data in a FormData object
	var data = new FormData();
	data.append('user', user);
	data.append('group', group);
	
	// Add function so the RESTController knows what to do with the data
	data.append("function", "transferGroupOwnership");

    $.ajax({
	    url: "/CMPUT391F/RestService",
	    data: data,
	    type: 'POST',
	    success: function(response){
	        if (response == 'success') {
	        	var cur = window.location.href;
				var page = cur.substring(0, cur.indexOf('CMPUT391F/')+10);
				window.location.href = page + "groups.html";
	        } else {
	        	var $transfer = $('div[data-member-id="'+user+'"]').find('div.transfer');
	        	$transfer.find('p.transfer').empty().append('Failed to tansfer leadership to ' + user + '.  ' + response);
	        	expand($transfer);
	        }
	    },
	    //Options to tell jQuery not to process data or worry about content-type.
        cache: false,
        contentType: false,
        processData: false
	});
}

function handleDisband(group) {
	// Get all our data in a FormData object
	var data = new FormData();
	data.append('group', group);
	// Add function so the RESTController knows what to do with the data
	data.append("function", "disbandGroup");

    $.ajax({
	    url: "/CMPUT391F/RestService",
	    data: data,
	    type: 'POST',
	    success: function(response){
	        if (response == 'success') {
	        	var cur = window.location.href;
				var page = cur.substring(0, cur.indexOf('CMPUT391F/')+10);
				window.location.href = page + "groups.html";
	        } else {
	        	$('p.disband').empty().append(response);
	        	expand($('div.collapsible.disband'));
	        }
	    },
	    //Options to tell jQuery not to process data or worry about content-type.
        cache: false,
        contentType: false,
        processData: false
	});
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
	        	expand($('div.collapsible.add'));
	        	$('p.add').removeClass('validation');
	        	$('p.add').empty().append('User, ' + user + ', added successfully!');
	        	expand($('div.collapsible.add'));

	        	$('#members').append('<div class="hdivider"></div>'
					+ '<div data-member-id="' + user + '"><b>' + user + '</b>'
					+ '<button class="remove medium-button hright" type="button">Remove</button>'
					+ '<button class="hright transfer" type="button">Transfer Leadership</button>'
					+ '<p class="notice">' + notice + '</p>'
					+ '<div class="collapsible hspan transfer"><br><span>Transfer Leadership to ' + user + '?</span>'
					+ '<button class="confirm-transfer small-button" type="button">Yes</button>'
					+ '<button class="cancel-transfer small-button" type="button">No</button>'
					+ '<p class="validation transfer"></p></div>'
					+ '<div class="collapsible hspan remove"><br><span>Remove ' + user + '?</span>'
					+ '<button class="confirm-remove small-button" type="button">Yes</button>'
					+ '<button class="cancel-remove small-button" type="button">No</button>'
					+ '<p class="validation remove"></p></div>'
					+ '</div>');	
	        } else {
	        	$('p.add').addClass('validation');
	        	$('p.add').empty().append(response);
	        	expand($('div.collapsible.add'));
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
	data.append("function", "kickUser");

    $.ajax({
	    url: "/CMPUT391F/RestService",
	    data: data,
	    type: 'POST',
	    success: function(response){
	        console.log(response);
	        if (response == 'success') {
	        	$('div[data-member-id="'+user+'"]').empty().append('<div data-member-id="' + user + '"><b>' + user + '</b><a class="hright">Successfully removed!</a></div>');
	        	setTimeout(function() {
	        		var $div = $('div[data-member-id="'+user+'"]');
	        		$div.prev().remove();
	        		$div.remove();
	        	}, 2000);
	        } else {
	        	var $remove = $('div[data-member-id="'+user+'"]').find('div.remove');
	        	$remove.find('p.remove').empty().append('Failed to remove ' + user + '.  ' + response);
	        	expand($remove);
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