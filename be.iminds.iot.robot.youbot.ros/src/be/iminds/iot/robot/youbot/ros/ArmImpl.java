package be.iminds.iot.robot.youbot.ros;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.osgi.framework.BundleContext;
import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.Promise;
import org.ros.message.MessageFactory;
import org.ros.message.MessageListener;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;

import be.iminds.iot.robot.api.Arm;
import be.iminds.iot.robot.api.Gripper;
import be.iminds.iot.robot.api.Joint;
import be.iminds.iot.robot.api.JointDescription;
import be.iminds.iot.robot.api.JointState;
import be.iminds.iot.robot.api.JointValue;
import be.iminds.iot.robot.api.JointValue.Type;

public class ArmImpl implements Arm {

	private final List<JointImpl> joints;
	private final Gripper gripper;
	
	private final MessageFactory factory;
	private final Publisher<brics_actuator.JointPositions> pPos;
	private final Publisher<brics_actuator.JointVelocities> pVel;
	private final Publisher<brics_actuator.JointTorques> pTorq;
	private final Publisher<brics_actuator.JointPositions> pGrip;

	private final Map<Target, Deferred<Arm>> targets = new ConcurrentHashMap<>();
	
	private final Timer timer = new Timer();
	
	// TODO get from config? also look at be.iminds.iot.ros.simulator.vrep.youbot.Youbot
	private String[] config = new String[]{
			"arm_joint_1",
			"arm_joint_2",
			"arm_joint_3",
			"arm_joint_4",
			"arm_joint_5",
			"gripper_finger_joint_l",
			"gripper_finger_joint_r"
	};
	
	private float[] positionMin = new float[]{
		0.0100693f, 
		0.0100693f, 
		-5.02655f,
		0.0221239f, 
		0.11062f, 
		0f,      
		0f	
	};
	
	private float[] positionMax = new float[]{
		5.84014f,
		2.61799f,
		-0.015708f, 
		3.4292f, 
		5.64159f, 
		0.0114f, 
		0.0114f	
	};
	
	public ArmImpl(BundleContext context,
			ConnectedNode node){
	
		this.factory = node.getTopicMessageFactory();
		// commands for arm joints
		this.pPos = node.newPublisher("/arm_1/arm_controller/position_command", brics_actuator.JointPositions._TYPE);
		this.pVel = node.newPublisher("/arm_1/arm_controller/velocity_command", brics_actuator.JointVelocities._TYPE);;
		// TODO torque_command is not supported atm
		this.pTorq = node.newPublisher("/arm_1/arm_controller/torque_command", brics_actuator.JointTorques._TYPE);;
		
		// commands for gripper joints
		this.pGrip = node.newPublisher("/arm_1/gripper_controller/position_command", brics_actuator.JointPositions._TYPE);
		
		// joints
		joints = new ArrayList<>();
		for(int i=0;i<config.length;i++){
			String name = config[i];
			JointDescription d = new JointDescription(name,
					positionMin[i], positionMax[i], 
					0.0f, 1.5f, 0.0f, 1.0f); // TODO what are min and max velocities/torques?
			JointImpl joint = new JointImpl(d, this);
			joints.add(joint);
		}
		
		// gripper
		gripper = new Gripper() {
			
			@Override
			public void open(float opening) {
				openGripper(opening);
			}
			
			@Override
			public void close() {
				closeGripper();
			}
		};
		
		
		// add subscriber
		Subscriber<sensor_msgs.JointState> subscriber = node.newSubscriber("/joint_states",
				sensor_msgs.JointState._TYPE);
		subscriber.addMessageListener(new MessageListener<sensor_msgs.JointState>() {
			@Override
			public void onNewMessage(sensor_msgs.JointState jointState) {
				// update JointImpl internal state
				for(int i=0;i<jointState.getName().size();i++){
					String name = jointState.getName().get(i);
					JointImpl joint = getJoint(name);
					if(joint==null){
						continue;
					}
					
					joint.position = (float)jointState.getPosition()[i];
					joint.velocity = (float)jointState.getVelocity()[i];
// torque not supported	atm				
//					joint.torque = (float)jointState.getEffort()[i];
				}
				
				// check if target is resolved 
				Iterator<Entry<Target, Deferred<Arm>>> it = targets.entrySet().iterator();
				while(it.hasNext()){
					Entry<Target, Deferred<Arm>> e = it.next();
					Target target = e.getKey();
					if(target.isResolved()){
						Deferred<Arm> d = e.getValue();
						if(d!=null){
							try {
								d.resolve(ArmImpl.this);
							} catch(IllegalStateException ex){}
						}
						it.remove();
					}
				}
			}
		});
		
		// register OSGi services
		for(Joint joint : joints){
			Dictionary<String, Object> properties = new Hashtable<>();
			properties.put("joint.name", joint.getName());
			context.registerService(Joint.class, joint, properties);
		}
		
		context.registerService(Gripper.class, gripper, null);
		context.registerService(Arm.class, this, null);
	}
	
