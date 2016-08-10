package be.iminds.iot.simulator.vrep.ros;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.Promise;
import org.ros.exception.RemoteException;
import org.ros.node.ConnectedNode;
import org.ros.node.service.ServiceClient;
import org.ros.node.service.ServiceResponseListener;

import vrep_common.simRosDisablePublisher;
import vrep_common.simRosDisablePublisherRequest;
import vrep_common.simRosDisablePublisherResponse;
import vrep_common.simRosEnablePublisher;
import vrep_common.simRosEnablePublisherRequest;
import vrep_common.simRosEnablePublisherResponse;
import vrep_common.simRosGetObjectHandle;
import vrep_common.simRosGetObjectHandleRequest;
import vrep_common.simRosGetObjectHandleResponse;
import vrep_common.simRosLoadScene;
import vrep_common.simRosLoadSceneRequest;
import vrep_common.simRosLoadSceneResponse;
import vrep_common.simRosPauseSimulation;
import vrep_common.simRosPauseSimulationRequest;
import vrep_common.simRosPauseSimulationResponse;
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
import vrep_common.simRosStartSimulation;
import vrep_common.simRosStartSimulationRequest;
import vrep_common.simRosStartSimulationResponse;
import vrep_common.simRosStopSimulation;
import vrep_common.simRosStopSimulationRequest;
import vrep_common.simRosStopSimulationResponse;
import vrep_common.simRosSynchronous;
import vrep_common.simRosSynchronousRequest;
import vrep_common.simRosSynchronousResponse;
import vrep_common.simRosSynchronousTrigger;
import vrep_common.simRosSynchronousTriggerRequest;
import vrep_common.simRosSynchronousTriggerResponse;

public class VREPInterface {

	/* Simulator */
	private ServiceClient<simRosStartSimulationRequest, simRosStartSimulationResponse> startSimulation;
	private ServiceClient<simRosStopSimulationRequest, simRosStopSimulationResponse> stopSimulation;
	private ServiceClient<simRosPauseSimulationRequest, simRosPauseSimulationResponse> pauseSimulation;
	private ServiceClient<simRosSynchronousRequest, simRosSynchronousResponse> synchronous;
	private ServiceClient<simRosSynchronousTriggerRequest, simRosSynchronousTriggerResponse> trigger;
	private ServiceClient<simRosLoadSceneRequest, simRosLoadSceneResponse> loadScene;
	
	/* Objects */
	private ServiceClient<simRosGetObjectHandleRequest, simRosGetObjectHandleResponse> getHandle;
	private ServiceClient<simRosSetObjectIntParameterRequest, simRosSetObjectIntParameterResponse> setIntParam;
	
	/* Joints */
	private ServiceClient<simRosSetJointPositionRequest, simRosSetJointPositionResponse> setJointPosition;
	private ServiceClient<simRosSetJointTargetPositionRequest, simRosSetJointTargetPositionResponse> setJointTargetPosition;
	private ServiceClient<simRosSetJointTargetVelocityRequest, simRosSetJointTargetVelocityResponse> setJointVelocity;
	private ServiceClient<simRosSetJointForceRequest, simRosSetJointForceResponse> setJointTorque;

	/* ROS publishers */
	private ServiceClient<simRosEnablePublisherRequest, simRosEnablePublisherResponse> enablePublisher;
	private ServiceClient<simRosDisablePublisherRequest, simRosDisablePublisherResponse> disablePublisher;

	
	public VREPInterface(ConnectedNode node) throws Exception {
		startSimulation = node.newServiceClient("/vrep/simRosStartSimulation", simRosStartSimulation._TYPE);
		stopSimulation = node.newServiceClient("/vrep/simRosStopSimulation", simRosStopSimulation._TYPE);
		pauseSimulation = node.newServiceClient("/vrep/simRosPauseSimulation", simRosPauseSimulation._TYPE);
		synchronous = node.newServiceClient("/vrep/simRosSynchronous", simRosSynchronous._TYPE);
		trigger = node.newServiceClient("/vrep/simRosSynchronousTrigger", simRosSynchronousTrigger._TYPE);
		loadScene = node.newServiceClient("/vrep/simRosLoadScene", simRosLoadScene._TYPE);
		
		getHandle = node.newServiceClient("/vrep/simRosGetObjectHandle", simRosGetObjectHandle._TYPE);
		setIntParam = node.newServiceClient("/vrep/simRosSetObjectIntParameter", simRosSetObjectIntParameter._TYPE);
		
		
		setJointPosition = node.newServiceClient("/vrep/simRosSetJointPosition", simRosSetJointPosition._TYPE);
		setJointTargetPosition = node.newServiceClient("/vrep/simRosSetJointTargetPosition", simRosSetJointTargetPosition._TYPE);
		setJointVelocity = node.newServiceClient("/vrep/simRosSetJointTargetVelocity", simRosSetJointTargetVelocity._TYPE);
		setJointTorque = node.newServiceClient("/vrep/simRosSetJointForce", simRosSetJointForce._TYPE);
		
		enablePublisher = node.newServiceClient("/vrep/simRosEnablePublisher", simRosEnablePublisher._TYPE);
		disablePublisher = node.newServiceClient("/vrep/simRosDisablePublisher", simRosDisablePublisher._TYPE);
	}
	
