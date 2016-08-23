package be.iminds.iot.robot.api;

import java.util.Collection;
import java.util.List;

import org.osgi.util.promise.Promise;

public interface Arm extends Robot {

	List<JointDescription> getJoints();
	
	List<JointState> getState();

	
	Promise<Arm> setPosition(int joint, float position);
	
	Promise<Arm> setVelocity(int joint, float velocity);

	Promise<Arm> setTorque(int joint, float torque);
	
	
	Promise<Arm> setPositions(float... position);
	
	Promise<Arm> setVelocities(float... velocity);

	Promise<Arm> setTorques(float... torque);


	Promise<Arm> openGripper();
	
	Promise<Arm> openGripper(float opening);
	
	Promise<Arm> closeGripper();


	Promise<Arm> setPositions(Collection<JointValue> positions);
	
	Promise<Arm> setVelocities(Collection<JointValue> velocities);

	Promise<Arm> setTorques(Collection<JointValue> torques);
	
	
	Promise<Arm> reset();
	
	Promise<Arm> stop(int joint);
}
