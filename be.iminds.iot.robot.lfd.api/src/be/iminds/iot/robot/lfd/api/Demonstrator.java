/*******************************************************************************
 *  ROSOSGi - Bridging the gap between Robot Operating System (ROS) and OSGi
 *  Copyright (C) 2015, 2017  imec - IDLab - UGent
 *
 *  This file is part of DIANNE  -  Framework for distributed artificial neural networks
 *
 *  DIANNE is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *   
 *  Contributors:
 *      Tim Verbelen, Steven Bohez
 *******************************************************************************/
package be.iminds.iot.robot.lfd.api;

import java.util.List;
import java.util.UUID;

import org.osgi.util.promise.Promise;

/**
 */
public interface Demonstrator {
	
	/**
	 * List all available demonstrations.
	 * @return list of demonstration names
	 */
	List<String> demonstrations();
	
	/**
	 * Load a demonstration.
	 * @param name demonstration name
	 * @return demonstration
	 */
	Demonstration load(String name);
	
	/**
	 * Record a step for a certain demonstration. This will record any sensor value
	 * together with the manipulator joint and cartesian poses if available.
	 * @param demonstration demonstration to add the step to
	 * @param type type of the step
	 * @return recorded step
	 */
	Step step(String demonstration, Step.Type type);
	
	/**
	 * Save changes to the demonstration. This will write steps to a .csv file on disk.
	 * @param d demonstration to save
	 */
	void save(Demonstration d);
	
	
	/**
	 * Execute a single step.
	 * @param step step to execute
	 * @return a promise that is resolved when the execution is finished.
	 */
	default Promise<Void> execute(Step step){
		return execute(step, false);
	}
	
	
	/**
	 * Execute a single step.
	 * @param step step to execute
	 * @param reversed if true the demonstration is executed reversed, meaning the steps are iterated in reversed
	 * order and also opening and closing the gripper is reversed
	 * @return a promise that is resolved when the execution is finished.
	 */
	Promise<Void> execute(Step step, boolean reversed);

	
	/**
	 * Execute a demonstration.
	 * @param d demonstration to execute
	 * @return a promise that is resolved when the execution is finished.
	 */
	default Promise<Void> execute(Demonstration d){
		return execute(d, false);
	}
	
	/**
	 * Execute a demonstration.
	 * @param d demonstration to execute
	 * @param reversed if true the demonstration is executed reversed, meaning the steps are iterated in reversed
	 * order and also opening and closing the gripper is reversed
	 * @return a promise that is resolved when the execution is finished.
	 */
	Promise<Void> execute(Demonstration d, boolean reversed);
	

	/**
	 * Execute a demonstration for a number of times. Steps before a START step will only be executed in the first iteration.
	 * @param d demonstration to execute
	 * @param times number of times to repeat the demonstration, a negative integer means loop forever until interrupted.
	 * @param reverse if true the demonstration is first executed reversed before repeating again
	 * @return a promise that is resolved when the execution is finished.
	 */
	Promise<Void> repeat(Demonstration d, int times, boolean reverse);
	
	
	/**
	 * Execute a demonstration and record it.
	 * @param d demonstration to execute
	 * @param reversed if true the demonstration is executed reversed, meaning the steps are iterated in reversed
	 * @return a promise resolved with the resulting recording
	 */
	Promise<Recording> executeAndRecord(Demonstration d, boolean reversed);
	
	
	/**
	 * Repeat a demonstration and record each execution. Steps before a START step will only be executed in the first iteration and are not recorded.
	 * @param d demonstration to repeat
	 * @param times number of times to repeat
	 * @param reverse also do the reverse operation before executing again
	 * @return a promise resolved with the resulting recordings
	 */
	Promise<List<Recording>> repeatAndRecord(Demonstration d, int times, boolean reverse);
	
	
	// introduce cancelable promises?!
	/**
	 * Interrupt any running execution.
	 */
	void interrupt();
	
	/**
	 * Recover from errors in case an execution failed with an error.
	 */
	void recover();

	/**
	 * Set the robot to guide mode to allow the operator to demonstrate.
	 * @param guide
	 */
	void guide(boolean guide);
	

	/**
	 * Start recording a session at a given desired rate. The recording is written to file.
	 * @param rate rate (in Hz) that we should record all sensors / states (best effort)
	 * @return UUID under which this recording will be available
	 */
	UUID record(int rate);
	
	/**
	 * Stop a recording session
	 * @param id of the recording that was returned by the record method
	 * @return recording
	 */
	Recording stop(UUID id);
	
	/**
	 * Stop all recordings
	 */
	List<Recording> stop();
}