	/*
	 * Simulator
	 */ 
	
	public void startSimuation() {
		final simRosStartSimulationRequest request = startSimulation.newMessage();
		final Deferred<Void> deferred = new Deferred<>();		
		startSimulation.call(request, new ServiceResponseListener<simRosStartSimulationResponse>() {
			@Override
			public void onSuccess(simRosStartSimulationResponse response) {
				if(response.getResult()==-1){
					deferred.fail(new Exception("Failed to start simulation"));
				} else {
					deferred.resolve((Void)null);
				}
			}
			@Override
			public void onFailure(RemoteException e) {
				deferred.fail(e);
			}
		});	
		try {
			deferred.getPromise().getValue();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void pauseSimulation() {
		final simRosPauseSimulationRequest request = pauseSimulation.newMessage();
		final Deferred<Void> deferred = new Deferred<>();		
		pauseSimulation.call(request, new ServiceResponseListener<simRosPauseSimulationResponse>() {
			@Override
			public void onSuccess(simRosPauseSimulationResponse response) {
				if(response.getResult()==-1){
					deferred.fail(new Exception("Failed to pause simulator"));
				} else {
					deferred.resolve((Void)null);
				}
			}
			@Override
			public void onFailure(RemoteException e) {
				deferred.fail(e);
			}
		});	
		try {
			deferred.getPromise().getValue();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void stopSimulation() {
		final simRosStopSimulationRequest request = stopSimulation.newMessage();
		final Deferred<Void> deferred = new Deferred<>();		
		stopSimulation.call(request, new ServiceResponseListener<simRosStopSimulationResponse>() {
			@Override
			public void onSuccess(simRosStopSimulationResponse response) {
				if(response.getResult()==-1){
					deferred.fail(new Exception("Failed to stop simulator"));
				} else {
					deferred.resolve((Void)null);
				}
			}
			@Override
			public void onFailure(RemoteException e) {
				deferred.fail(e);
			}
		});	
		try {
			deferred.getPromise().getValue();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public void setSynchronous(boolean sync){
		final simRosSynchronousRequest request = synchronous.newMessage();
		request.setEnable((byte) (sync ? 1 : 0));
		final Deferred<Void> deferred = new Deferred<>();		
		synchronous.call(request, new ServiceResponseListener<simRosSynchronousResponse>() {
			@Override
			public void onSuccess(simRosSynchronousResponse response) {
				if(response.getResult()==-1){
					deferred.fail(new Exception("Failed to set synchronous mode"));
				} else {
					deferred.resolve((Void)null);
				}
			}
			@Override
			public void onFailure(RemoteException e) {
				deferred.fail(e);
			}
		});	
		try {
			deferred.getPromise().getValue();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void trigger() {
		final simRosSynchronousTriggerRequest request = trigger.newMessage();
		final Deferred<Void> deferred = new Deferred<>();		
		trigger.call(request, new ServiceResponseListener<simRosSynchronousTriggerResponse>() {
			@Override
			public void onSuccess(simRosSynchronousTriggerResponse response) {				
				if(response.getResult()==-1){
					deferred.fail(new Exception("Failed to trigger simulator"));
				} else {
					deferred.resolve((Void)null);
				}
			}
			@Override
			public void onFailure(RemoteException e) {
				deferred.fail(e);
			}
		});	
		try {
			deferred.getPromise().getValue();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void loadScene(String file) {
		File f = new File(file);
		final simRosLoadSceneRequest request = loadScene.newMessage();
		request.setFileName(f.getAbsolutePath());
		final Deferred<Void> deferred = new Deferred<>();		
		loadScene.call(request, new ServiceResponseListener<simRosLoadSceneResponse>() {
			@Override
			public void onSuccess(simRosLoadSceneResponse response) {
				if(response.getResult()==-1){
					deferred.fail(new Exception("Failed to load scene"));
				} else {
					deferred.resolve((Void)null);
				}
			}
			@Override
			public void onFailure(RemoteException e) {
				deferred.fail(e);
			}
		});	
		try {
			deferred.getPromise().getValue();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Objects
	 */ 
	
	public int getObjectHandle(String name){
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
		try {
			return deferred.getPromise().getValue();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	
	public void setParameter(int handle, int param, int value){
		final simRosSetObjectIntParameterRequest request = setIntParam.newMessage();
		request.setHandle(handle);
		request.setParameter(param);
		request.setParameterValue(value);
		final Deferred<Void> deferred = new Deferred<>();		
		setIntParam.call(request, new ServiceResponseListener<simRosSetObjectIntParameterResponse>() {
			@Override
			public void onSuccess(simRosSetObjectIntParameterResponse response) {
				if(response.getResult()==-1){
					deferred.fail(new Exception("Failed to set parameter"));
				} else {
					deferred.resolve((Void)null);
				}
			}
			@Override
			public void onFailure(RemoteException e) {
				deferred.fail(e);
			}
		});	
		try {
			deferred.getPromise().getValue();
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	
	/*
	 * Joints
	 */ 
	
	public void setJointPosition(int handle, double position){
		final simRosSetJointPositionRequest request = setJointPosition.newMessage();
		request.setHandle(handle);
		request.setPosition(position);
		final Deferred<Void> deferred = new Deferred<>();		
		setJointPosition.call(request, new ServiceResponseListener<simRosSetJointPositionResponse>() {
			@Override
			public void onSuccess(simRosSetJointPositionResponse response) {
				if(response.getResult()==-1){
					deferred.fail(new Exception("Failed to set joint position"));
				} else {
					deferred.resolve((Void)null);
				}
			}
			@Override
			public void onFailure(RemoteException e) {
				deferred.fail(e);
			}
		});	
		try {
			deferred.getPromise().getValue();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void setJointTargetPosition(int handle, double position){
		final simRosSetJointTargetPositionRequest request = setJointTargetPosition.newMessage();
		request.setHandle(handle);
		request.setTargetPosition(position);
		final Deferred<Void> deferred = new Deferred<>();		
		setJointTargetPosition.call(request, new ServiceResponseListener<simRosSetJointTargetPositionResponse>() {
			@Override
			public void onSuccess(simRosSetJointTargetPositionResponse response) {
				if(response.getResult()==-1){
					deferred.fail(new Exception("Failed to set joint target position"));
				} else {
					deferred.resolve((Void)null);
				}
			}
			@Override
			public void onFailure(RemoteException e) {
				deferred.fail(e);
			}
		});	
		try {
			deferred.getPromise().getValue();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void setJointTargetVelocity(int handle, double velocity){
		final simRosSetJointTargetVelocityRequest request = setJointVelocity.newMessage();
		request.setHandle(handle);
		request.setTargetVelocity(velocity);
		final Deferred<Void> deferred = new Deferred<>();		
		setJointVelocity.call(request, new ServiceResponseListener<simRosSetJointTargetVelocityResponse>() {
			@Override
			public void onSuccess(simRosSetJointTargetVelocityResponse response) {
				if(response.getResult()==-1){
					deferred.fail(new Exception("Failed to set joint target velocity"));
				} else {
					deferred.resolve((Void)null);
				}
			}
			@Override
			public void onFailure(RemoteException e) {
				deferred.fail(e);
			}
		});	
		try {
			deferred.getPromise().getValue();
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	
	public void setJointTorque(int handle, double torque){
		final simRosSetJointForceRequest request = setJointTorque.newMessage();
		request.setHandle(handle);
		request.setForceOrTorque(torque);
		final Deferred<Void> deferred = new Deferred<>();		
		setJointTorque.call(request, new ServiceResponseListener<simRosSetJointForceResponse>() {
			@Override
			public void onSuccess(simRosSetJointForceResponse response) {
				if(response.getResult()==-1){
					deferred.fail(new Exception("Failed to set joint torque"));
				} else {
					deferred.resolve((Void)null);
				}
			}
			@Override
			public void onFailure(RemoteException e) {
				deferred.fail(e);
			}
		});	
		try {
			deferred.getPromise().getValue();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * ROS publishers
	 */ 
	
	public void enablePublisher(String topic, int type, int handle){
		final simRosEnablePublisherRequest request = enablePublisher.newMessage();
		request.setTopicName(topic);
		request.setQueueSize(1);
		request.setStreamCmd(type);
		request.setAuxInt1(handle);
		request.setAuxInt2(-1);
		request.setAuxString("");
		final Deferred<Void> deferred = new Deferred<>();		
		enablePublisher.call(request, new ServiceResponseListener<simRosEnablePublisherResponse>() {
			@Override
			public void onSuccess(simRosEnablePublisherResponse response) {
				deferred.resolve((Void)null);
			}
			@Override
			public void onFailure(RemoteException e) {
				deferred.fail(e);
			}
		});	
		try {
			deferred.getPromise().getValue();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void disablePublisher(String topic){
		final simRosDisablePublisherRequest request = disablePublisher.newMessage();
		request.setTopicName(topic);
		final Deferred<Void> deferred = new Deferred<>();		
		disablePublisher.call(request, new ServiceResponseListener<simRosDisablePublisherResponse>() {
			@Override
			public void onSuccess(simRosDisablePublisherResponse response) {
				deferred.resolve((Void)null);
			}
			@Override
			public void onFailure(RemoteException e) {
				deferred.fail(e);
			}
		});	
		try {
			deferred.getPromise().getValue();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
