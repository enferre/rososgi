/*******************************************************************************
 *  ROSOSGi - Bridging the gap between Robot Operating System (ROS) and OSGi
 *  Copyright (C) 2015, 2017  imec - IDLab - UGent
 *
 *  This file is part of DIANNE  -  Framework for distributed artificial neural networks
 *
 *  DIANNE is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *   
 *  Contributors:
 *      Tim Verbelen, Steven Bohez
 *******************************************************************************/
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
	private float speed = 0.15f;
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
					throttle = speed;
					rover.move(throttle, yaw);
					break;
				case "ArrowDown":
				case "s":
					throttle = -speed;
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
				case "p":
					speed += 0.05f;
					if(speed > 1){
						speed = 1;
					}
					System.out.println("Set speed "+speed);
					throttle = throttle > 0 ? speed : throttle < 0 ? -speed : 0;
					rover.move(throttle, yaw);
					break;
				case "l":
					speed -= 0.05f;
					if(speed < 0.15){
						speed = 0.15f;
					}
					System.out.println("Set speed "+speed);
					throttle = throttle > 0 ? speed : throttle < 0 ? -speed : 0;
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
