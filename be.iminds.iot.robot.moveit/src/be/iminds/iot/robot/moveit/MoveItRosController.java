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
	private String gripper_topic;
	private String joint_states_topic; 
	private String[] joints;
	private String move_group_topic; 
	private String move_group;
	private String compute_ik; 
	private String compute_fk; 
	private String fk_link;
	
	private MoveItArmImpl arm;
	
	private BundleContext context;
	
	@Activate
	void activate(BundleContext context, Map<String, Object> config){
		this.context = context;
		
		if(config.containsKey("name")) {
			name = config.get("name").toString();
		} else {
			name = "arm";
		}
		
		if(config.containsKey("gripper_topic")) {
			gripper_topic = config.get("gripper_topic").toString();
		}
		
		if(config.containsKey("joint_states_topic")) {
			joint_states_topic = config.get("joint_states_topic").toString();
		}
		
		if(config.containsKey("joints")) {
			joints = config.get("joints").toString().split(",");
		}
		
		if(config.containsKey("move_group_topic")) {
			move_group_topic = config.get("move_group_topic").toString();
		}
		
		if(config.containsKey("move_group")) {
			move_group = config.get("move_group").toString();
		}
		
		if(config.containsKey("compute_ik")) {
			compute_ik = config.get("compute_ik").toString();
		}
		
		if(config.containsKey("compute_fk")) {
			compute_fk = config.get("compute_fk").toString();
		}
		
		if(config.containsKey("fk_link")) {
			fk_link = config.get("fk_link").toString();
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
		arm.register(gripper_topic, joint_states_topic, joints, move_group_topic, move_group, compute_ik, compute_fk, fk_link);
	}
}
