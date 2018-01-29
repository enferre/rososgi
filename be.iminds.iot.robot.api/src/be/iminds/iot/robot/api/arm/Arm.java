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
package be.iminds.iot.robot.api.arm;

import java.util.Collection;
import java.util.List;

import org.osgi.util.promise.Promise;

import be.iminds.iot.robot.api.JointDescription;
import be.iminds.iot.robot.api.JointState;
import be.iminds.iot.robot.api.JointValue;
import be.iminds.iot.robot.api.Pose;
import be.iminds.iot.robot.api.Robot;

public interface Arm extends Robot<Arm> {

	List<JointDescription> getJoints();
	
	List<JointState> getState();

	Pose getPose();
	
	
	float getSpeed();
	
	void setSpeed(float speed);
	
	Object getProperty(String property);
	
	void setProperty(String property, Object value);


	Promise<Arm> setPosition(int joint, float position);
	
	Promise<Arm> setVelocity(int joint, float velocity);

	Promise<Arm> setTorque(int joint, float torque);
	
	
	Promise<Arm> setPositions(float... position);
	
	Promise<Arm> setVelocities(float... velocity);

	Promise<Arm> setTorques(float... torque);


	Promise<Arm> openGripper();
	
	Promise<Arm> openGripper(float opening);
		
	Promise<Arm> closeGripper();
	
	Promise<Arm> closeGripper(float opening, float effort);


	Promise<Arm> setPositions(Collection<JointValue> positions);
	
	Promise<Arm> setVelocities(Collection<JointValue> velocities);

	Promise<Arm> setTorques(Collection<JointValue> torques);
	

	Promise<Arm> reset();
	
	Promise<Arm> stop(int joint);
	
	Promise<Arm> recover();
	
	
	Promise<Arm> moveTo(float x, float y, float z);
	
	Promise<Arm> moveTo(float x, float y, float z, float ox, float oy, float oz, float ow);
	
	Promise<Arm> moveTo(Pose p);
	
	
	Promise<Arm> move(float vx, float vy, float vz);

	Promise<Arm> move(float vx, float vy, float vz, float ox, float oy, float oz);

	
	void guide();
}
