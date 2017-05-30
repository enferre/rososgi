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
import be.iminds.iot.robot.api.arm.Arm;
import be.iminds.iot.robot.api.omni.OmniDirectional;
import be.iminds.iot.input.keyboard.api.KeyboardListener;

@Component( 
	    property = {"aiolos.proxy=false" }, 
		immediate = true)
public class KeyboardInputYoubot implements KeyboardListener {

	private Arm arm;
	private OmniDirectional base;
	
	private float velocity = 0.2f;
	private float angular = 0.4f;
	
	private float vx = 0;
	private float vy = 0;
	private float va = 0;
	
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
					vx = velocity;
					base.move(vx, vy, va);
					break;
				case "ArrowDown":
				case "s":
					vx = -velocity;
					base.move(vx, vy, va);
					break;
				case "ArrowLeft":
				case "a":
					vy = velocity;
					base.move(vx, vy, va);
					break;
				case "ArrowRight":
				case "d":
					vy = -velocity;
					base.move(vx, vy, va);
					break;
				case "q":
					va = angular;
					base.move(vx, vy, va);
					break;
				case "e":
					va = -angular;
					base.move(vx, vy, va);
					break;
				case "t":
					arm.setPosition(0, arm.getJoints().get(0).positionMax);
					break;
				case "g":
					arm.setPosition(0, arm.getJoints().get(0).positionMin);
					break;
				case "y":
					arm.setPosition(1, arm.getJoints().get(1).positionMax);
					break;
				case "h":
					arm.setPosition(1, arm.getJoints().get(1).positionMin);
					break;
				case "u":
					arm.setPosition(2, arm.getJoints().get(2).positionMax);
					break;
				case "j":
					arm.setPosition(2, arm.getJoints().get(2).positionMin);
					break;
				case "i":
					arm.setPosition(3, arm.getJoints().get(3).positionMax);
					break;
				case "k":
					arm.setPosition(3, arm.getJoints().get(3).positionMin);
					break;
				case "o":
					arm.setPosition(4, arm.getJoints().get(4).positionMax);
					break;
				case "l":
					arm.setPosition(4, arm.getJoints().get(4).positionMin);
					break;	
				case "m":
					arm.openGripper();
					break;
				case "n":
					arm.closeGripper();
					break;	
				case "Enter":
					// grip action
					arm.openGripper()
						.then(p -> arm.setPositions(2.92f, 0.0f, 0.0f, 0.0f, 2.875f))
						.then(p -> arm.setPositions(2.92f, 1.76f, -1.37f, 2.55f))
						.then(p -> arm.closeGripper())
						.then(p -> arm.setPositions(0.01f, 0.8f))
						.then(p -> arm.setPositions(0.01f, 0.8f, -1f, 2.9f))
						.then(p -> arm.openGripper())
						.then(p -> arm.setPosition(1, -1.3f))
						.then(p -> arm.reset());
					break;
				case "Backspace":
					arm.reset();
			}
		} else if(e.type == Type.RELEASED){
			switch(e.key){
				case "ArrowUp":
				case "w":
				case "ArrowDown":
				case "s":
					vx = 0;
					base.move(vx, vy, va);
					break;
				case "ArrowLeft":
				case "a":
				case "ArrowRight":
				case "d":
					vy = 0;
					base.move(vx, vy, va);
					break;
				case "q":
				case "e":
					va = 0;
					base.move(vx, vy, va);
					break;
				case "t":
				case "g":
					arm.setVelocity(0, 0).then(p -> arm.stop(0));
					break;
				case "y":
				case "h":
					arm.setVelocity(1, 0).then(p -> arm.stop(1));
					break;
				case "u":
				case "j":
					arm.setVelocity(2, 0).then(p -> arm.stop(2));
					break;
				case "i":
				case "k":
					arm.setVelocity(3, 0).then(p -> arm.stop(3));
					break;
				case "o":
				case "l":
					arm.setVelocity(4, 0).then(p -> arm.stop(4));
					break;
			}
		}
	}

	
	@Reference
	void setArm(Arm arm){
		this.arm = arm;
	}
	
	@Reference
	void setBase(OmniDirectional base){
		this.base = base;
	}

}
