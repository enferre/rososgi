package be.iminds.iot.robot.api.arm;

import be.iminds.iot.robot.api.arm.JointDescription;
import be.iminds.iot.robot.api.arm.JointState;

public interface Joint {

	String getName();
	
	JointDescription getDescription();
	
	void setPosition(float p);
	
	float getPosition();
	
	void setVelocity(float v);
	
	float getVelocity();
	
	void setTorque(float t);
	
	float getTorque();
	
	JointState getState();
	
}
