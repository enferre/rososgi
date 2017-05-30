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
package be.iminds.iot.robot.input.joy;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import be.iminds.iot.input.joystick.api.JoystickEvent;
import be.iminds.iot.input.joystick.api.JoystickListener;
import be.iminds.iot.robot.api.arm.Arm;
import be.iminds.iot.robot.api.omni.OmniDirectional;

@Component
public class JoystickInputYoubot implements JoystickListener {

	private Arm arm;
	private OmniDirectional base;	
	
	private float velocity = 0.2f;
	private float angular = 0.4f;

	@Override
	public void onEvent(JoystickEvent e) {
		
		switch(e.type){
		case BUTTON_L1_PRESSED:
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
		case JOYSTICK_CHANGED:
			// move base
			float vx = e.axes[1]*velocity;
			float vy = e.axes[0]*velocity;
			float va = e.axes[2]*angular;
			
			base.move(vx, vy, va);
			break;
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
