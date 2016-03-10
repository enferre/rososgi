package be.iminds.iot.ros.simulator.vrep;

import org.ros.exception.RemoteException;
import org.ros.node.ConnectedNode;
import org.ros.node.service.ServiceClient;
import org.ros.node.service.ServiceResponseListener;

import vrep_common.simRosSetJointForce;
import vrep_common.simRosSetJointForceRequest;
import vrep_common.simRosSetJointForceResponse;
import vrep_common.simRosSetJointPosition;
import vrep_common.simRosSetJointPositionRequest;
import vrep_common.simRosSetJointPositionResponse;
import vrep_common.simRosSetJointTargetPosition;
import vrep_common.simRosSetJointTargetPositionRequest;
import vrep_common.simRosSetJointTargetPositionResponse;
import vrep_common.simRosSetJointTargetVelocity;
import vrep_common.simRosSetJointTargetVelocityRequest;
import vrep_common.simRosSetJointTargetVelocityResponse;
import vrep_common.simRosSetObjectIntParameter;
import vrep_common.simRosSetObjectIntParameterRequest;
import vrep_common.simRosSetObjectIntParameterResponse;

public class JointController {

	private ServiceClient<simRosSetObjectIntParameterRequest, simRosSetObjectIntParameterResponse> setIntParam;
	private ServiceClient<simRosSetJointPositionRequest, simRosSetJointPositionResponse> setJointPosition;
	private ServiceClient<simRosSetJointTargetPositionRequest, simRosSetJointTargetPositionResponse> setJointTargetPos;
	private ServiceClient<simRosSetJointTargetVelocityRequest, simRosSetJointTargetVelocityResponse> setJointVelocity;
	private ServiceClient<simRosSetJointForceRequest, simRosSetJointForceResponse> setJointTorque;

	
	public JointController(ConnectedNode node){
		try {
			setIntParam = node.newServiceClient("/vrep/simRosSetObjectIntParameter", simRosSetObjectIntParameter._TYPE);
			setJointPosition = node.newServiceClient("/vrep/simRosSetJointPosition", simRosSetJointPosition._TYPE);
			setJointTargetPos = node.newServiceClient("/vrep/simRosSetJointTargetPosition", simRosSetJointTargetPosition._TYPE);
			setJointVelocity = node.newServiceClient("/vrep/simRosSetJointTargetVelocity", simRosSetJointTargetVelocity._TYPE);
			setJointTorque = node.newServiceClient("/vrep/simRosSetJointForce", simRosSetJointForce._TYPE);
		} catch(Exception e){
			System.err.println("Failed to create VREP joint controller");
		}
	}
	
	
	public void setJointParameter(int handle, int param, int value){
		final simRosSetObjectIntParameterRequest request = setIntParam.newMessage();
		request.setHandle(handle);
		request.setParameter(param);
		request.setParameterValue(value);
		setIntParam.call(request, new ServiceResponseListener<simRosSetObjectIntParameterResponse>() {
			@Override
			public void onSuccess(simRosSetObjectIntParameterResponse response) {
				if(response.getResult()==-1)
					System.err.println("Failed to set joint parameter.");
			}
			@Override
			public void onFailure(RemoteException e) {
			}
		});	
	}
	
	public void setJointPosition(int handle, float position){
		final simRosSetJointPositionRequest request = setJointPosition.newMessage();
		request.setHandle(handle);
		request.setPosition(position);
		setJointPosition.call(request, new ServiceResponseListener<simRosSetJointPositionResponse>() {
			@Override
			public void onSuccess(simRosSetJointPositionResponse response) {
				if(response.getResult()==-1)
					System.err.println("Failed to set joint position.");
			}
			@Override
			public void onFailure(RemoteException e) {
			}
		});	
	}
	
	public void setJointTargetPosition(int handle, float position){
		final simRosSetJointTargetPositionRequest request = setJointTargetPos.newMessage();
		request.setHandle(handle);
		request.setTargetPosition(position);
		setJointTargetPos.call(request, new ServiceResponseListener<simRosSetJointTargetPositionResponse>() {
			@Override
			public void onSuccess(simRosSetJointTargetPositionResponse response) {
				if(response.getResult()==-1)
					System.err.println("Failed to set joint target position.");
			}
			@Override
			public void onFailure(RemoteException e) {
			}
		});		
	}
	
	public void setJointTargetVelocity(int handle, float velocity){
		final simRosSetJointTargetVelocityRequest request = setJointVelocity.newMessage();
		request.setHandle(handle);
		request.setTargetVelocity(velocity);
		setJointVelocity.call(request, new ServiceResponseListener<simRosSetJointTargetVelocityResponse>() {
			@Override
			public void onSuccess(simRosSetJointTargetVelocityResponse response) {
				if(response.getResult()==-1)
					System.err.println("Failed to set joint target velocity.");
			}
			@Override
			public void onFailure(RemoteException e) {
			}
		});		
	}
	
	public void setJointTorque(int handle, float torque){
		final simRosSetJointForceRequest request = setJointTorque.newMessage();
		request.setHandle(handle);
		request.setForceOrTorque(torque);
		setJointTorque.call(request, new ServiceResponseListener<simRosSetJointForceResponse>() {
			@Override
			public void onSuccess(simRosSetJointForceResponse response) {
				if(response.getResult()==-1)
					System.err.println("Failed to set joint force.");
			}
			@Override
			public void onFailure(RemoteException e) {
			}
		});	
	}
	
}
