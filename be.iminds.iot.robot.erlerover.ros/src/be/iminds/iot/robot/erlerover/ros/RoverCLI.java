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
package be.iminds.iot.robot.erlerover.ros;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import be.iminds.iot.robot.api.rover.Rover;

@Component(service={Object.class},
	property = {"osgi.command.scope=rover", 
	"osgi.command.function=move",
	"osgi.command.function=halt"})
public class RoverCLI {

	private Rover rover;
	
	public void move(float throttle, float yaw){
		rover.move(throttle, yaw);
	}
	
	public void halt(){
		rover.stop();
	}
	
	@Reference
	public void setRover(Rover r){
		this.rover = r;
	}
}
