package be.iminds.iot.robot.api.rover;

import org.osgi.util.promise.Promise;

import be.iminds.iot.robot.api.Robot;

/**
 * API for controlling a rover-type robot similar to the ErleRover
 * 
 * @author tverbele
 *
 */
public interface Rover extends Robot {

	public Promise<Rover> move(float throttle, float yaw);
	
}
