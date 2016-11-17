
window.addEventListener("keydown", keyevent, false);
window.addEventListener("keyup", keyevent, false);
 
function keyevent(e) {
	 var xhr = new XMLHttpRequest();
	 xhr.open("POST", "/keyboard/servlet", true);
	 xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	 xhr.send('type='+e.type+'&key='+e.key);
}