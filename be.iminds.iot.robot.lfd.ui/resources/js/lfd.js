/*
 * UI Mode
 */
var currentMode = "teach";

var currentDemonstration = undefined;
var currentRecording = undefined;

/**
 * Set UI modus
 */
function setModus(m){
	if(currentMode === m){
		return;
	}
	
	$(".active").removeClass("active");
	// remove all dialogs
	$(".modal").remove();
	// empty canvas
	$('#canvas').empty();
	
	if(m !== "teach")
		$("#teach-controls").hide();
	
	currentMode = m;
	if(currentMode === "teach"){
		$("#menu-teach").addClass("active");
		$("#teach-controls").show();
	} else if(currentMode === "record"){
		$("#menu-recordings").addClass("active");
		
	} 
}

/**
 * Lock UI mode (handy for interactive demos in kiosk mode) on ctrl+k
 */

window.addEventListener("keydown", checkKeyPressed, false);
window.addEventListener("keyup", checkKeyReleased, false);

var ctrl = false;
var locked = false;

function checkKeyPressed(e) {
	console.log(e.keyCode);
    if (e.keyCode == "17") {
    	ctrl = true
    } else if(e.keyCode == "75"){
    	if(ctrl){
	    	if(!locked){
	    		$(".controls").hide();
	    		locked = true;
	    	} else {
	    		$(".controls").show();
	    		locked = false;
	    	}
    	}
    } else if(e.keyCode == "32"){
    	// space pressed
    	step('MOVE');
    } else if(e.keyCode == "79"){
    	// o(pen) pressed
    	step('PLACE');
    } else if(e.keyCode == "67"){
    	// c(lose) pressed
    	step('PICK');
    } else if(e.keyCode == "83"){
    	// s(tart) pressed
    	step('START');
    }
}

function checkKeyReleased(e) {
    if (e.keyCode == "17") {
    	ctrl = false;
    } 
}

$( document ).ready(function() {
	setModus("teach")
	$("#canvas").mCustomScrollbar({ theme:"minimal" });
	$("#spinnerwrap").hide();
});


/**
 * record current state as step for current teaching
 */
function step(type){
	if(currentMode === "teaching" || currentRecording !== undefined){
		if(currentDemonstration === undefined){
			// just do the step to open/close gripper when needed
			// in case we are recording guided trajectories
			$.post("/lfd", {"method" : "step", "type" : type}); 
		} else {
			$.post("/lfd", {"method" : "step", "type" : type, "name" : currentDemonstration.name}, 
					function( data ) {
						var step = data;
						step.n = currentDemonstration.steps.length + 1;
						currentDemonstration.steps.push(step);
						var s = renderTemplate("step", step, $('#steps'));
					}
					, "json");
		}
	}
}

/**
 * edit a step
 */
function showEditDialog(i){
	var dialog = renderTemplate("dialog", {
		id : "edit",
		title : "Manually edit this step parameters ",
		submit: "Edit",
		cancel: "Cancel"
	}, $(document.body));
	
	var step = currentDemonstration.steps[i-1];
	
	// TODO allow to choose to edit joint state or cartesian state?
	var keys = ["x","y","z","o_x","o_y","o_z","o_w"];

	// TODO what with the camera images?
	$.each(keys, function(index, key){
		// Render toolbox item
		renderTemplate('form-item',
			{
				name: key,
				id: key,
				type: "text",
				value: step[key]
			}, dialog.find('.form-items'));
	});
	
	// submit button callback
	dialog.find(".submit").click(function(e){
		var data = $(this).closest('.modal').find('form').serializeArray();
		var new_step = {};
		new_step['type'] = step['type'];
		new_step['n'] = step['n'];
		$.each(data, function(index, entry){
			new_step[entry.name] = entry.value;
		});
		currentDemonstration.steps[i-1] = new_step;
		
		// TODO only update step?
		renderDemonstration(currentDemonstration);
		
		$('#dialog-edit').remove();
	});
	
	// show dialog
	dialog.modal('show');
}

/**
 * play current demonstration / step
 */
function reverse(){
	$.post("/lfd", {"method" : "execute", 
				    "demonstration" : JSON.stringify(currentDemonstration), 
				    "reversed" : true},
		function( result ) {
			if(!result.success){
				error(result.message);
			}
		}
		, "json");
}

