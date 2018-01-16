/*
 * UI Mode
 */
var currentMode = "teach";

var currentDemonstration = {};

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
    	if(currentMode === "teach"){
    		step('MOVE');
    	}
    } else if(e.keyCode == "79"){
    	// o(pen) pressed
    	if(currentMode === "teach"){
    		step('PLACE');
    	}
    } else if(e.keyCode == "67"){
    	// c(lose) pressed
    	if(currentMode === "teach"){
    		step('PICK');
    	}
    } else if(e.keyCode == "83"){
    	// s(tart) pressed
    	if(currentMode === "teach"){
    		step('START');
    	}
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
	$.post("/lfd", {"method" : "step", "type" : type}, 
			function( data ) {
				var step = data;
				step.n = currentDemonstation.steps.length;
				currentDemonstration.steps.push(step);
				var s = renderTemplate("step", step, $('#steps'));
			}
			, "json");
}

/**
 * play current demonstration / step
 */
function reverse(){
	$.post("/lfd", {"method" : "execute", "demonstration" : JSON.stringify(currentDemonstration), "reversed" : true});
}

function play(i){
	if(i === undefined){
		// play complete sequence
		$.post("/lfd", {"method" : "execute", "demonstration" : JSON.stringify(currentDemonstration)});
	} else {
		// play step
		var step = currentDemonstration.steps[i-1];
		$.post("/lfd", {"method" : "execute", "step" : JSON.stringify(step)});
	}
}


/**
 * stop playing
 */
function stop(){
	$.post("/lfd", {"method" : "stop"});
}

/**
 * load demonstration
 */
function load(name){
	$.post("/lfd", {"method":"load", "name":name}, 
		function( data ) {
			currentDemonstration = data;
			
			$('#name').text(currentDemonstration.name);
			$('#steps').empty();
			var len = currentDemonstration.steps.length;
			var i;
			for(i=0; i<len; i++){
				var step = currentDemonstration.steps[i];
				step.n = i+1;
				var s = renderTemplate("step", step, $('#steps'));
			}
			
			$('#dialog-load').remove();
		}
		, "json");
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
	// remove module-modal specific stuff
	dialog.removeClass("module-modal");
	dialog.find('.module-dialog').removeClass("module-dialog");
	// show dialog
	dialog.modal('show');
}


function showNewDialog(){
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
	// remove module-modal specific stuff
	dialog.removeClass("module-modal");
	dialog.find('.module-dialog').removeClass("module-dialog");
	// show dialog
	dialog.modal('show');	
}


/**
 * save demonstration
 */
function save(){
	$.post("/lfd", {"method" : "save", "demonstration" : JSON.stringify(currentDemonstration)});
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

