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
package be.iminds.iot.robot.panda.ros;

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
	name="be.iminds.iot.robot.panda.ros.Panda",
	configurationPolicy=ConfigurationPolicy.REQUIRE)
public class PandaRosController extends AbstractNodeMain {

	private String name;
	
	private PandaArmImpl arm;
	
	private BundleContext context;
	
	@Activate
	void activate(BundleContext context, Map<String, Object> config){
		this.context = context;
		
		if(config.containsKey("name")) {
			name = config.get("name").toString();
		} else {
			name = "panda";
		}
	}
	
	@Deactivate
	void deactivate(){
		try {
			arm.reset();
		} catch(Exception e){}
		
		arm.unregister();
	}
	
	@Override
	public GraphName getDefaultNodeName() {
		return GraphName.of(name.toLowerCase()+"/controller");
	}
	
	@Override
	public void onStart(ConnectedNode connectedNode){
		connectedNode.getTopicMessageFactory();
		arm = new PandaArmImpl(name, context, connectedNode);
		arm.register();
	}
}
