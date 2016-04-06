package be.iminds.iot.robot.api;

import java.util.Collection;

import org.osgi.util.promise.Promise;

/**
 * API for controlling an omnidirectional robot
 * 
 * @author tverbele
 *
 */
public interface OmniDirectional extends Robot {

	Promise<OmniDirectional> setVelocities(Collection<JointValue> velocities);
	
	Promise<OmniDirectional> move(float vx, float vy, float va);
	
}
