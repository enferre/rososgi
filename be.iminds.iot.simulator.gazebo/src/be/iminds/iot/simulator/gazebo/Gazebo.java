package be.iminds.iot.simulator.gazebo;

import java.io.File;
import java.nio.file.Files;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.osgi.util.promise.Deferred;
import org.ros.exception.RemoteException;
import org.ros.message.MessageListener;
import org.ros.message.Time;
import org.ros.node.ConnectedNode;
import org.ros.node.service.ServiceClient;
import org.ros.node.service.ServiceResponseListener;
import org.ros.node.topic.Subscriber;

import be.iminds.iot.simulator.api.Orientation;
import be.iminds.iot.simulator.api.Position;
import be.iminds.iot.simulator.api.Simulator;
import gazebo_msgs.SpawnModelRequest;
import gazebo_msgs.SpawnModelResponse;
import geometry_msgs.Point;
import geometry_msgs.Pose;
import geometry_msgs.Quaternion;
import rosgraph_msgs.Clock;
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
	private ServiceClient<std_srvs.EmptyRequest, std_srvs.EmptyResponse> resetWorld;
	private ServiceClient<gazebo_msgs.SpawnModelRequest, gazebo_msgs.SpawnModelResponse> spawnSDFModel;
	private ServiceClient<gazebo_msgs.SpawnModelRequest, gazebo_msgs.SpawnModelResponse> spawnURDFModel;
	private ServiceClient<gazebo_msgs.SpawnModelRequest, gazebo_msgs.SpawnModelResponse> spawnGazeboModel;

	
	private Subscriber<rosgraph_msgs.Clock> clock; 

	private volatile boolean running = false;
	private volatile boolean sync = false;
	
	private long millis = 0;
	private long step = 100; // TODO make this configurable?
	
	public Gazebo(ConnectedNode node) throws Exception{
		start = node.newServiceClient("/gazebo/unpause_physics", std_srvs.Empty._TYPE);
		stop = node.newServiceClient("/gazebo/pause_physics",  std_srvs.Empty._TYPE);
		resetWorld = node.newServiceClient("/gazebo/reset_world",  std_srvs.Empty._TYPE);
		spawnSDFModel = node.newServiceClient("/gazebo/spawn_sdf_model", gazebo_msgs.SpawnModel._TYPE);
		spawnURDFModel = node.newServiceClient("/gazebo/spawn_urdf_model", gazebo_msgs.SpawnModel._TYPE);
		spawnGazeboModel = node.newServiceClient("/gazebo/spawn_gazebo_model", gazebo_msgs.SpawnModel._TYPE);

		
		clock = node.newSubscriber("/clock", rosgraph_msgs.Clock._TYPE);
		clock.addMessageListener(new MessageListener<rosgraph_msgs.Clock>() {
			@Override
			public void onNewMessage(Clock c) {
				if(sync && c.getClock().compareTo(new Time((int)(millis / 1000), (int)((millis % 1000)*1000000))) >= 0){
					pause();
					millis = c.getClock().totalNsecs()/1000000;
				}
			}
		});
	}
	
	public synchronized void start(){
		start(false);
	}
	
	@Override
	public synchronized void start(boolean s) {
		running = true;
		sync = s;
		unpause();
	}
	
	private synchronized void unpause(){
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
		running = false;
		final Deferred<Void> deferred = new Deferred<>();
		stop.call(stop.newMessage(), new ServiceResponseListener<EmptyResponse>() {
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
		try {
			deferred.getPromise().getValue();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void tick() throws TimeoutException {
		if(running){
			millis += step;
			unpause();
		}
	}

	@Override
	public void loadScene(String file, Map<String, String> config) {
		// TODO introduce separate spawn_model function in Simulator?!
		// TODO could this load world files similar to what roslaunch does?!
		ServiceClient<SpawnModelRequest, SpawnModelResponse> spawnModel = null;
		if(file.endsWith(".sdf")){
			spawnModel = spawnSDFModel;
		} else if(file.endsWith(".urdf")){
			spawnModel = spawnURDFModel;
		} else {
			spawnModel = spawnGazeboModel;
		}
		
		try {
			final Deferred<Void> deferred = new Deferred<>();
			
			File f = new File(file);
			byte[] bytes = Files.readAllBytes(f.toPath());
			String xml = new String(bytes);
			
			SpawnModelRequest req = spawnModel.newMessage();
			req.setModelName(f.getName());
			req.setModelXml(xml);
			Pose p = req.getInitialPose();
			Point point = p.getPosition();
			Quaternion quaternion = p.getOrientation();
			if(config.containsKey("pose")){
				String pose = config.get("pose");
				String[] numbers = pose.split(",");
				if(numbers.length >= 3){
					point.setX(Double.parseDouble(numbers[0].trim()));
					point.setY(Double.parseDouble(numbers[1].trim()));
					point.setZ(Double.parseDouble(numbers[2].trim()));
				} 
				if(numbers.length == 7){
					quaternion.setW(Double.parseDouble(numbers[3].trim()));
					quaternion.setX(Double.parseDouble(numbers[4].trim()));
					quaternion.setY(Double.parseDouble(numbers[5].trim()));
					quaternion.setZ(Double.parseDouble(numbers[6].trim()));
				}
				p.setPosition(point);
				p.setOrientation(quaternion);
			}			
			req.setInitialPose(p);

			spawnModel.call(req, new ServiceResponseListener<SpawnModelResponse>() {
				@Override
				public void onFailure(RemoteException ex) {
					deferred.fail(ex);
				}

				@Override
				public void onSuccess(SpawnModelResponse resp) {
					if(resp.getSuccess()){
						deferred.resolve(null);
					} else {
						deferred.fail(new Exception(resp.getStatusMessage()));
					}
				}
			});
			
			deferred.getPromise().getValue();
		} catch(Exception e){
			e.printStackTrace();
		}
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
