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
import org.ros.message.MessageListener;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;

import be.iminds.iot.robot.api.Orientation;
import be.iminds.iot.robot.api.Pose;
import be.iminds.iot.robot.api.Position;
import be.iminds.iot.robot.api.arm.Arm;
import be.iminds.iot.robot.moveit.api.MoveItArmImpl;
import franka_control.ErrorRecoveryActionGoal;

public class PandaArmImpl extends MoveItArmImpl implements Arm {

	private Publisher<franka_control.ErrorRecoveryActionGoal> recovery;
	private Subscriber<franka_msgs.FrankaState> frankaState; 

	
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
				"/panda/move_group","panda_arm_EE",
				"/panda/compute_ik", "/panda/compute_fk", "panda_EE");
		
		recovery = node.newPublisher("/panda/error_recovery/goal", franka_control.ErrorRecoveryActionGoal._TYPE);

		frankaState = node.newSubscriber("/panda/franka_state_controller/franka_states", franka_msgs.FrankaState._TYPE);
		frankaState.addMessageListener(new MessageListener<franka_msgs.FrankaState>() {
			@Override
			public void onNewMessage(franka_msgs.FrankaState state) {
				double[] otee = state.getOTEE();
				
				float[] m = new float[9];
				for(int i=0;i<3;i++) {
					for(int j=0;j<3;j++) {
						m[i*3+j] = (float)otee[j*4 + i];
					}
				}
				
				Position p = new Position((float)otee[12], (float)otee[13], (float)otee[14]);
				Orientation o = new Orientation(m);
				Pose pose = new Pose(p, o);
			}
		});
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
