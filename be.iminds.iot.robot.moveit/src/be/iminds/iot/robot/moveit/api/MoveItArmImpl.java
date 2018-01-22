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
package be.iminds.iot.robot.moveit.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
import be.iminds.iot.robot.api.Orientation;
import be.iminds.iot.robot.api.Pose;
import be.iminds.iot.robot.api.Position;
import be.iminds.iot.robot.api.arm.Arm;
import control_msgs.GripperCommand;
import control_msgs.GripperCommandActionGoal;
import control_msgs.GripperCommandGoal;
import geometry_msgs.PoseStamped;
import moveit_msgs.Constraints;
import moveit_msgs.GetPositionFKRequest;
import moveit_msgs.GetPositionFKResponse;
import moveit_msgs.GetPositionIKRequest;
import moveit_msgs.GetPositionIKResponse;
import moveit_msgs.JointConstraint;
import moveit_msgs.MotionPlanRequest;
import moveit_msgs.MoveGroupActionGoal;
import moveit_msgs.MoveGroupGoal;
import moveit_msgs.PositionIKRequest;
import moveit_msgs.RobotState;

/**
 * Base Arm implementation using MoveIt. 
 * 
 * Is exported as it can be used in other bundles to extend 
 * further to implement vendor-specific functionality.
 */
public class MoveItArmImpl implements Arm {

	protected final String name;
	
	protected final BundleContext context;
	protected final List<ServiceRegistration<?>> registrations = new ArrayList<>();
	
	protected final ConnectedNode node;
	protected final MessageFactory factory;
	protected String compute_fk;
	protected String compute_ik;
	protected String ef_link;
	
	protected String move_group;
	protected Publisher<moveit_msgs.MoveGroupActionGoal> moveIt;
	protected Publisher<actionlib_msgs.GoalID> moveItCancel;
	protected Subscriber<moveit_msgs.MoveGroupActionResult> moveItResult; 

	protected Publisher<control_msgs.GripperCommandActionGoal> gripper;
	protected Subscriber<control_msgs.GripperCommandActionResult> gripperResult;

	protected Subscriber<sensor_msgs.JointState> subscriber; 
	protected List<JointState> state = new ArrayList<>();
	
	protected ServiceClient<moveit_msgs.GetPositionIKRequest, moveit_msgs.GetPositionIKResponse> ik;
	protected ServiceClient<moveit_msgs.GetPositionFKRequest, moveit_msgs.GetPositionFKResponse> fk;

	protected float speed = 1.0f;
	
	protected Map<UUID, Deferred<Arm>> inprogress = new ConcurrentHashMap<>();
	
	public MoveItArmImpl(String name, BundleContext context,
			ConnectedNode node){
		this.name = name;
		this.context = context;
		this.node = node;
		
		this.factory = node.getTopicMessageFactory();
	}
	
	public void register(String gripper_topic, 
			String joint_states_topic, String[] joints,
			String move_group_topic, String move_group,
			String compute_ik, String compute_fk, String ef_link){
		this.move_group = move_group;
		this.compute_ik = compute_ik;
		this.compute_fk = compute_fk;
		this.ef_link = ef_link;
		
		initMoveIt(move_group_topic);

		initGripper(gripper_topic);

		initStateListener(joint_states_topic, joints);
		
		loadIKService();
		loadFKService();
		
		Dictionary<String, Object> properties = new Hashtable<>();
		properties.put("name", name);

		ServiceRegistration<Arm> rArm = context.registerService(Arm.class, this, properties);
		registrations.add(rArm);
	}
	
	
	protected void initMoveIt(String move_group_topic) {
		// commands for plan / execute
		moveIt = node.newPublisher(move_group_topic+"/goal", moveit_msgs.MoveGroupActionGoal._TYPE);
		moveItCancel = node.newPublisher(move_group_topic+"/cancel", actionlib_msgs.GoalID._TYPE);
		
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
	}

	protected void initGripper(String gripper_topic) {
		// commands for gripper
		gripper = node.newPublisher(gripper_topic+"/goal", control_msgs.GripperCommandActionGoal._TYPE);
		
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
	}
	
