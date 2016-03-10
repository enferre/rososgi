package be.iminds.iot.ros.simulator.vrep;

import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.Promise;
import org.ros.exception.RemoteException;
import org.ros.node.ConnectedNode;
import org.ros.node.service.ServiceClient;
import org.ros.node.service.ServiceResponseListener;

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
	
	public Youbot(ConnectedNode node, String name, 
			String joint0, String joint1, String joint2, String joint3, String joint4, 
			String gripperL, String gripperR,
			String wheelFL, String wheelFR, String wheelRL, String wheelRR) throws Exception {
		
		getHandle = node.newServiceClient("/vrep/simRosGetObjectHandle", simRosGetObjectHandle._TYPE);
		enablePublisher = node.newServiceClient("/vrep/simRosEnablePublisher", simRosEnablePublisher._TYPE);

		
		JointController c = new JointController(node);
		
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
		
		// TODO subscribe to topics to control this youbot
	}
	
	public YoubotBase base(){
		return base;
	}
	
	public YoubotArm arm(){
		return arm;
	}
	
	public void publish(){
		// TODO setup ROS topics similar to youbot ros driver
		enablePublisher("/"+name+"/joint_state_0", 4102, arm.joints[0]);
		enablePublisher("/"+name+"/joint_state_1", 4102, arm.joints[1]);
		enablePublisher("/"+name+"/joint_state_2", 4102, arm.joints[2]);
		enablePublisher("/"+name+"/joint_state_3", 4102, arm.joints[3]);
		enablePublisher("/"+name+"/joint_state_4", 4102, arm.joints[4]);
		enablePublisher("/"+name+"/odom", 8193, handle);
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