	@Override
	public List<JointDescription> getJoints() {
		return joints.stream().map(j -> j.getDescription()).collect(Collectors.toList());
	}

	@Override
	public List<JointState> getState() {
		return joints.stream().map(j -> j.getState()).collect(Collectors.toList());
	}

	@Override
	public Promise<Arm> setPosition(int joint, float position){
		return setPositions(Collections.singleton(new JointValue(config[joint], Type.POSITION, position)));
	}
	
	@Override
	public Promise<Arm> setVelocity(int joint, float velocity){
		return setVelocities(Collections.singleton(new JointValue(config[joint], Type.VELOCITY, velocity)));
	}

	@Override
	public Promise<Arm> setTorque(int joint, float torque){
		return setTorques(Collections.singleton(new JointValue(config[joint], Type.TORQUE, torque)));
	}
	
	@Override
	public Promise<Arm> setPositions(Collection<JointValue> positions) {
		// check for collision with operations in progress
		for(JointValue v : positions){
			Iterator<Entry<Target, Deferred<Arm>>> it = targets.entrySet().iterator();
			while(it.hasNext()){
				Entry<Target, Deferred<Arm>> target = it.next();
				for(JointValue joint : target.getKey().target){
					if(joint.joint.equals(v.joint)){
						Deferred<Arm> deferred = target.getValue();
						try {
							deferred.fail(new Exception("Operation interrupted!"));
						} catch(IllegalStateException ex){}
						it.remove();
					}
				}
			}
		}
		Deferred<Arm> deferred = new Deferred<Arm>();
		Target target = new Target(positions);
		targets.put(target, deferred);

		boolean arm = false;
		boolean gripper = false;
		brics_actuator.JointPositions msg = pPos.newMessage();
		List<brics_actuator.JointValue> pp = new ArrayList<>();
		for(JointValue position : positions){
			brics_actuator.JointValue pos = factory.newFromType(brics_actuator.JointValue._TYPE);
			
			if(position.joint.startsWith("arm")){
				arm = true;
				pos.setUnit("rad");
			}
			if(position.joint.startsWith("gripper")){
				gripper = true;
				pos.setUnit("m");
			}
			
			pos.setJointUri(position.joint);
			
			JointDescription d = getJoint(position.joint).getDescription();
			position.value = clamp(position.value, d.positionMin, d.positionMax);
			pos.setValue(position.value);
			pp.add(pos);
		}
		msg.setPositions(pp);
		
		if(arm)
			pPos.publish(msg);
		if(gripper)
			pGrip.publish(msg);
		
		
		return deferred.getPromise();
	}

	@Override
	public Promise<Arm> setVelocities(Collection<JointValue> velocities) {
		// check for collision with operations in progress
		for(JointValue v : velocities){
			Iterator<Entry<Target, Deferred<Arm>>> it = targets.entrySet().iterator();
			while(it.hasNext()){
				Entry<Target, Deferred<Arm>> target = it.next();
				for(JointValue joint : target.getKey().target){
					if(joint.joint.equals(v.joint)){
						Deferred<Arm> deferred = target.getValue();
						try {
							deferred.fail(new Exception("Operation interrupted!"));
						} catch(IllegalStateException ex){}
						it.remove();
					}
				}
			}
		}
		Deferred<Arm> deferred = new Deferred<Arm>();
		Target target = new Target(velocities);
		targets.put(target, deferred);

		brics_actuator.JointVelocities msg = pVel.newMessage();
		List<brics_actuator.JointValue> vv = new ArrayList<>();
		for(JointValue velocity : velocities){
			brics_actuator.JointValue vel = factory.newFromType(brics_actuator.JointValue._TYPE);
			vel.setJointUri(velocity.joint);
			vel.setUnit("rad/s");
			
			JointDescription d = getJoint(velocity.joint).getDescription();
			velocity.value = clamp(velocity.value, d.positionMin, d.positionMax);
			vel.setValue(velocity.value);
			vv.add(vel);
		}
		msg.setVelocities(vv);
		pVel.publish(msg);
		
		return deferred.getPromise();
	}

