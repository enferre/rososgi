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
package be.iminds.iot.robot.moveit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.promise.Promise;
import org.ros.message.MessageFactory;
import org.ros.message.MessageListener;
import org.ros.node.ConnectedNode;
import org.ros.node.service.ServiceClient;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;

import actionlib_msgs.GoalID;
import actionlib_msgs.GoalStatus;
import be.iminds.iot.robot.api.JointDescription;
import be.iminds.iot.robot.api.JointState;
import be.iminds.iot.robot.api.JointValue;
import be.iminds.iot.robot.api.arm.Arm;
import moveit_msgs.Constraints;
import moveit_msgs.ExecuteTrajectoryGoal;
import moveit_msgs.JointConstraint;
import moveit_msgs.MotionPlanRequest;
import moveit_msgs.MoveGroupActionGoal;
import moveit_msgs.MoveGroupGoal;
import moveit_msgs.MoveItErrorCodes;
import moveit_msgs.RobotState;
import moveit_msgs.RobotTrajectory;

public class MoveItArmImpl implements Arm {

	private final String name;
	
	private final BundleContext context;
	private final List<ServiceRegistration<?>> registrations = new ArrayList<>();
	
	
	private final ConnectedNode node;
	private final MessageFactory factory;
	private Publisher<moveit_msgs.MoveGroupActionGoal> plan;
	private Publisher<moveit_msgs.ExecuteTrajectoryGoal> execute;

	// TODO add cancel?
	private Subscriber<moveit_msgs.MoveGroupActionResult> planResult; 
	private Subscriber<moveit_msgs.ExecuteTrajectoryResult> executeResult; 

	private Subscriber<sensor_msgs.JointState> subscriber; 
	private Map<String, JointState> state = new HashMap<>();
	
	private ServiceClient<moveit_msgs.GetPositionIKRequest, moveit_msgs.GetPositionIKResponse> ik;
	
	public MoveItArmImpl(String name, BundleContext context,
			ConnectedNode node){
		this.name = name;
		this.context = context;
		this.node = node;
		
		this.factory = node.getTopicMessageFactory();
	}
	
	public void register(String joint_states, String move_group, String execute_trajectory, String compute_ik){
		// commands for plan / execute
		this.plan = node.newPublisher(move_group+"/goal", moveit_msgs.MoveGroupActionGoal._TYPE);
		this.execute = node.newPublisher(execute_trajectory+"/goal", moveit_msgs.ExecuteTrajectoryResult._TYPE);
		
		
		// add subscribers
		subscriber = node.newSubscriber(joint_states,
				sensor_msgs.JointState._TYPE);
		subscriber.addMessageListener(new MessageListener<sensor_msgs.JointState>() {
			@Override
			public void onNewMessage(sensor_msgs.JointState jointState) {
				// update JointImpl internal state1
				for(int i=0;i<jointState.getName().size();i++){
					String name = jointState.getName().get(i);
					JointState joint = state.get(name);
					if(joint==null){
						state.put(name, new JointState(name, 0, 0, 0));
					}
					
					joint.position = (float)jointState.getPosition()[i];
					joint.velocity = (float)jointState.getVelocity()[i];
					if(jointState.getEffort().length > i)
						joint.torque = (float)jointState.getEffort()[i];
				}
				
			}
		});

		planResult = node.newSubscriber(move_group+"/result",
				moveit_msgs.MoveGroupActionResult._TYPE);
		planResult.addMessageListener(new MessageListener<moveit_msgs.MoveGroupActionResult>() {
			@Override
			public void onNewMessage(moveit_msgs.MoveGroupActionResult result) {
				GoalStatus status = result.getStatus();
				
				if(status.getStatus() == 3) {
					// success
					RobotTrajectory trajectory = result.getResult().getPlannedTrajectory();
				
					ExecuteTrajectoryGoal goal = execute.newMessage();
					goal.setTrajectory(trajectory);
					execute.publish(goal);
				} else {
					// TODO fail promise?
				}
			}
		});

		executeResult = node.newSubscriber(execute_trajectory+"/result",
				moveit_msgs.ExecuteTrajectoryResult._TYPE);
		executeResult.addMessageListener(new MessageListener<moveit_msgs.ExecuteTrajectoryResult>() {
			@Override
			public void onNewMessage(moveit_msgs.ExecuteTrajectoryResult result) {
				MoveItErrorCodes errcode = result.getErrorCode();
				if(errcode.getVal() == 1) {
					// success, resolve promise
				} else {
					// fail promise
				}
				
			}
		});
		
		try {
		     ik = node.newServiceClient(compute_ik, moveit_msgs.GetPositionIK._TYPE);
		} catch(Exception e){
			// do nothing ... moveTo method will just fail when no ik service present
		}
		
//		// register OSGi services
//		for(Joint joint : joints){
//			Dictionary<String, Object> properties = new Hashtable<>();
//			properties.put("joint.name", joint.getName());
//			ServiceRegistration<Joint> rJoint = context.registerService(Joint.class, joint, properties);
//			registrations.add(rJoint);
//		}	
//		
		Dictionary<String, Object> properties = new Hashtable<>();
		properties.put("name", name);
//		ServiceRegistration<Gripper> rGripper = context.registerService(Gripper.class, gripper, properties);
//		registrations.add(rGripper);
//		
		ServiceRegistration<Arm> rArm = context.registerService(Arm.class, this, properties);
		registrations.add(rArm);
	}
	
