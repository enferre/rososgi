package be.iminds.iot.simulator.vrep.ros.youbot;

import java.util.List;

import org.ros.message.MessageListener;
import org.ros.node.ConnectedNode;
import org.ros.node.service.ServiceClient;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;

import be.iminds.iot.simulator.vrep.ros.VREPInterface;
import vrep_common.simRosDisablePublisher;
import vrep_common.simRosDisablePublisherRequest;
import vrep_common.simRosDisablePublisherResponse;
import vrep_common.simRosEnablePublisher;
import vrep_common.simRosEnablePublisherRequest;
import vrep_common.simRosEnablePublisherResponse;
import vrep_common.simRosGetObjectHandle;
import vrep_common.simRosGetObjectHandleRequest;
import vrep_common.simRosGetObjectHandleResponse;

public class VREPYoubot {

	private VREPYoubotBase base;
	private VREPYoubotArm arm;

	private String name;
	private int handle = -1;
	
	private ServiceClient<simRosGetObjectHandleRequest, simRosGetObjectHandleResponse> getHandle;
	private ServiceClient<simRosEnablePublisherRequest, simRosEnablePublisherResponse> enablePublisher;
	private ServiceClient<simRosDisablePublisherRequest, simRosDisablePublisherResponse> disablePublisher;

	private Subscriber<geometry_msgs.Twist> cmd_vel;
	private Subscriber<brics_actuator.JointPositions> arm_pos;
	private Subscriber<brics_actuator.JointVelocities> arm_vel;
	private Subscriber<brics_actuator.JointPositions> grip_pos;
	
	private Subscriber<sensor_msgs.JointState> subJoint;
	private Publisher<sensor_msgs.JointState>  pubJoint;

	private YoubotVREPConvertor convertor;
	
	private ConnectedNode node;
	private VREPInterface vrep;
	
	private boolean enabled = false;
	
	public VREPYoubot(ConnectedNode node, VREPInterface vrep, String name, 
			String joint0, String joint1, String joint2, String joint3, String joint4, 
			String gripperL, String gripperR,
			String wheelFL, String wheelFR, String wheelRL, String wheelRR) throws Exception {
		
		this.node = node;
		this.vrep = vrep;
		
		convertor = new YoubotVREPConvertor(joint0, joint1, joint2, joint3, joint4, gripperL, gripperR);
		
		getHandle = node.newServiceClient("/vrep/simRosGetObjectHandle", simRosGetObjectHandle._TYPE);
		enablePublisher = node.newServiceClient("/vrep/simRosEnablePublisher", simRosEnablePublisher._TYPE);
		disablePublisher = node.newServiceClient("/vrep/simRosDisablePublisher", simRosDisablePublisher._TYPE);
		
		this.base = new VREPYoubotBase(vrep, 
				vrep.getObjectHandle(wheelFL).getValue(),
				vrep.getObjectHandle(wheelFR).getValue(), 
				vrep.getObjectHandle(wheelRL).getValue(), 
				vrep.getObjectHandle(wheelRR).getValue());
		
		this.arm = new VREPYoubotArm(vrep, 
				vrep.getObjectHandle(joint0).getValue(),
				vrep.getObjectHandle(joint1).getValue(),
				vrep.getObjectHandle(joint2).getValue(),
				vrep.getObjectHandle(joint3).getValue(),
				vrep.getObjectHandle(joint4).getValue(),
				vrep.getObjectHandle(gripperL).getValue(),
				vrep.getObjectHandle(gripperR).getValue());
		
		this.name = name;
		this.handle = vrep.getObjectHandle(name).getValue();
		
	}
	
	public VREPYoubotBase base(){
		return base;
	}
	
	public VREPYoubotArm arm(){
		return arm;
	}
	
	public void enable(){
		if(enabled)
			return;
		
		// TODO setup ROS topics similar to youbot ros driver
		//vrep.enablePublisher("/"+name+"/joint_state_1", 4102, arm.joints[1]);

		vrep.enablePublisher("/vrep/joint_states", 4102, -2); // publish all joint states in scene
		// translate from vrep joint_states to the joint_states youbot expects
		pubJoint = node.newPublisher("/joint_states", sensor_msgs.JointState._TYPE);
		subJoint = node.newSubscriber("/vrep/joint_states", sensor_msgs.JointState._TYPE);
		subJoint.addMessageListener(new MessageListener<sensor_msgs.JointState>() {
			@Override
			public void onNewMessage(sensor_msgs.JointState jointStates) {
				sensor_msgs.JointState translated = pubJoint.newMessage();
				List<String> names = convertor.getYoubotArmJoints();
				double[] pos = new double[names.size()];
				double[] vel = new double[names.size()];
				double[] tor = new double[names.size()];

				for(int i=0;i<names.size();i++){
					String vrepJoint = convertor.getVREPJoint(names.get(i));
					int index = jointStates.getName().indexOf(vrepJoint);
					if(index!=-1){
						pos[i] = convertor.invert(jointStates.getPosition()[index], i);
						vel[i] = jointStates.getVelocity()[index];
						tor[i] = jointStates.getEffort()[index];
					} else {
						System.err.println("Joint state message missing joint "+vrepJoint);
					}
				}
				translated.setName(names);
				translated.setPosition(pos);
				translated.setVelocity(vel);
				translated.setEffort(tor);
				pubJoint.publish(translated);
			}
		});
		
		vrep.enablePublisher("/odom", 8193, handle); // publish odom of youbot handle
		
		// setup subscribers
		subscribe();
		
		enabled = true;
	}
	
	public void disable(){
		vrep.disablePublisher("/joint_states");
		vrep.disablePublisher("/odom"); 
		
		subJoint.shutdown();
		pubJoint.shutdown();
		
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
					
					int i = convertor.getJointIndex(joint);
					arm.setTargetPosition(i, convertor.convert(val, i));
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
					
					int i = convertor.getJointIndex(joint);
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
					
					int i = convertor.getJointIndex(joint);
					arm.setTargetPosition(i, convertor.convert(val, i));
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
}
