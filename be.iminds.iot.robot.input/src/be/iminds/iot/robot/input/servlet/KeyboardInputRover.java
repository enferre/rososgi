package be.iminds.iot.robot.input.servlet;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.HttpService;

import be.iminds.iot.input.keyboard.api.KeyboardEvent;
import be.iminds.iot.input.keyboard.api.KeyboardEvent.Type;
import be.iminds.iot.input.keyboard.api.KeyboardListener;
import be.iminds.iot.robot.api.rover.Rover;

@Component( 
	    property = {"aiolos.proxy=false" }, 
		immediate = true)
public class KeyboardInputRover implements KeyboardListener {

	private Rover rover;
	
	private float throttle = 0;
	private float yaw = 0;
	
	@Reference
	void setHttpService(HttpService http) {
		try {
			// TODO How to register resources with whiteboard pattern?
			http.registerResources("/robot", "res", null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	@Override
	public void onEvent(KeyboardEvent e){
		// control base
		if(e.type == Type.PRESSED){
			switch(e.key){
				case "ArrowUp":
				case "w":
					throttle = 1f;
					rover.move(throttle, yaw);
					break;
				case "ArrowDown":
				case "s":
					throttle = -1f;
					rover.move(throttle, yaw);
					break;
				case "ArrowLeft":
				case "a":
					yaw = -1f;
					rover.move(throttle, yaw);
					break;
				case "ArrowRight":
				case "d":
					yaw = 1f;
					rover.move(throttle, yaw);
					break;
			}
		} else if(e.type == Type.RELEASED){
			switch(e.key){
				case "ArrowUp":
				case "w":
				case "ArrowDown":
				case "s":
					throttle = 0;
					rover.move(throttle, yaw);
					break;
				case "ArrowLeft":
				case "a":
				case "ArrowRight":
				case "d":
					yaw = 0;
					rover.move(throttle, yaw);
					break;
			}
		}
	}

	
	@Reference
	void setRover(Rover rover){
		this.rover = rover;
	}

}
