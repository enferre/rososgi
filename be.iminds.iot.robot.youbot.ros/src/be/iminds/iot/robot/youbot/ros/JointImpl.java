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
package be.iminds.iot.robot.youbot.ros;

import java.util.Collections;

import be.iminds.iot.robot.api.Joint;
import be.iminds.iot.robot.api.JointDescription;
import be.iminds.iot.robot.api.JointState;
import be.iminds.iot.robot.api.JointValue;
import be.iminds.iot.robot.api.JointValue.Type;

public class JointImpl implements Joint {

	private final JointDescription description;
	
	private final ArmImpl arm;
	
	float position;
	float velocity;
	float torque;

	public JointImpl(JointDescription d,
			ArmImpl arm) {
		this.description = d;
		
		this.arm = arm;
	}
	
	@Override
	public String getName() {
		return description.name;
	}

	@Override
	public JointDescription getDescription() {
		return description;
	}
	
	@Override
	public void setPosition(float p) {
		JointValue val = new JointValue(description.name, Type.POSITION, p);
		arm.setPositions(Collections.singleton(val));
	}

	@Override
	public float getPosition() {
		return position;
	}

	@Override
	public void setVelocity(float v) {
		JointValue val = new JointValue(description.name, Type.VELOCITY, v);
		arm.setPositions(Collections.singleton(val));
	}

	@Override
	public float getVelocity() {
		return velocity;
	}

	@Override
	public void setTorque(float t) {
		JointValue val = new JointValue(description.name, Type.TORQUE, t);
		arm.setPositions(Collections.singleton(val));
	}

	@Override
	public float getTorque() {
		return torque;
	}

	@Override
	public JointState getState() {
		return new JointState(description.name, position, velocity, torque);
	}

}
