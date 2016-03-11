package be.iminds.iot.ros.simulator.vrep.youbot;

import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.Promise;
import org.ros.exception.RemoteException;
import org.ros.message.MessageListener;
import org.ros.node.ConnectedNode;
import org.ros.node.service.ServiceClient;
import org.ros.node.service.ServiceResponseListener;
import org.ros.node.topic.Subscriber;

import be.iminds.iot.ros.simulator.vrep.VREPJointController;
import vrep_common.simRosDisablePublisher;
import vrep_common.simRosDisablePublisherRequest;
import vrep_common.simRosDisablePublisherResponse;
import vrep_common.simRosEnablePublisher;
import vrep_common.simRosEnablePublisherRequest;
import vrep_common.simRosEnablePublisherResponse;
import vrep_common.simRosGetObjectHandle;
import vrep_common.simRosGetObjectHandleRequest;
import vrep_common.simRosGetObjectHandleResponse;

public class Youbot {

	private YoubotBase base;
	private YoubotArm arm;

	private String name;
	private int handle = -1;
	
	private ServiceClient<simRosGetObjectHandleRequest, simRosGetObjectHandleResponse> getHandle;
	private ServiceClient<simRosEnablePublisherRequest, simRosEnablePublisherResponse> enablePublisher;
	private ServiceClient<simRosDisablePublisherRequest, simRosDisablePublisherResponse> disablePublisher;

	private Subscriber<geometry_msgs.Twist> cmd_vel;
	private Subscriber<brics_actuator.JointPositions> arm_pos;
	private Subscriber<brics_actuator.JointVelocities> arm_vel;
	private Subscriber<brics_actuator.JointPositions> grip_pos;
	
	private ConnectedNode node;
	
	private boolean enabled = false;
	
	public Youbot(ConnectedNode node, String name, 
			String joint0, String joint1, String joint2, String joint3, String joint4, 
			String gripperL, String gripperR,
			String wheelFL, String wheelFR, String wheelRL, String wheelRR) throws Exception {
		
		this.node = node;
		
		getHandle = node.newServiceClient("/vrep/simRosGetObjectHandle", simRosGetObjectHandle._TYPE);
		enablePublisher = node.newServiceClient("/vrep/simRosEnablePublisher", simRosEnablePublisher._TYPE);
		disablePublisher = node.newServiceClient("/vrep/simRosDisablePublisher", simRosDisablePublisher._TYPE);

		
		VREPJointController c = new VREPJointController(node);
		
		this.base = new YoubotBase(c, 
				getHandle(wheelFL).getValue(),
				getHandle(wheelFR).getValue(), 
				getHandle(wheelRL).getValue(), 
				getHandle(wheelRR).getValue());
		
		this.arm = new YoubotArm(c, 
				getHandle(joint0).getValue(),
				getHandle(joint1).getValue(),
				getHandle(joint2).getValue(),
				getHandle(joint3).getValue(),
				getHandle(joint4).getValue(),
				getHandle(gripperL).getValue(),
				getHandle(gripperR).getValue());
		
		this.name = name;
		this.handle = getHandle(name).getValue();
		
	}
	
	public YoubotBase base(){
		return base;
	}
	
	public YoubotArm arm(){
		return arm;
	}
	
	public void enable(){
		if(enabled)
			return;
		
		// TODO setup ROS topics similar to youbot ros driver
		//enablePublisher("/"+name+"/joint_state_1", 4102, arm.joints[1]);

		enablePublisher("/joint_states", 4102, -2); // publish all joint states in scene
		enablePublisher("/odom", 8193, handle); // publish odom of youbot handle
		
		// setup subscribers
		subscribe();
		
		enabled = true;
	}
	
	public void disable(){
		disablePublisher("/joint_states");
		disablePublisher("/odom"); 
		
		unsubscribe();
		
		enabled = false;
	}
	
