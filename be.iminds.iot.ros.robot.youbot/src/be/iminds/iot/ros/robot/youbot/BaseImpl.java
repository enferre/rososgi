package be.iminds.iot.ros.robot.youbot;

import java.util.Collection;
import java.util.Map;

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
	public Promise<Void> move(float vx, float vy, float va) {
		Deferred<Void> deferred = new Deferred<>();

		Twist cmd = pTwist.newMessage();
		
		cmd.getLinear().setX(vx);
		cmd.getLinear().setY(vy);
		cmd.getLinear().setZ(0);
		
		cmd.getAngular().setX(0);
		cmd.getAngular().setY(0);
		cmd.getAngular().setZ(va);

		pTwist.publish(cmd);
		
		// immediately resolve?
		deferred.resolve(null);
		return deferred.getPromise();
	}

	@Override
	public void stop() {
		move(0, 0, 0);
	}
}
