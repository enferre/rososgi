package be.iminds.iot.robot.api.omni;

import java.util.Collection;

import org.osgi.util.promise.Promise;

import be.iminds.iot.robot.api.JointValue;
import be.iminds.iot.robot.api.Robot;

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