	@Override
	public Promise<Arm> setTorques(Collection<JointValue> torques) {
		throw new UnsupportedOperationException();
		
		// check for collision with operations in progress
//		for(JointValue v : torques){
//			Iterator<Entry<Target, Deferred<Arm>>> it = targets.entrySet().iterator();
//			while(it.hasNext()){
//				Entry<Target, Deferred<Arm>> target = it.next();
//				for(JointValue joint : target.getKey().target){
//					if(joint.joint.equals(v.joint)){
//						Deferred<Arm> deferred = target.getValue();
//						try {		
//							deferred.fail(new Exception("Operation interrupted!"));
//						} catch(IllegalStateException ex){}
//						it.remove();
//					}
//				}
//			}
//		}
//		Deferred<Arm> deferred = new Deferred<Arm>();
//		Target target = new Target(torques);
//		targets.put(target, deferred);
//
//		brics_actuator.JointTorques msg = pTorq.newMessage();
//		List<brics_actuator.JointValue> tt = new ArrayList<>();
//		for(JointValue torque : torques){
//			brics_actuator.JointValue tor = factory.newFromType(brics_actuator.JointValue._TYPE);
//			tor.setJointUri(torque.joint);
//			tor.setUnit("Nm");
//		
//			JointDescription d = getJoint(torque.joint).getDescription();
//			torque.value = clamp(torque.value, d.positionMin, d.positionMax);
//			tor.setValue(torque.value);
//			tt.add(tor);
//		}
//		msg.setTorques(tt);
//		pTorq.publish(msg);
//		
//		return deferred.getPromise();
	}

	@Override
	public Promise<Arm> openGripper(float opening) {
		List<JointValue> positions = new ArrayList<>();
		positions.add(new JointValue(config[5], Type.POSITION, opening/2));
		positions.add(new JointValue(config[6], Type.POSITION, opening/2));
		return setPositions(positions);
	}

	@Override
	public Promise<Arm> closeGripper() {
		return openGripper(0);
	}
	
	@Override
	public Promise<Arm> setPositions(float... position) {
		List<JointValue> jointValues = new ArrayList<>();
		for(int i=0;i<position.length;i++){
			JointValue val = new JointValue(joints.get(i).getName(), Type.POSITION, position[i]);
			jointValues.add(val);
		}
		return setPositions(jointValues);
	}

	@Override
	public Promise<Arm> setVelocities(float... velocity) {
		List<JointValue> jointValues = new ArrayList<>();
		for(int i=0;i<velocity.length;i++){
			JointValue val = new JointValue(joints.get(i).getName(), Type.VELOCITY, velocity[i]);
			jointValues.add(val);
		}
		return setVelocities(jointValues);
	}

	@Override
	public Promise<Arm> setTorques(float... torque) {
		List<JointValue> jointValues = new ArrayList<>();
		for(int i=0;i<torque.length;i++){
			JointValue val = new JointValue(joints.get(i).getName(), Type.TORQUE, torque[i]);
			jointValues.add(val);
		}
		return setTorques(jointValues);
	}

	@Override
	public Promise<Arm> waitFor(long time) {
		Deferred<Arm> deferred = new Deferred<Arm>();
		timer.schedule(new ResolveTask(deferred), time);
		return deferred.getPromise();
	}
	
	@Override
	public Promise<Arm> reset() {
		return setPositions(0.0f, 0.0f, 0.0f, 0.0f, 0.0f).then(p -> closeGripper());
	}
	
	private class ResolveTask extends TimerTask {
		
		private Deferred<Arm> deferred;
		
		public ResolveTask(Deferred<Arm> deferred){
			this.deferred = deferred;
		}
		
		@Override
		public void run() {
			try {
				deferred.resolve(ArmImpl.this);
			} catch(IllegalStateException e){
				// ignore if already resolved
			}
		}
	}
	
	@Override
	public Promise<Arm> stop() {
		// TODO how to implement stop? motors off?

		// TODO stop?
		System.err.println("Stop not implemented atm...");
		
		// return an already resolved promise?
		Deferred<Arm> d = new Deferred<>();
		d.resolve(ArmImpl.this);
		return d.getPromise();
	}

	
	private class Target {
		
		private static final float THRESHOLD = 0.002f;
		
		final Collection<JointValue> target;
		
		public Target(Collection<JointValue> states){
			this.target = states;
		}
		
		public boolean isResolved(){
			for(JointValue v : target){
				Joint joint = getJoint(v.joint);
				if(joint==null)
					return false;
				
				switch(v.type){
				case POSITION:
					if(Math.abs(joint.getPosition()-v.value) > THRESHOLD){
						return false;
					}
					break;
				case VELOCITY:
					if(Math.abs(joint.getVelocity()-v.value) > THRESHOLD){
						return false;
					}
					break;
				case TORQUE:
					if(Math.abs(joint.getTorque()-v.value) > THRESHOLD){
						return false;
					}
					break;
				}
			}
			return true;
		}
	}
	
	private JointImpl getJoint(String name){
		try {
			return joints.stream().filter(j -> j.getName().equals(name)).findFirst().get();
		} catch(NoSuchElementException e){
			return null;
		}
	}

	private float clamp(float value, float min, float max){
		if(value < min){
			return min;
		} else if(value > max){
			return max;
		}
		return value;
	}
}
