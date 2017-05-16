package be.iminds.iot.robot.erlerover.ros;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.Promise;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;

import be.iminds.iot.robot.api.rover.Rover;
import mavros_msgs.OverrideRCIn;

public class RoverImpl implements Rover {

	private final String name;
	private final BundleContext context;
	private ServiceRegistration<Rover> registration;
	
	private final ConnectedNode node;
	
	private Publisher<mavros_msgs.OverrideRCIn> pRC;
	
	private Deferred<Rover> deferred = null;
	private Timer timer = new Timer();

	
	public RoverImpl(String name, BundleContext context,
			ConnectedNode node){
		this.name = name;
		this.context = context;
		this.node = node;
		
		// TODO expose odometry/IMU information?
		
	}
	
	public void register() throws Exception{
		pRC = node.newPublisher("/mavros/rc/override", mavros_msgs.OverrideRCIn._TYPE);
				
		Dictionary<String, Object> properties = new Hashtable<>();
		properties.put("name", name);
		registration = 	context.registerService(Rover.class, RoverImpl.this, properties);
	}
	
	public void unregister(){
		if(registration != null){
			registration.unregister();
		}
		
		pRC.shutdown();
	}

	@Override
	public Promise<Rover> move(float throttle, float yaw) {
		if(deferred!=null){
			deferred.fail(new Exception("Operation interrupted!"));
			deferred = null;
		}
		// will be resolved immediately
		Deferred<Rover> d = new Deferred<>();

		OverrideRCIn cmd = pRC.newMessage();
		short[] channels = cmd.getChannels();
		
		// convert -1 .. 1 to values ranging between 1100 and 1900
		short t = (short)(1500+throttle*400);
		short y = (short)(1500+yaw*400);
		
		channels[0] = y;
		channels[2] = t;
		
		cmd.setChannels(channels);
		pRC.publish(cmd);
		
		// resolve immediately
		d.resolve(RoverImpl.this);
		return d.getPromise();
		
	}
	
	@Override
	public synchronized Promise<Rover> waitFor(long time) {
		if(deferred!=null){
			deferred.fail(new Exception("Operation interrupted!"));
		}
		deferred = new Deferred<Rover>();

		timer.schedule(new ResolveTask(deferred), time);
	
		return deferred.getPromise();
	}

	@Override
	public Promise<Rover> stop() {
		return move(0, 0);
	}
	
	private class ResolveTask extends TimerTask {
		
		private Deferred<Rover> deferred;
		
		public ResolveTask(Deferred<Rover> deferred){
			this.deferred = deferred;
		}
		
		@Override
		public void run() {
			if(deferred == RoverImpl.this.deferred){
				synchronized(RoverImpl.this){
					RoverImpl.this.deferred = null;
				}
			}
				
			try {
				deferred.resolve(RoverImpl.this);
			} catch(IllegalStateException e){
				// ignore if already resolved
			}
		}
	}
}
