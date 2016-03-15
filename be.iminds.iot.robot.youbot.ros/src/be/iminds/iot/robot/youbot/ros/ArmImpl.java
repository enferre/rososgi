package be.iminds.iot.robot.youbot.ros;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Timer;
import java.util.TimerTask;
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

	
	private Deferred<Void> deferred = null;
	private Target target = null;
	private Timer timer = new Timer();
	
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
		for(String name : config){
			JointImpl joint = new JointImpl(name, this);
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
					joint.torque = (float)jointState.getEffort()[i];
				}
				
				// check if target is resolved 
				if(target!=null && target.isResolved()){
					Deferred d = deferred;
					if(d!=null){
						synchronized(ArmImpl.this){
							deferred = null;
							target = null;
						}
						d.resolve(null);
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
	public List<String> getJoints() {
		return joints.stream().map(j -> j.getName()).collect(Collectors.toList());
	}

	@Override
	public List<JointState> getState() {
		return joints.stream().map(j -> j.getState()).collect(Collectors.toList());
	}

	@Override
	public Promise<Void> setPosition(int joint, float position){
		return setPositions(Collections.singleton(new JointValue(config[joint], Type.POSITION, position)));
	}
	
	@Override
	public Promise<Void> setVelocity(int joint, float velocity){
		return setVelocities(Collections.singleton(new JointValue(config[joint], Type.VELOCITY, velocity)));
	}

	@Override
	public Promise<Void> setTorque(int joint, float torque){
		return setTorques(Collections.singleton(new JointValue(config[joint], Type.TORQUE, torque)));
	}
	
	@Override
	public synchronized Promise<Void> setPositions(Collection<JointValue> positions) {
		if(deferred!=null){
			deferred.fail(new Exception("Operation interrupted!"));
		}
		deferred = new Deferred<Void>();
		target = new Target(positions);

		brics_actuator.JointPositions msg = pPos.newMessage();
		List<brics_actuator.JointValue> pp = new ArrayList<>();
		for(JointValue position : positions){
			brics_actuator.JointValue pos = factory.newFromType(brics_actuator.JointValue._TYPE);
			pos.setJointUri(position.joint);
			pos.setUnit("rad");
			pos.setValue(position.value);
			pp.add(pos);
		}
		msg.setPositions(pp);
		pPos.publish(msg);
		
		return deferred.getPromise();
	}

	@Override
	public synchronized Promise<Void> setVelocities(Collection<JointValue> velocities) {
		if(deferred!=null){
			deferred.fail(new Exception("Operation interrupted!"));
		}
		deferred = new Deferred<Void>();
		target = new Target(velocities);

		brics_actuator.JointVelocities msg = pVel.newMessage();
		List<brics_actuator.JointValue> vv = new ArrayList<>();
		for(JointValue velocity : velocities){
			brics_actuator.JointValue vel = factory.newFromType(brics_actuator.JointValue._TYPE);
			vel.setJointUri(velocity.joint);
			vel.setUnit("rad/s");
			vel.setValue(velocity.value);
			vv.add(vel);
		}
		msg.setVelocities(vv);
		pVel.publish(msg);
		
		return deferred.getPromise();
	}

	@Override
	public synchronized Promise<Void> setTorques(Collection<JointValue> torques) {
		throw new UnsupportedOperationException();
		
//		if(deferred!=null){
//			deferred.fail(new Exception("Operation interrupted!"));
//		}
//		deferred = new Deferred<Void>();
//		target = new Target(torques);
//
//		brics_actuator.JointTorques msg = pTorq.newMessage();
//		List<brics_actuator.JointValue> tt = new ArrayList<>();
//		for(JointValue torque : torques){
//			brics_actuator.JointValue tor = factory.newFromType(brics_actuator.JointValue._TYPE);
//			tor.setJointUri(torque.joint);
//			tor.setUnit("Nm");
//			tor.setValue(torque.value);
//			tt.add(tor);
//		}
//		msg.setTorques(tt);
//		pTorq.publish(msg);
//		
//		return deferred.getPromise();
	}

	@Override
	public Promise<Void> openGripper(float opening) {
		List<JointValue> positions = new ArrayList<>();
		positions.add(new JointValue(config[5], Type.POSITION, opening/2));
		positions.add(new JointValue(config[6], Type.POSITION, opening/2));
		return setPositions(positions);
	}

	@Override
	public Promise<Void> closeGripper() {
		return openGripper(0);
	}
	
	@Override
	public Promise<Void> setPositions(float... position) {
		List<JointValue> jointValues = new ArrayList<>();
		for(int i=0;i<position.length;i++){
			JointValue val = new JointValue(joints.get(i).getName(), Type.POSITION, position[i]);
			jointValues.add(val);
		}
		return setPositions(jointValues);
	}

	@Override
	public Promise<Void> setVelocities(float... velocity) {
		List<JointValue> jointValues = new ArrayList<>();
		for(int i=0;i<velocity.length;i++){
			JointValue val = new JointValue(joints.get(i).getName(), Type.VELOCITY, velocity[i]);
			jointValues.add(val);
		}
		return setVelocities(jointValues);
	}

	@Override
	public Promise<Void> setTorques(float... torque) {
		List<JointValue> jointValues = new ArrayList<>();
		for(int i=0;i<torque.length;i++){
			JointValue val = new JointValue(joints.get(i).getName(), Type.TORQUE, torque[i]);
			jointValues.add(val);
		}
		return setTorques(jointValues);
	}

	@Override
	public synchronized Promise<Void> waitFor(long time) {
		if(deferred!=null){
			deferred.fail(new Exception("Operation interrupted!"));
		}
		deferred = new Deferred<Void>();

		timer.schedule(new ResolveTask(deferred), time);
	
		return deferred.getPromise();
	}
	
	private class ResolveTask extends TimerTask {
		
		private Deferred<Void> deferred;
		
		public ResolveTask(Deferred<Void> deferred){
			this.deferred = deferred;
		}
		
		@Override
		public void run() {
			if(deferred == ArmImpl.this.deferred){
				synchronized(ArmImpl.this){
					ArmImpl.this.deferred = null;
				}
			}
				
			try {
				deferred.resolve(null);
			} catch(IllegalStateException e){
				// ignore if already resolved
			}
		}
	}
	
	@Override
	public Promise<Void> stop() {
		// TODO how to implement stop? motors off?
		if(deferred!=null){
			deferred.fail(new Exception("Operation interrupted!"));
		}
		
		// TODO stop?
		
		// return an already resolved promise?
		Deferred<Void> d = new Deferred<>();
		d.resolve(null);
		return d.getPromise();
	}

	
	private class Target {
		
		private static final float THRESHOLD = 0.002f;
		
		private final Collection<JointValue> target;
		
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

}
