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
import be.iminds.iot.robot.api.rover.Rover;

@Component
public class JoystickInputRover implements JoystickListener {

	private Rover rover;
	
	@Override
	public void onEvent(JoystickEvent e) {
		
		switch(e.type){
		case JOYSTICK_CHANGED:
			float throttle = clamp(e.axes[1], -1f, 1f);
			float yaw = clamp(e.axes[2], -1f, 1f);
			rover.move(throttle, yaw);
			break;
		}
		
	}

	@Reference()
	void setRover(Rover rover){
		this.rover = rover;
	}

	private float clamp(float f, float low, float high){
		if(f < low)
			return low;
		else if(f > high)
			return high;
		return f;
	}
}