	public void unregister(){
		for(ServiceRegistration<?> r : registrations){
			r.unregister();
		}
		registrations.clear();
		
		plan.shutdown();
		execute.shutdown();
		planResult.shutdown();
		executeResult.shutdown();
		subscriber.shutdown();
	}
	
	
	@Override
	public Promise<Arm> waitFor(long time) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Promise<Arm> stop() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<JointDescription> getJoints() {
		// TODO get joint descriptions from configuration / urdf description?
		throw new UnsupportedOperationException("Joint descriptions unavailable...");
	}

	@Override
	public List<JointState> getState() {
		return state.values().stream()
				.map(s -> new JointState(s.joint, s.position, s.velocity, s.torque))
				.collect(Collectors.toList());
	}

	@Override
	public Promise<Arm> setPosition(int joint, float position) {
		List<JointValue> cmd = getState().stream()
				.map(s -> new JointValue(s.joint, JointValue.Type.POSITION, s.position))
				.collect(Collectors.toList());
		cmd.get(joint).value = position;
		return setPositions(cmd);
	}

	@Override
	public Promise<Arm> setVelocity(int joint, float velocity) {
		throw new UnsupportedOperationException("Joint velocity control not supported via MoveIt...");
	}

	@Override
	public Promise<Arm> setTorque(int joint, float torque) {
		throw new UnsupportedOperationException("Joint torque control not supported via MoveIt...");
	}

	@Override
	public Promise<Arm> setPositions(float... position) {
		List<JointValue> cmd = getState().stream()
				.map(s -> new JointValue(s.joint, JointValue.Type.POSITION, s.position))
				.collect(Collectors.toList());
		for(int i=0;i<position.length;i++) {
			if(cmd.size() > i) {
				cmd.get(i).value = position[i];
			}
		}
		return setPositions(cmd);
	}

	@Override
	public Promise<Arm> setVelocities(float... velocity) {
		throw new UnsupportedOperationException("Joint velocity control not supported via MoveIt...");
	}

	@Override
	public Promise<Arm> setTorques(float... torque) {
		throw new UnsupportedOperationException("Joint torque control not supported via MoveIt...");
	}

	@Override
	public Promise<Arm> openGripper() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Promise<Arm> openGripper(float opening) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Promise<Arm> closeGripper() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Promise<Arm> setPositions(Collection<JointValue> positions) {
		MoveGroupActionGoal goalMsg = plan.newMessage();
		MoveGroupGoal goal = goalMsg.getGoal();
		MotionPlanRequest req = goal.getRequest();
		
		// set start state
		List<JointState> s = getState();
		RobotState startState = req.getStartState();
		sensor_msgs.JointState jointState = startState.getJointState();
		jointState.setName(s.stream().map(js -> js.joint).collect(Collectors.toList()));
		jointState.setPosition(s.stream().mapToDouble(js -> (double) js.position).toArray());
		
		// set goal state
		List<Constraints> constraints = new ArrayList<>();
		Constraints c = factory.newFromType(moveit_msgs.Constraints._TYPE);
		c.setName("target positions");
		List<JointConstraint> jointConstraints = new ArrayList<>();
		for(JointValue v : positions) {
			JointConstraint jc = factory.newFromType(moveit_msgs.JointConstraint._TYPE);
			jc.setJointName(v.joint);
			jc.setPosition(v.value);
			jc.setToleranceAbove(0.0001);
			jc.setToleranceBelow(0.0001);
			jc.setWeight(1.0);
			jointConstraints.add(jc);
		}
		c.setJointConstraints(jointConstraints);
		constraints.add(c);
		req.setGoalConstraints(constraints);
		
		// TODO make configurable
		req.setGroupName("panda_arm_hand");
		
		// TODO control velocity / acceleration
		req.setMaxAccelerationScalingFactor(1.0);
		req.setMaxVelocityScalingFactor(1.0);
		
		goal.setRequest(req);
		
		GoalID goalId = factory.newFromType(actionlib_msgs.GoalID._TYPE);
		goalId.setId(UUID.randomUUID().toString());
		goalMsg.setGoalId(goalId);
		
		plan.publish(goalMsg);
		
		return null;
	}

	@Override
	public Promise<Arm> setVelocities(Collection<JointValue> velocities) {
		throw new UnsupportedOperationException("Joint velocity control not supported via MoveIt...");
	}

	@Override
	public Promise<Arm> setTorques(Collection<JointValue> torques) {
		throw new UnsupportedOperationException("Joint torque control not supported via MoveIt...");
	}

	@Override
	public Promise<Arm> reset() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Promise<Arm> stop(int joint) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Promise<Arm> moveTo(float x, float y, float z) {
		// TODO Auto-generated method stub
		return null;
	}


}
