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
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.Promise;

import be.iminds.iot.robot.api.JointDescription;
import be.iminds.iot.robot.api.JointState;
import be.iminds.iot.robot.api.JointValue;
import be.iminds.iot.robot.api.Pose;
import be.iminds.iot.robot.api.arm.Arm;

@Component(
	configurationPid="be.iminds.iot.robot.panda.ros.Panda", 
	// for now reuse the same config as the ROS impl
	// so we can just swap these bundles
	configurationPolicy=ConfigurationPolicy.REQUIRE)
public class PandaArmImpl implements Arm {

	static {
		try {
			System.loadLibrary("panda");
		} catch (final UnsatisfiedLinkError e) {
		    System.err.println("Native code library panda failed to load. \n"+ e);
		    throw e;
		}
	}
	
	private List<JointDescription> joints;
	
	private float speed = 0.25f;
	
	@Activate
	public void activate(Map<String, String> config) {
		System.out.println("Activate Panda?!");
		String robot_ip = config.get("robot_ip");
		init(robot_ip);
		
		speed(speed);
	}
	
	@Deactivate
	public void deactivate() {
		deinit();
	}
	
	private native void init(String robot_ip);
	
	private native void deinit();
	
	@Override
	public Promise<Arm> waitFor(long time) {
		throw new UnsupportedOperationException("waitFor not implemented...");
	}

	@Override
	public Promise<Arm> stop() {
		Deferred d = new Deferred();
		stop(d);
		return d.getPromise();
	}

	@Override
	public List<JointDescription> getJoints() {
		// TODO
		return null;
	}

	@Override
	public List<JointState> getState() {
		// TODO
		return null;
	}

	@Override
	public Pose getPose() {
		// TODO
		return null;
	}

	@Override
	public float getSpeed() {
		return speed;
	}

	@Override
	public void setSpeed(float speed) {
		this.speed = speed;
		speed(speed);
	}

	@Override
	public Object getProperty(String property) {
		return null;
	}

	@Override
	public void setProperty(String property, Object value) {
	}

	@Override
	public Promise<Arm> setPosition(int joint, float position) {
		throw new UnsupportedOperationException("setPosition not implemented...");
	}

	@Override
	public Promise<Arm> setVelocity(int joint, float velocity) {
		throw new UnsupportedOperationException("setVelocity not implemented...");
	}

	@Override
	public Promise<Arm> setTorque(int joint, float torque) {
		throw new UnsupportedOperationException("setTorque not implemented...");
	}

	@Override
	public Promise<Arm> setPositions(float... position) {
		throw new UnsupportedOperationException("setPositions not implemented...");
	}

	@Override
	public Promise<Arm> setVelocities(float... velocity) {
		throw new UnsupportedOperationException("setVelocities not implemented...");
	}

	@Override
	public Promise<Arm> setTorques(float... torque) {
		throw new UnsupportedOperationException("setTorques not implemented...");
	}

	@Override
	public Promise<Arm> openGripper() {
		return openGripper(0.08f);
	}

	@Override
	public Promise<Arm> openGripper(float opening) {
		return openGripper(opening, 100);
	}

	@Override
	public Promise<Arm> openGripper(float opening, float effort) {
		Deferred<Arm> d = new Deferred();
		open(d, opening, effort);
		return d.getPromise();
	}

	@Override
	public Promise<Arm> closeGripper() {
		return openGripper(0, 100);
	}

	@Override
	public Promise<Arm> closeGripper(float effort) {
		return openGripper(0, effort);
	}

	@Override
	public Promise<Arm> setPositions(Collection<JointValue> positions) {
		throw new UnsupportedOperationException("setPositions not implemented...");
	}

	@Override
	public Promise<Arm> setVelocities(Collection<JointValue> velocities) {
		throw new UnsupportedOperationException("setVelocities not implemented...");
	}

	@Override
	public Promise<Arm> setTorques(Collection<JointValue> torques) {
		throw new UnsupportedOperationException("setTorques not implemented...");
	}

	@Override
	public Promise<Arm> reset() {
		throw new UnsupportedOperationException("reset not implemented...");
	}

	@Override
	public Promise<Arm> stop(int joint) {
		throw new UnsupportedOperationException("stop per joint not implemented...");
	}

	@Override
	public Promise<Arm> recover() {
		Deferred<Arm> d = new Deferred<>();
		recover(d);
		return d.getPromise();
	}

	@Override
	public Promise<Arm> moveTo(float x, float y, float z) {
		Pose current = getPose();
		return moveTo(x, y, z, current.orientation.x, current.orientation.y, current.orientation.z, current.orientation.w);
	}

	@Override
	public Promise<Arm> moveTo(float x, float y, float z, float ox, float oy, float oz, float ow) {
		System.out.println("MOVE!");
		Deferred<Arm> d = new Deferred<>();
		moveTo(d, x, y, z, ox, oy, oz, ow);
		return d.getPromise();
	}

	@Override
	public Promise<Arm> moveTo(Pose p) {
		return moveTo(p.position.x, p.position.y, p.position.z, p.orientation.x, p.orientation.y, p.orientation.z, p.orientation.w);
	}


	private native void speed(float s);
	
	private native float[] joints();
	
	private native float[] pose();

	private native void positions(Deferred<Arm> d, float p1, float p2, float p3, float p4, float p5, float p6, float p7);
	
	private native void velocities(Deferred<Arm> d, float v1, float v2, float v3, float v4, float v5, float v6, float v7);
	
	private native void torques(Deferred<Arm> d, float t1, float t2, float t3, float t4, float t5, float t6, float t7);

	private native void moveTo(Deferred<Arm> d, float x, float y, float z, float ox, float oy, float oz, float ow);

	private native void stop(Deferred<Arm> d);

	private native void recover(Deferred<Arm> d);
	
	private native void open(Deferred<Arm> d, float opening, float effort);
	
}
