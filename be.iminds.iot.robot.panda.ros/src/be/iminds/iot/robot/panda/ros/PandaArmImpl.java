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
package be.iminds.iot.robot.panda.ros;

import org.osgi.framework.BundleContext;
import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.Promise;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;

import be.iminds.iot.robot.api.arm.Arm;
import be.iminds.iot.robot.moveit.api.MoveItArmImpl;
import franka_control.ErrorRecoveryActionGoal;

public class PandaArmImpl extends MoveItArmImpl implements Arm {

	private Publisher<franka_control.ErrorRecoveryActionGoal> recovery;
	
	public PandaArmImpl(String name, 
			BundleContext context, 
			ConnectedNode node) {
		super(name, context, node);
	}

	public void register() {
		String[] joints = new String[] {
				"panda_joint1",
				"panda_joint2",
				"panda_joint3",
				"panda_joint4",
				"panda_joint5",
				"panda_joint6",
				"panda_joint7"};
		
		super.register("/panda/franka_gripper_node/gripper_action",
				"/panda/joint_states", joints,
				"/panda/move_group","panda_arm_hand",
				"/panda/compute_ik", "/panda/compute_fk", "panda_hand");
		
		recovery = node.newPublisher("/panda/error_recovery/goal", franka_control.ErrorRecoveryActionGoal._TYPE);
		
	}
	
	@Override
	public Promise<Arm> recover() {
		Deferred<Arm> deferred = new Deferred<>();
		
		ErrorRecoveryActionGoal msg = recovery.newMessage();
		recovery.publish(msg);
		
		deferred.resolve(this);
		return deferred.getPromise();
	}
}
