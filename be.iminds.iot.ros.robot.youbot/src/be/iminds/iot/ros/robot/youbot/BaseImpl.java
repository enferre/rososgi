package be.iminds.iot.ros.robot.youbot;

import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;

import org.osgi.framework.BundleContext;
import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.Promise;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;

import be.iminds.iot.robot.api.JointValue;
import be.iminds.iot.robot.api.OmniDirectional;
import geometry_msgs.Twist;

public class BaseImpl implements OmniDirectional {

	private final Publisher<geometry_msgs.Twist> pTwist;
	
	private Deferred<Void> deferred = null;
	private Timer timer = new Timer();

	
	public BaseImpl(BundleContext context,
			ConnectedNode node){
		this.pTwist = node.newPublisher("/cmd_vel", geometry_msgs.Twist._TYPE);
		
		// TODO separate service for wheel joints?
		// can you even control those individually using ROS interface?
		
		// TODO expose odometry information?
		
		context.registerService(OmniDirectional.class, this, null);
	}

	@Override
	public Promise<Void> setVelocities(Collection<JointValue> velocities) {
		// TODO is this even possible with current ROS interface?
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized Promise<Void> move(float vx, float vy, float va) {
		if(deferred!=null){
			deferred.fail(new Exception("Operation interrupted!"));
			deferred = null;
		}
		// will be resolved immediately
		Deferred<Void> d = new Deferred<>();

		Twist cmd = pTwist.newMessage();
		
		cmd.getLinear().setX(vx);
		cmd.getLinear().setY(vy);
		cmd.getLinear().setZ(0);
		
		cmd.getAngular().setX(0);
		cmd.getAngular().setY(0);
		cmd.getAngular().setZ(va);

		pTwist.publish(cmd);
		
		// resolve immediately
		d.resolve(null);
		return d.getPromise();
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

	@Override
	public Promise<Void> stop() {
		return move(0, 0, 0);
	}
	
	private class ResolveTask extends TimerTask {
		
		private Deferred<Void> deferred;
		
		public ResolveTask(Deferred<Void> deferred){
			this.deferred = deferred;
		}
		
		@Override
		public void run() {
			if(deferred == BaseImpl.this.deferred){
				synchronized(BaseImpl.this){
					BaseImpl.this.deferred = null;
				}
			}
				
			try {
				deferred.resolve(null);
			} catch(IllegalStateException e){
				// ignore if already resolved
			}
		}
	}
}
