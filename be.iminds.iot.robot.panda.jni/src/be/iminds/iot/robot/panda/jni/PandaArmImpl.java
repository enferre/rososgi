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
package be.iminds.iot.robot.panda.jni;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.util.promise.Promise;

import be.iminds.iot.robot.api.JointDescription;
import be.iminds.iot.robot.api.JointState;
import be.iminds.iot.robot.api.JointValue;
import be.iminds.iot.robot.api.Pose;
import be.iminds.iot.robot.api.arm.Arm;

@Component
public class PandaArmImpl implements Arm {

	static {
		try {
			System.loadLibrary("panda");
		} catch (final UnsatisfiedLinkError e) {
		    System.err.println("Native code library panda failed to load. \n"+ e);
		    throw e;
		}
	}
	
	@Activate
	public void activate(Map<String, String> config) {
		String robot_ip = config.get("robot_ip");
		init(robot_ip);
	}
	
	@Deactivate
	public void deactivate() {
		deinit();
	}
	
	private native void init(String robot_ip);
	
	private native void deinit();
	
	@Override
	public Promise<Arm> waitFor(long time) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Promise<Arm> stop() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<JointDescription> getJoints() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<JointState> getState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Pose getPose() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float getSpeed() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setSpeed(float speed) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object getProperty(String property) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setProperty(String property, Object value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Promise<Arm> setPosition(int joint, float position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Promise<Arm> setVelocity(int joint, float velocity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Promise<Arm> setTorque(int joint, float torque) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Promise<Arm> setPositions(float... position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Promise<Arm> setVelocities(float... velocity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Promise<Arm> setTorques(float... torque) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Promise<Arm> openGripper() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Promise<Arm> openGripper(float opening) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Promise<Arm> openGripper(float opening, float effort) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Promise<Arm> closeGripper() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Promise<Arm> closeGripper(float effort) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Promise<Arm> setPositions(Collection<JointValue> positions) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Promise<Arm> setVelocities(Collection<JointValue> velocities) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Promise<Arm> setTorques(Collection<JointValue> torques) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Promise<Arm> reset() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Promise<Arm> stop(int joint) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Promise<Arm> recover() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Promise<Arm> moveTo(float x, float y, float z) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Promise<Arm> moveTo(float x, float y, float z, float ox, float oy, float oz, float ow) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Promise<Arm> moveTo(Pose p) {
		// TODO Auto-generated method stub
		return null;
	}

}
