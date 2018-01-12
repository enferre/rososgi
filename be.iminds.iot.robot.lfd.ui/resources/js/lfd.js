/*
 * UI Mode
 */
var currentMode = "teach";

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
	} else if(currentMode === "demonstrations"){
		$("#menu-demonstrations").addClass("active");
		
	} else if(currentMode === "recordings"){
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
    } else if(e.keyCode == "13"){
    	// enter pressed
    	if(currentMode === "teach"){
    		step();
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


/*
 * record current state as step for current teaching
 */
function step(){
	
}

/*
 * play current teaching / demonstration / recording 
 */
function play(){
	
}

/*
 * stop playing
 */
function stop(){
	
}