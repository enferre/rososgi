package be.iminds.iot.robot.api;

public interface Joint {

	String getName();
	
	void setPosition(float p);
	
	float getPosition();
	
	void setVelocity(float v);
	
	float getVelocity();
	
	void setTorque(float t);
	
	float getTorque();
	
	JointState getState();
	
}
