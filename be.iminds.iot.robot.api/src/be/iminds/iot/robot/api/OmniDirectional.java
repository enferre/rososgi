package be.iminds.iot.robot.api;

import java.util.Collection;

import org.osgi.util.promise.Promise;

/**
 * API for controlling an omnidirectional robot
 * 
 * @author tverbele
 *
 */
public interface OmniDirectional {

	Promise<Void> setVelocities(Collection<JointValue> velocities);
	
	Promise<Void> move(float vx, float vy, float va);
	
	Promise<Void> waitFor(long time);

	Promise<Void> stop();
	
}
