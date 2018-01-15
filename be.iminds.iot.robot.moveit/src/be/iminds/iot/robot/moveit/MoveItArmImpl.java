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
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.Promise;
import org.ros.exception.RemoteException;
import org.ros.message.MessageFactory;
import org.ros.message.MessageListener;
import org.ros.node.ConnectedNode;
import org.ros.node.service.ServiceClient;
import org.ros.node.service.ServiceResponseListener;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;

import actionlib_msgs.GoalID;
import actionlib_msgs.GoalStatus;
import be.iminds.iot.robot.api.JointDescription;
import be.iminds.iot.robot.api.JointState;
import be.iminds.iot.robot.api.JointValue;
import be.iminds.iot.robot.api.arm.Arm;
import control_msgs.GripperCommand;
import control_msgs.GripperCommandActionGoal;
import control_msgs.GripperCommandGoal;
import geometry_msgs.Pose;
import geometry_msgs.PoseStamped;
import moveit_msgs.Constraints;
import moveit_msgs.GetPositionIKRequest;
import moveit_msgs.GetPositionIKResponse;
import moveit_msgs.JointConstraint;
import moveit_msgs.MotionPlanRequest;
import moveit_msgs.MoveGroupActionGoal;
import moveit_msgs.MoveGroupGoal;
import moveit_msgs.PositionIKRequest;
import moveit_msgs.RobotState;

public class MoveItArmImpl implements Arm {

	private final String name;
	
	private final BundleContext context;
	private final List<ServiceRegistration<?>> registrations = new ArrayList<>();
	
	private final ConnectedNode node;
	private final MessageFactory factory;
	
	private String move_group;
	private Publisher<moveit_msgs.MoveGroupActionGoal> moveIt;
	private Publisher<actionlib_msgs.GoalID> moveItCancel;
	private Subscriber<moveit_msgs.MoveGroupActionResult> moveItResult; 

	private Publisher<control_msgs.GripperCommandActionGoal> gripper;
	private Subscriber<control_msgs.GripperCommandActionResult> gripperResult;

	private Subscriber<sensor_msgs.JointState> subscriber; 
	private List<JointState> state = new ArrayList<>();
	
	private ServiceClient<moveit_msgs.GetPositionIKRequest, moveit_msgs.GetPositionIKResponse> ik;
	
	private Map<UUID, Deferred<Arm>> inprogress = new ConcurrentHashMap<>();
	
	public MoveItArmImpl(String name, BundleContext context,
			ConnectedNode node){
		this.name = name;
		this.context = context;
		this.node = node;
		
		this.factory = node.getTopicMessageFactory();
	}
	
