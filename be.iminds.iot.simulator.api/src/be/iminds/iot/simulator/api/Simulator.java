/*******************************************************************************
 *  ROSOSGi - Bridging the gap between Robot Operating System (ROS) and OSGi
 *  Copyright (C) 2015, 2016  imec - IDLab - UGent
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
package be.iminds.iot.simulator.api;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public interface Simulator {

	// start/stop/pause simulator
	
	void start(boolean sync);
	
	void pause();
	
	void stop();
	
	void tick() throws TimeoutException;
	
	// load scene
	default void loadScene(String file){
		loadScene(file, new HashMap<String, String>());
	}
	
	void loadScene(String file, Map<String, String> entities);
	
	// get and set positions of objects
	
	Position getPosition(String object);
	
	void setPosition(String object, Position p);
	
	Position getPosition(String object, String relativeTo);
	
	void setPosition(String object, String relativeTo, Position p);
	
	// get and set orientation of objects
	
	Orientation getOrientation(String object);
	
	void setOrientation(String object, Orientation o);
	
	Orientation getOrientation(String object, String relativeTo);
	
	void setOrientation(String object, String relativeTo, Orientation o);
	
	// check collisions
	boolean checkCollisions(String object);
	
	// allow to set environment properties - implementation depends on the simulator
	void setProperty(String object, String key, int value);
	
	void setProperty(String object, String key, float value);

	void setProperty(String object, String key, boolean value);

}
