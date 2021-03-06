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
package be.iminds.iot.ros.panda;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;

import be.iminds.iot.ros.util.NativeRosNode;

@Component(immediate=true,
	configurationPid="be.iminds.iot.ros.panda.Panda",
	configurationPolicy=ConfigurationPolicy.REQUIRE)
public class FrankaMoveIt extends NativeRosNode {

	public FrankaMoveIt(){
		super("panda_moveit_config",
			  "panda_moveit.launch", 
			  "arm_id:=panda",
			  "controller:=effort");
	}
	
	@Reference
	void setFrankaControl(FrankaControl control) {
		// depend on franka control to be launched first!
		try {
			Thread.sleep(1000);
		} catch(InterruptedException e) {
			
		}
	}
}

