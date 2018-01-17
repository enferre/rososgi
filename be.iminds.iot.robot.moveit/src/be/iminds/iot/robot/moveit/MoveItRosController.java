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
package be.iminds.iot.robot.moveit;

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
	name="be.iminds.iot.robot.moveit.Arm",
	configurationPolicy=ConfigurationPolicy.REQUIRE)
public class MoveItRosController extends AbstractNodeMain {

	private String name;
	private MoveItArmImpl arm;
	
	private BundleContext context;
	
	@Activate
	void activate(BundleContext context, Map<String, Object> config){
		this.context = context;
		
		if(config.containsKey("name")) {
			name = config.get("name").toString();
		} else {
			name = "MoveItArm";
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

		// this brings online arm and base services
		arm = new MoveItArmImpl(name, context, connectedNode);
		
		String[] joints = new String[] {
				"panda_joint1",
				"panda_joint2",
				"panda_joint3",
				"panda_joint4",
				"panda_joint5",
				"panda_joint6",
				"panda_joint7"};
		
		arm.register("/panda/franka_gripper_node/gripper_action",
				"/panda/joint_states", joints,
				"/panda/move_group","panda_arm_hand",
				"/panda/compute_ik", "/panda/compute_fk");
	}
}
