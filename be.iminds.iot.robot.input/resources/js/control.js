
window.addEventListener("keydown", keyboard, false);
window.addEventListener("keyup", keyboard, false);
 
function keyboard(e) {
	$.post("control",{'type':e.type,'key':e.key})
}