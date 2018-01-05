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
public interface OmniDirectional extends Robot<OmniDirectional> {

	Promise<OmniDirectional> setVelocities(Collection<JointValue> velocities);
	
	Promise<OmniDirectional> move(float vx, float vy, float va);
	
}