	protected void initStateListener(String joint_states_topic, String[] joints) {
		// init joint states
		for(String j : joints) {
			JointState jointState = new JointState(j, 0, 0, 0);
			state.add(jointState);
		}
		
		// add subscribers
		subscriber = node.newSubscriber(joint_states_topic,
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
		
		req.setMaxAccelerationScalingFactor(speed);
		req.setMaxVelocityScalingFactor(speed);
		
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
		// move to x,y,z while keeping current orientation
		Pose pose = getPose();
		pose.position.x = x;
		pose.position.y = y;
		pose.position.z = z;
		return moveTo(pose);
	}
	
	@Override
	public Promise<Arm> moveTo(float x, float y, float z, float ox, float oy, float oz, float ow) {
		final Deferred<Arm> deferred = new Deferred<>();
		calculateIK(x, y, z, ox, oy, oz, ow).then(jointValues -> {deferred.resolveWith(setPositions(jointValues.getValue()));return null;},
				p -> deferred.fail(p.getFailure()));
		return deferred.getPromise();
	}

	@Override
	public Promise<Arm> moveTo(Pose p) {
		return moveTo(p.position.x, p.position.y, p.position.z, p.orientation.x, p.orientation.y, p.orientation.z, p.orientation.w);
	}

	protected JointState getJoint(String name) {
		for(JointState s : state) {
			if(s.joint.equals(name)) {
				return s;
			}
		}
		return null;
	}
	
	protected Promise<List<JointValue>> calculateIK(float x, float y, float z, float ox, float oy, float oz, float ow){
		if(ik == null) {
			loadIKService();
			if(ik == null) {
				throw new RuntimeException("IK service "+compute_ik+" not available");
			}
		}
		
		final Deferred<List<JointValue>> deferred = new Deferred<>();
		
		GetPositionIKRequest request = ik.newMessage();
		PositionIKRequest req = request.getIkRequest();
		
		// move group
		req.setGroupName(move_group);
		
		// link name
		req.setIkLinkName(ef_link);
		
		// set start state
		List<JointState> s = getState();
		RobotState startState = req.getRobotState();
		sensor_msgs.JointState jointState = startState.getJointState();
		jointState.setName(s.stream().map(js -> js.joint).collect(Collectors.toList()));
		jointState.setPosition(s.stream().mapToDouble(js -> (double) js.position).toArray());
		
		// set target pose
		PoseStamped spose = req.getPoseStamped();
		geometry_msgs.Pose p = spose.getPose();
		p.getPosition().setX(x);
		p.getPosition().setY(y);
		p.getPosition().setZ(z);
		p.getOrientation().setX(ox);
		p.getOrientation().setY(oy);
		p.getOrientation().setZ(oz);
		p.getOrientation().setW(ow);
		
		
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
	
	protected Promise<Pose> calculateFK(List<JointState> joints){
		if(fk == null) {
			loadFKService();
			if(fk == null) {
				throw new RuntimeException("FK service "+compute_fk+" not available");
			}
		}
		
		final Deferred<Pose> deferred = new Deferred<>();
		
		GetPositionFKRequest request = fk.newMessage();
		request.setFkLinkNames(Collections.singletonList(ef_link));
		RobotState state = request.getRobotState();
		sensor_msgs.JointState jointState = state.getJointState();
		jointState.setName(joints.stream().map(js -> js.joint).collect(Collectors.toList()));
		jointState.setPosition(joints.stream().mapToDouble(js -> (double) js.position).toArray());
		
		fk.call(request, new ServiceResponseListener<GetPositionFKResponse>() {
			
			@Override
			public void onSuccess(GetPositionFKResponse response) {
				if(response.getErrorCode().getVal() == 1) {
					// success
					PoseStamped p = response.getPoseStamped().get(0);
					
					Position pos = new Position(); 
					pos.x = (float) p.getPose().getPosition().getX();
					pos.y = (float) p.getPose().getPosition().getY();
					pos.z = (float) p.getPose().getPosition().getZ();

					Orientation o = new Orientation();
					o.x = (float) p.getPose().getOrientation().getX();
					o.y = (float) p.getPose().getOrientation().getY();
					o.z = (float) p.getPose().getOrientation().getZ();
					o.w = (float) p.getPose().getOrientation().getW();

					deferred.resolve(new Pose(pos, o));
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
	
	private void loadIKService() {
		try {
		     ik = node.newServiceClient(compute_ik, moveit_msgs.GetPositionIK._TYPE);
		} catch(Exception e){
			// do nothing ... moveTo method will just fail when no ik service present
		}
	}
	
	
	private void loadFKService() {
		try {
		     fk = node.newServiceClient(compute_fk, moveit_msgs.GetPositionFK._TYPE);
		} catch(Exception e){
			// do nothing ... getPose method will just fail when no fk service present
		}
	}

	@Override
	public Pose getPose() {
		try {
			Pose p = calculateFK(state).getValue();
			return p;
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public float getSpeed() {
		return speed;
	}

	@Override
	public void setSpeed(float speed) {
		this.speed = speed;
	}

	@Override
	public Object getProperty(String property) {
		return null;
	}

	@Override
	public void setProperty(String property, Object value) {
	}

	@Override
	public Promise<Arm> recover() {
		return stop();
	}

}
