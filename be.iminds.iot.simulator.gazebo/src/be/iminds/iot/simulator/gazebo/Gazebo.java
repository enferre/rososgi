package be.iminds.iot.simulator.gazebo;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.osgi.util.promise.Deferred;
import org.ros.exception.RemoteException;
import org.ros.node.ConnectedNode;
import org.ros.node.service.ServiceClient;
import org.ros.node.service.ServiceResponseListener;

import be.iminds.iot.simulator.api.Orientation;
import be.iminds.iot.simulator.api.Position;
import be.iminds.iot.simulator.api.Simulator;
import std_srvs.EmptyResponse;

/**
 * Simulator implementation for the Gazebo simulator
 * 
 * @author tverbele
 *
 */
public class Gazebo implements Simulator {
	
	private ServiceClient<std_srvs.EmptyRequest, std_srvs.EmptyResponse> start;
	private ServiceClient<std_srvs.EmptyRequest, std_srvs.EmptyResponse> stop;
	private ServiceClient<std_srvs.EmptyRequest, std_srvs.EmptyResponse> resetSimulation;
	private ServiceClient<std_srvs.EmptyRequest, std_srvs.EmptyResponse> resetWorld;


	
	public Gazebo(ConnectedNode node) throws Exception{
		start = node.newServiceClient("/gazebo/unpause_physics", std_srvs.Empty._TYPE);
		stop = node.newServiceClient("/gazebo/pause_physics",  std_srvs.Empty._TYPE);
		resetSimulation = node.newServiceClient("/gazebo/reset_simulation",  std_srvs.Empty._TYPE);
		resetWorld = node.newServiceClient("/gazebo/reset_world",  std_srvs.Empty._TYPE);
		
	}
	
	public void start(){
		start(false);
	}
	
	@Override
	public synchronized void start(boolean sync) {
		final Deferred<Void> deferred = new Deferred<>();
		start.call(start.newMessage(), new ServiceResponseListener<EmptyResponse>() {
			@Override
			public void onFailure(RemoteException ex) {
				deferred.fail(ex);
			}
			@Override
			public void onSuccess(EmptyResponse arg0) {
				deferred.resolve(null);
			}
		});
		try {
			deferred.getPromise().getValue();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public synchronized void pause() {
		final Deferred<Void> deferred = new Deferred<>();
		stop.call(stop.newMessage(), new ServiceResponseListener<EmptyResponse>() {
			@Override
			public void onFailure(RemoteException ex) {
				deferred.fail(ex);
			}
			@Override
			public void onSuccess(EmptyResponse arg0) {
				deferred.resolve(null);
			}
		});
		try {
			deferred.getPromise().getValue();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public synchronized void stop() {
		final Deferred<Void> deferred = new Deferred<>();
		stop.call(stop.newMessage(), new ServiceResponseListener<EmptyResponse>() {
			@Override
			public void onSuccess(EmptyResponse r) {
				resetSimulation.call(resetSimulation.newMessage(), new ServiceResponseListener<EmptyResponse>() {
					@Override
					public void onSuccess(EmptyResponse paramMessageType) {
						resetWorld.call(resetWorld.newMessage(), new ServiceResponseListener<EmptyResponse>() {
							@Override
							public void onSuccess(EmptyResponse paramMessageType) {
								deferred.resolve(null);
							}
							@Override
							public void onFailure(RemoteException ex) {
								deferred.fail(ex);
							}
						});
					}
					@Override
					public void onFailure(RemoteException ex) {
						deferred.fail(ex);
					}
				});
			}
			@Override
			public void onFailure(RemoteException ex) {
				deferred.fail(ex);
			}
		});
		try {
			deferred.getPromise().getValue();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void tick() throws TimeoutException {
	}

	@Override
	public void loadScene(String file, Map<String, String> entities) {

	}

	@Override
	public Position getPosition(String object) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setPosition(String object, Position p) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Position getPosition(String object, String relativeTo) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setPosition(String object, String relativeTo, Position p) {
		throw new UnsupportedOperationException();
	}
		
	public void setPosition(String object, float x, float y, float z) {
		throw new UnsupportedOperationException();
	}
	
	public void setPosition(String object, String relativeTo, float x, float y, float z) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Orientation getOrientation(String object) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setOrientation(String object, Orientation o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Orientation getOrientation(String object, String relativeTo) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setOrientation(String object, String relativeTo, Orientation o) {
		throw new UnsupportedOperationException();
	}
	
	public void setOrientation(String object, float a, float b, float g) {
		throw new UnsupportedOperationException();
	}
	
	public void setOrientation(String object, String relativeTo, float a, float b, float g) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean checkCollisions(String object) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void setProperty(String object, String key, int value){
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void setProperty(String object, String key, float value){
		throw new UnsupportedOperationException();
	}

	@Override
	public void setProperty(String object, String key, boolean value){
		throw new UnsupportedOperationException();
	}

}