	public void register(String joint_states, String move_group_topic, String move_group, String compute_ik, String gripper_topic, String[] joints){
		this.move_group = move_group;
		
		// commands for plan / execute
		moveIt = node.newPublisher(move_group_topic+"/goal", moveit_msgs.MoveGroupActionGoal._TYPE);
		moveItCancel = node.newPublisher(move_group_topic+"/cancel", actionlib_msgs.GoalID._TYPE);

		// commands for gripper
		gripper = node.newPublisher(gripper_topic+"/goal", control_msgs.GripperCommandActionGoal._TYPE);
		
		// init joint states
		for(String j : joints) {
			JointState jointState = new JointState(j, 0, 0, 0);
			state.add(jointState);
		}
		
		// add subscribers
		subscriber = node.newSubscriber(joint_states,
				sensor_msgs.JointState._TYPE);
		subscriber.addMessageListener(new MessageListener<sensor_msgs.JointState>() {
			@Override
			public void onNewMessage(sensor_msgs.JointState jointState) {
				// update JointImpl internal state1
				for(int i=0;i<jointState.getName().size();i++){
					String name = jointState.getName().get(i);
					JointState joint = getJoint(name);
					if(joint==null){
						// only capture state of configured joints!
						return;
					}
					
					joint.position = (float)jointState.getPosition()[i];
					if(jointState.getVelocity().length > i)
						joint.velocity = (float)jointState.getVelocity()[i];
					
					if(jointState.getEffort().length > i)
						joint.torque = (float)jointState.getEffort()[i];
				}
				
			}
		});

		moveItResult = node.newSubscriber(move_group_topic+"/result",
				moveit_msgs.MoveGroupActionResult._TYPE);
		moveItResult.addMessageListener(new MessageListener<moveit_msgs.MoveGroupActionResult>() {
			@Override
			public void onNewMessage(moveit_msgs.MoveGroupActionResult result) {
				GoalStatus status = result.getStatus();
				UUID id;
				try {
					id = UUID.fromString(result.getStatus().getGoalId().getId());
				} catch(IllegalArgumentException e) {
					// ignore if goal id is not a uuid 
					return;
				}
				
				
				Deferred<Arm> deferred = inprogress.remove(id);
				if(deferred == null) {
					System.out.println("WTF? No deferred for "+id);
					return;
				}
				
				if(status.getStatus() == 3) {
					// success
					deferred.resolve(MoveItArmImpl.this);
				} else {
					// fail promise?
					deferred.fail(new Exception(status.getText()));
				}
			}
		});

		gripperResult = node.newSubscriber(gripper_topic+"/result",
				control_msgs.GripperCommandActionResult._TYPE);
		gripperResult.addMessageListener(new MessageListener<control_msgs.GripperCommandActionResult>() {
			@Override
			public void onNewMessage(control_msgs.GripperCommandActionResult result) {
				GoalStatus status = result.getStatus();
				UUID id;
				try {
					id = UUID.fromString(result.getStatus().getGoalId().getId());
				} catch(IllegalArgumentException e) {
					// ignore if goal id is not a uuid 
					return;
				}
				
				Deferred<Arm> deferred = inprogress.remove(id);
				if(deferred == null) {
					System.out.println("WTF? No deferred for "+id);
					return;
				}
				
				if(status.getStatus() == 3) {
					// success
					deferred.resolve(MoveItArmImpl.this);
				} else {
					// fail promise?
					deferred.fail(new Exception(status.getText()));
				}
			}
		});
		
		try {
		     ik = node.newServiceClient(compute_ik, moveit_msgs.GetPositionIK._TYPE);
		} catch(Exception e){
			// do nothing ... moveTo method will just fail when no ik service present
		}
		
		Dictionary<String, Object> properties = new Hashtable<>();
		properties.put("name", name);

		ServiceRegistration<Arm> rArm = context.registerService(Arm.class, this, properties);
		registrations.add(rArm);
	}
	
	public void unregister(){
		for(ServiceRegistration<?> r : registrations){
			r.unregister();
		}
		registrations.clear();
		
		moveIt.shutdown();
		moveItCancel.shutdown();
		moveItResult.shutdown();
		gripper.shutdown();
		gripperResult.shutdown();
		subscriber.shutdown();
	}
	
	
	@Override
	public Promise<Arm> waitFor(long time) {
		throw new UnsupportedOperationException("waitFor not implemented...");
	}

	@Override
	public Promise<Arm> stop() {
		Deferred<Arm> deferred = new Deferred<>();
		if(!inprogress.isEmpty()) {
			inprogress.entrySet().forEach(e ->{
				GoalID gid = moveItCancel.newMessage();
				gid.setId(e.getKey().toString());
				deferred.resolveWith(e.getValue().getPromise());
				moveItCancel.publish(gid);
				
			});
		} else {
			// ok to resolve immediately here?
			deferred.resolve(this);
		}
		return deferred.getPromise();
	}

	@Override
	public List<JointDescription> getJoints() {
		// TODO get joint descriptions from configuration / urdf description?
		throw new UnsupportedOperationException("Joint descriptions unavailable...");
	}

	@Override
	public List<JointState> getState() {
		return state.stream()
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
		// TODO configure max opening?
		return openGripper(0.08f);
	}

	@Override
	public Promise<Arm> openGripper(float opening) {
		Deferred<Arm> deferred = new Deferred<>();
		
		GripperCommandActionGoal cmdMsg = gripper.newMessage();
		GripperCommandGoal goal = cmdMsg.getGoal();
		GripperCommand cmd = goal.getCommand();
		cmd.setPosition(opening);
		
		GoalID goalId = factory.newFromType(actionlib_msgs.GoalID._TYPE);
		UUID gid = UUID.randomUUID();
		inprogress.put(gid, deferred);
		goalId.setId(gid.toString());
		cmdMsg.setGoalId(goalId);
		
		gripper.publish(cmdMsg);
		
		return deferred.getPromise();
	}

