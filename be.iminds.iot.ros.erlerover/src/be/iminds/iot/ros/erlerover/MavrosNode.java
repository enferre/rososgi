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
package be.iminds.iot.ros.erlerover;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;

import be.iminds.iot.ros.util.NativeRosNode;

/**
 * Start MAVROS node connecting to rover, requires APM service to be there
 * 
 * TODO seems not to work ... for now launch it ourselves outside of OSGi?!
 * TODO take a look how this (w/c)ould work on the real rover
 * 
 * @author tverbele
 *
 */
@Component(immediate=true,
		name="be.iminds.iot.ros.erlerover.Rover",
		configurationPolicy=ConfigurationPolicy.REQUIRE)
public class MavrosNode extends NativeRosNode {

	public MavrosNode(){
		super("mavros","mavros_node");
	}
	
	@Reference
	void setAPMService(APMService s){
		// Wait for APM service to be started
	}
}

