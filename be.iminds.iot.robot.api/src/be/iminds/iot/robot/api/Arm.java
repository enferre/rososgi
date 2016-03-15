package be.iminds.iot.robot.api;

import java.util.Collection;
import java.util.List;

import org.osgi.util.promise.Promise;

public interface Arm {

	List<String> getJoints();
	
	List<JointState> getState();

	
	Promise<Void> setPosition(int joint, float position);
	
	Promise<Void> setVelocity(int joint, float velocity);

	Promise<Void> setTorque(int joint, float torque);
	
	
	Promise<Void> setPositions(float... position);
	
	Promise<Void> setVelocities(float... velocity);

	Promise<Void> setTorques(float... torque);


	Promise<Void> openGripper(float opening);
	
	Promise<Void> closeGripper();


	Promise<Void> setPositions(Collection<JointValue> positions);
	
	Promise<Void> setVelocities(Collection<JointValue> velocities);

	Promise<Void> setTorques(Collection<JointValue> torques);
	
	
	Promise<Void> waitFor(long time);
	
	Promise<Void> stop();
}