	private void subscribe(){
		cmd_vel = node.newSubscriber("/cmd_vel", geometry_msgs.Twist._TYPE);
		cmd_vel.addMessageListener(new MessageListener<geometry_msgs.Twist>() {
			@Override
			public void onNewMessage(geometry_msgs.Twist message) {
				double x = message.getLinear().getX();
				double y = message.getLinear().getY();
				double a = message.getAngular().getZ();
				
				// y and x are flipped between real youbot and vrep apparently...
				base.move(y, x, a);
			}
		});
		
		arm_pos = node.newSubscriber("/arm_1/arm_controller/position_command", brics_actuator.JointPositions._TYPE);
		arm_pos.addMessageListener(new MessageListener<brics_actuator.JointPositions>() {
			@Override
			public void onNewMessage(brics_actuator.JointPositions message) {
				message.getPositions().stream().forEach(p -> {
					String joint = p.getJointUri();
					double val = p.getValue();
					
					int i = arm.getJoint(joint);
					arm.setTargetPosition(i, arm.convert(val, i));
				});
			}
		});
		
		arm_vel = node.newSubscriber("/arm_1/arm_controller/velocity_command", brics_actuator.JointVelocities._TYPE);
		arm_vel.addMessageListener(new MessageListener<brics_actuator.JointVelocities>() {
			@Override
			public void onNewMessage(brics_actuator.JointVelocities message) {
				message.getVelocities().stream().forEach(p -> {
					String joint = p.getJointUri();
					double val = p.getValue();
					
					int i = arm.getJoint(joint);
					arm.setTargetVelocity(i, val);
				});
			}
		});
		
		grip_pos = node.newSubscriber("/arm_1/gripper_controller/position_command", brics_actuator.JointPositions._TYPE);
		grip_pos.addMessageListener(new MessageListener<brics_actuator.JointPositions>() {
			@Override
			public void onNewMessage(brics_actuator.JointPositions message) {
				message.getPositions().stream().forEach(p -> {
					String joint = p.getJointUri();
					double val = p.getValue();
					
					int i = arm.getJoint(joint);
					arm.setTargetPosition(i, arm.convert(val, i));
				});
			}
		});
	}
	
	private void unsubscribe(){
		cmd_vel.shutdown();
		arm_pos.shutdown();
		arm_vel.shutdown();
		grip_pos.shutdown();
	}
	
	private void enablePublisher(String topic, int type, int handle){
		final simRosEnablePublisherRequest request = enablePublisher.newMessage();
		request.setTopicName(topic);
		request.setQueueSize(1);
		request.setStreamCmd(type);
		request.setAuxInt1(handle);
		request.setAuxInt2(-1);
		request.setAuxString("");
		enablePublisher.call(request, new ServiceResponseListener<simRosEnablePublisherResponse>() {
			@Override
			public void onSuccess(simRosEnablePublisherResponse response) {
			}
			@Override
			public void onFailure(RemoteException e) {
			}
		});	
	}
	
	private void disablePublisher(String topic){
		final simRosDisablePublisherRequest request = disablePublisher.newMessage();
		request.setTopicName(topic);
		disablePublisher.call(request, new ServiceResponseListener<simRosDisablePublisherResponse>() {
			@Override
			public void onSuccess(simRosDisablePublisherResponse response) {
			}
			@Override
			public void onFailure(RemoteException e) {
			}
		});	
	}
	
	private Promise<Integer> getHandle(String name){
		final simRosGetObjectHandleRequest request = getHandle.newMessage();
		request.setObjectName(name);
		final Deferred<Integer> deferred = new Deferred<>();		
		getHandle.call(request, new ServiceResponseListener<simRosGetObjectHandleResponse>() {
			@Override
			public void onSuccess(simRosGetObjectHandleResponse response) {
				deferred.resolve(response.getHandle());
			}
			@Override
			public void onFailure(RemoteException e) {
				deferred.fail(e);
			}
		});	
		return deferred.getPromise();
	}
	
}
