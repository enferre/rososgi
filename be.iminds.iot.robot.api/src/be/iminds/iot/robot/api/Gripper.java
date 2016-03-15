package be.iminds.iot.robot.api;

public interface Gripper {

	void open(float opening);
	
	void close();
	
	// TODO methods to IK steer the gripper?
	
}
