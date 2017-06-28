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
package be.iminds.iot.robot.youbot.ros;

import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeMain;

@Component(service = {NodeMain.class},
	name="be.iminds.iot.robot.youbot.ros.Youbot",
	configurationPolicy=ConfigurationPolicy.REQUIRE)
public class YoubotRosController extends AbstractNodeMain {

	private String name;
	private ArmImpl arm;
	private BaseImpl base;
	
	private BundleContext context;
	
	@Activate
	void activate(BundleContext context, Map<String, Object> config){
		this.context = context;
		
		name = config.get("name").toString();
		if(name == null){
			name = "Youbot";
		}
	}
	
	@Deactivate
	void deactivate(){
		try {
			base.stop();
			arm.reset();
		} catch(Exception e){}
		
		arm.unregister();
		base.unregister();
	}
	
	@Override
	public GraphName getDefaultNodeName() {
		return GraphName.of("youbot/controller");
	}
	
	@Override
	public void onStart(ConnectedNode connectedNode){
		connectedNode.getTopicMessageFactory();

		// this brings online arm and base services
		arm = new ArmImpl(name, context, connectedNode);
		arm.register();
		
		base = new BaseImpl(name, context, connectedNode);
		base.register();
	}
}
