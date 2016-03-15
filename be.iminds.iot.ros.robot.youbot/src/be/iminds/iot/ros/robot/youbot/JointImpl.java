package be.iminds.iot.ros.robot.youbot;

import java.util.Collections;

import be.iminds.iot.robot.api.Joint;
import be.iminds.iot.robot.api.JointState;
import be.iminds.iot.robot.api.JointValue;
import be.iminds.iot.robot.api.JointValue.Type;

public class JointImpl implements Joint {

	private final String name;
	
	private final ArmImpl arm;
	
	float position;
	float velocity;
	float torque;

	public JointImpl(String name,
			ArmImpl arm) {
		this.name = name;
		
		this.arm = arm;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setPosition(float p) {
		JointValue val = new JointValue(name, Type.POSITION, p);
		arm.setPositions(Collections.singleton(val));
	}

	@Override
	public float getPosition() {
		return position;
	}

	@Override
	public void setVelocity(float v) {
		JointValue val = new JointValue(name, Type.VELOCITY, v);
		arm.setPositions(Collections.singleton(val));
	}

	@Override
	public float getVelocity() {
		return velocity;
	}

	@Override
	public void setTorque(float t) {
		JointValue val = new JointValue(name, Type.TORQUE, t);
		arm.setPositions(Collections.singleton(val));
	}

	@Override
	public float getTorque() {
		return torque;
	}

	@Override
	public JointState getState() {
		return new JointState(position, velocity, torque);
	}

}