function play(i){
	if(i === undefined){
		// play complete sequence
		$.post("/lfd", {"method" : "execute", "demonstration" : JSON.stringify(currentDemonstration)},
				function( result ) {
					if(!result.success){
						error(result.message);
					}
				}
				, "json");
	} else {
		// play step
		var step = currentDemonstration.steps[i-1];
		$.post("/lfd", {"method" : "execute", "step" : JSON.stringify(step)},
				function( result ) {
					if(!result.success){
						error(result.message);
					}
				}
				, "json");
	}
}


/**
 * interrupt playing
 */
function interrupt(){
	$.post("/lfd", {"method" : "interrupt"});
}

/**
 * recover form arm error
 */
function recover(){
	$.post("/lfd", {"method" : "recover"});
}

/**
 * load demonstration
 */
function load(name){
	$.post("/lfd", {"method":"load", "name":name}, 
		function( data ) {
			currentDemonstration = data;
			
			renderDemonstration(currentDemonstration);
			
			if(currentMode === "teach"){
				currentMode = "teaching";
				$.post("/lfd", {"method":"guide"}); 	
			}
			
			$('#dialog-load').remove();
		}
		, "json");
}


function renderDemonstration(d){
	$('#name').text(d.name);
	$('#steps').empty();
	var len = d.steps.length;
	var i;
	for(i=0; i<len; i++){
		var step = d.steps[i];
		step.n = i+1;
		var s = renderTemplate("step", step, $('#steps'));
	}
}


function showLoadDialog(){
	var dialog = renderTemplate("dialog", {
		id : "load",
		title : "Load a demonstration ",
		submit: "Load",
		cancel: "Cancel"
	}, $(document.body));
	
	dialog.find('.content').append("<p>Select a demonstration to load.</p>")
	
	renderTemplate("form-dropdown", 
			{	
				name: "Demonstration: "
			},
			dialog.find('.form-items'));
	
	$.post("/lfd", {"method" : "demonstrations"}, 
			function( data ) {
				data.sort();
				$.each(data, function(index, name){
					dialog.find('.options').append("<option value="+name+">"+name+"</option>")
				});
			}
			, "json");
	
	// submit button callback
	dialog.find(".submit").click(function(e){
		var name = $(this).closest('.modal').find('.options').val();
		load(name);
	});
	
	// remove cancel button
	dialog.find('.cancel').remove();
	// show dialog
	dialog.modal('show');
}


function showNewDialog(){
	currentMode = "teach";

	var dialog = renderTemplate("dialog", {
		id : "load",
		title : "Create a new demonstration ",
		submit: "Create",
		cancel: "Cancel"
	}, $(document.body));
	
	dialog.find('.content').append("<p>Select a demonstration name.</p>")
	
	renderTemplate("form-item", 
			{	
				id: "name",
				name: "Name: "
			},
			dialog.find('.form-items'));
	
	// submit button callback
	dialog.find(".submit").click(function(e){
		var name = $(this).closest('.modal').find('.name').val();
		load(name);
	});
	
	// remove cancel button
	dialog.find('.cancel').remove();
	// show dialog
	dialog.modal('show');	
}


/**
 * save demonstration
 */
function save(){
	$.post("/lfd", {"method" : "save", "demonstration" : JSON.stringify(currentDemonstration)});
}


/**
 * toggle recording
 * @returns
 */
function record(){
	if(currentRecording === undefined){
		// start recording
		$.post("/lfd", {"method" : "record"}, 
		function( data ) {
			console.log(data);
			currentRecording = data;
			$('#menu-record').addClass("record");
		}, "json");
	} else {
		// stop recording
		$.post("/lfd", {"method" : "stop", "id" : currentRecording}, 
		function( data ) {
			$('#menu-record').removeClass("record")
			currentRecording = undefined;
		}, "json");
	}
}

/*
 * Helper functions
 */

/**
 * render a template and append to the target
 */
function renderTemplate(template, options, target){
	var template = $('#'+template).html();
	Mustache.parse(template);
	var rendered = Mustache.render(template, options);
	return $(rendered).appendTo(target);
}

/**
 * render an error
 */
function error(message){
	renderTemplate("error", {
		'message' : message
	}, $("#alerts"));
}