	@Override
	public Promise<Arm> closeGripper() {
		return openGripper(0);
	}

	@Override
	public Promise<Arm> setPositions(Collection<JointValue> positions) {
		// TODO first cancel any currently running movement?

		// now give new instructions
		MoveGroupActionGoal goalMsg = moveIt.newMessage();
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
			if(getJoint(v.joint)!=null) { // only add joint constraint for configured joints!
				JointConstraint jc = factory.newFromType(moveit_msgs.JointConstraint._TYPE);
				jc.setJointName(v.joint);
				jc.setPosition(v.value);
				jc.setToleranceAbove(0.0001);
				jc.setToleranceBelow(0.0001);
				jc.setWeight(1.0);
				jointConstraints.add(jc);
			}
		}
		c.setJointConstraints(jointConstraints);
		constraints.add(c);
		req.setGoalConstraints(constraints);
		req.setGroupName(move_group);
		
		// TODO control velocity / acceleration
		req.setMaxAccelerationScalingFactor(1.0);
		req.setMaxVelocityScalingFactor(1.0);
		
		goal.setRequest(req);
		
		GoalID goalId = factory.newFromType(actionlib_msgs.GoalID._TYPE);
		UUID gid = UUID.randomUUID();
		goalId.setId(gid.toString());
		goalMsg.setGoalId(goalId);
		
		Deferred<Arm> deferred = new Deferred<>();
		inprogress.put(gid, deferred);
		
		moveIt.publish(goalMsg);
		
		return deferred.getPromise();
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
		throw new UnsupportedOperationException("Reset not supported via MoveIt...");
	}

	@Override
	public Promise<Arm> stop(int joint) {
		throw new UnsupportedOperationException("Stop per joint not supported via MoveIt...");
	}

	@Override
	public Promise<Arm> moveTo(float x, float y, float z) {
		final Deferred<Arm> deferred = new Deferred<>();
		calculateIK(x, y, z).then(jointValues -> {deferred.resolveWith(setPositions(jointValues.getValue()));return null;},
				p -> deferred.fail(p.getFailure()));
		return deferred.getPromise();
	}

	private JointState getJoint(String name) {
		for(JointState s : state) {
			if(s.joint.equals(name)) {
				return s;
			}
		}
		return null;
	}
	
	private Promise<List<JointValue>> calculateIK(float x, float y, float z){
		final Deferred<List<JointValue>> deferred = new Deferred<>();
		
		GetPositionIKRequest request = ik.newMessage();
		PositionIKRequest req = request.getIkRequest();
		
		// move group
		req.setGroupName(move_group);
		
		// set start state
		List<JointState> s = getState();
		RobotState startState = req.getRobotState();
		sensor_msgs.JointState jointState = startState.getJointState();
		jointState.setName(s.stream().map(js -> js.joint).collect(Collectors.toList()));
		jointState.setPosition(s.stream().mapToDouble(js -> (double) js.position).toArray());
		
		// set target pose
		PoseStamped spose = req.getPoseStamped();
		Pose p = spose.getPose();
		p.getPosition().setX(x);
		p.getPosition().setY(y);
		p.getPosition().setZ(z);
		p.getOrientation().setX(1.0);
		p.getOrientation().setY(0.0);
		p.getOrientation().setZ(0.0);
		p.getOrientation().setW(0.0);
		
		
		ik.call(request, new ServiceResponseListener<GetPositionIKResponse>() {
			
			@Override
			public void onSuccess(GetPositionIKResponse response) {
				if(response.getErrorCode().getVal() == 1) {
					// success
					List<JointValue> values = new ArrayList<>();
					sensor_msgs.JointState s = response.getSolution().getJointState();
					for(int i=0;i<s.getPosition().length;i++) {
						values.add(new JointValue(s.getName().get(i), JointValue.Type.POSITION, (float)s.getPosition()[i]));
					}
					deferred.resolve(values);
				} else {
					// failed?
					deferred.fail(new Exception("Failed to calculate IK solution"));
				}
			}
			
			@Override
			public void onFailure(RemoteException ex) {
				deferred.fail(ex);
			}
		});
		return deferred.getPromise();
	}
}
