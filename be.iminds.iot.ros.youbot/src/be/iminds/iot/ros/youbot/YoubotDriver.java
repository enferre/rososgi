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
package be.iminds.iot.ros.youbot;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;

import be.iminds.iot.ros.api.Ros;
import be.iminds.iot.ros.util.NativeRosNode;

@Component(immediate=true,
	name="be.iminds.iot.ros.youbot.Youbot",
	configurationPolicy=ConfigurationPolicy.REQUIRE)
public class YoubotDriver extends NativeRosNode {

	private Ros ros;
	
	public YoubotDriver(){
		super("youbot_driver_ros_interface","youbot_driver_ros_interface", 
				"base/joint_states:=/joint_states",
				"arm_1/joint_states:=/joint_states");
	}
	
	public void activate(Map<String, Object> properties) throws Exception {
		// for some reason youbot requires global parameters?!
		ros.setParameter("youBotHasBase", true);
		ros.setParameter("youBotHasArms", true);

		ros.setParameter("youBotBaseName", "youbot-base");
		ros.setParameter("youBotArmName1", "youbot-manipulator");
		
		super.activate(properties);
	}
	
	@Reference
	void setRos(Ros ros){
		this.ros = ros;
	}
}

