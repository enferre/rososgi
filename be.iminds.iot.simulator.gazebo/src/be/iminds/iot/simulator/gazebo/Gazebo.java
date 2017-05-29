package be.iminds.iot.simulator.gazebo;

import java.io.File;
import java.nio.file.Files;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.Promise;
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
import gazebo_msgs.DeleteModelRequest;
import gazebo_msgs.DeleteModelResponse;
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
	private ServiceClient<gazebo_msgs.DeleteModelRequest, gazebo_msgs.DeleteModelResponse> deleteModel;

	
	private Subscriber<rosgraph_msgs.Clock> clock; 

	private volatile boolean running = false;
	private volatile boolean sync = false;
	
	private long millis = 0;
	private long step = 100; // TODO make this configurable?
	
	private volatile String scene = null;
	
	public Gazebo(ConnectedNode node) throws Exception{
		start = node.newServiceClient("/gazebo/unpause_physics", std_srvs.Empty._TYPE);
		stop = node.newServiceClient("/gazebo/pause_physics",  std_srvs.Empty._TYPE);
		resetWorld = node.newServiceClient("/gazebo/reset_world",  std_srvs.Empty._TYPE);
		spawnSDFModel = node.newServiceClient("/gazebo/spawn_sdf_model", gazebo_msgs.SpawnModel._TYPE);
		spawnURDFModel = node.newServiceClient("/gazebo/spawn_urdf_model", gazebo_msgs.SpawnModel._TYPE);
		spawnGazeboModel = node.newServiceClient("/gazebo/spawn_gazebo_model", gazebo_msgs.SpawnModel._TYPE);
		deleteModel = node.newServiceClient("/gazebo/delete_model", gazebo_msgs.DeleteModel._TYPE);

		
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
		// in case of gazebo we see the "scene" as a single model that can be loaded
		// if we load a new scene, we first delete the previous "scene" 
		
		if(scene != null){
			try {
				deleteModel(scene).getValue();
			} catch (Exception e) {
			}
		}
		
		Position position = null;
		Orientation orientation = null;
	
		if(config.containsKey("pose")){
			String pose = config.get("pose");
			String[] numbers = pose.split(",");
			if(numbers.length >= 3){
				position = new Position();
				position.x = Float.parseFloat(numbers[0].trim());
				position.y = Float.parseFloat(numbers[1].trim());
				position.z = Float.parseFloat(numbers[2].trim());
			} 
			if(numbers.length == 6){
				orientation = new Orientation();
				orientation.alfa = Float.parseFloat(numbers[3].trim());
				orientation.beta = Float.parseFloat(numbers[4].trim());
				orientation.gamma = Float.parseFloat(numbers[5].trim());
			}
		}			
			
		try {
			spawnModel(file, position, orientation).getValue();
		} catch (Exception e) {
		}
	
	}
	
	private Promise<Void> spawnModel(String file, Position position, Orientation orientation){
		ServiceClient<SpawnModelRequest, SpawnModelResponse> spawnModel = null;
		if(file.endsWith(".sdf")){
			spawnModel = spawnSDFModel;
		} else if(file.endsWith(".urdf")){
			spawnModel = spawnURDFModel;
		} else {
			spawnModel = spawnGazeboModel;
		}

		final Deferred<Void> deferred = new Deferred<>();
			
		try {
			File f = new File(file);
			byte[] bytes = Files.readAllBytes(f.toPath());
			String xml = new String(bytes);
			
			SpawnModelRequest req = spawnModel.newMessage();
			final String name = f.getName();
			req.setModelName(name);
			req.setModelXml(xml);
			Pose p = req.getInitialPose();

			if(position != null){
				Point point = p.getPosition();
				point.setX(position.x);
				point.setY(position.y);
				point.setZ(position.z);
				p.setPosition(point);
			}
			
			if(orientation != null){
				Quaternion q = p.getOrientation();
				// Converting euler angles to quaternion
				// Assuming euler angles in radians!
				double c1 = Math.cos(orientation.alfa/2);
				double s1 = Math.sin(orientation.alfa/2);
				double c2 = Math.cos(orientation.beta/2);
				double s2 = Math.sin(orientation.beta/2);
				double c3 = Math.cos(orientation.gamma/2);
				double s3 = Math.sin(orientation.gamma/2);
				double c1c2 = c1*c2;
				double s1s2 = s1*s2;
				double w =c1c2*c3 - s1s2*s3;
				double x =c1c2*s3 + s1s2*c3;
				double y =s1*c2*c3 + c1*s2*s3;
				double z =c1*s2*c3 - s1*c2*s3;
				q.setW(w);
				q.setX(x);
				q.setY(y);
				q.setZ(z);
				p.setOrientation(q);
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
						Gazebo.this.scene = name;
						deferred.resolve(null);
					} else {
						deferred.fail(new Exception(resp.getStatusMessage()));
					}
				}
			});
		} catch(Exception e){
			deferred.fail(e);
		}
		
		return deferred.getPromise();
	}
	
	private Promise<Void> deleteModel(String modelName){
		final Deferred<Void> deferred = new Deferred<>();
		
		DeleteModelRequest req = deleteModel.newMessage();
		req.setModelName(modelName);
		
		deleteModel.call(req, new ServiceResponseListener<DeleteModelResponse>() {
			@Override
			public void onFailure(RemoteException ex) {
				deferred.fail(ex);
			}

			@Override
			public void onSuccess(DeleteModelResponse resp) {
				if(resp.getSuccess()){
					deferred.resolve(null);
				} else {
					deferred.fail(new Exception(resp.getStatusMessage()));
				}
			}
		});
		
		return deferred.getPromise();
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
