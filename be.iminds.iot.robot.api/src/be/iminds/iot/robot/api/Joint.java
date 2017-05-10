package be.iminds.iot.robot.api;

import be.iminds.iot.robot.api.JointDescription;
import be.iminds.iot.robot.api.JointState;

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
