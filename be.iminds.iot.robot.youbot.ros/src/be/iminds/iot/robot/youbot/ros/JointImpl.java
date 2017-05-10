package be.iminds.iot.robot.youbot.ros;

import java.util.Collections;

import be.iminds.iot.robot.api.arm.Joint;
import be.iminds.iot.robot.api.arm.JointDescription;
import be.iminds.iot.robot.api.arm.JointState;
import be.iminds.iot.robot.api.arm.JointValue;
import be.iminds.iot.robot.api.arm.JointValue.Type;

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
