package be.iminds.iot.ros.turtlesim.output;

import org.osgi.service.component.annotations.Component;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeMain;
import org.ros.node.topic.Subscriber;

import turtlesim.Pose;

@Component(service = NodeMain.class)
public class TurtleSimOutput extends AbstractNodeMain {

	@Override
	public GraphName getDefaultNodeName() {
		return GraphName.of("turtlesim_output");
	}

	@Override
	public void onStart(ConnectedNode connectedNode) {
		Subscriber<Pose> subscriber = connectedNode.newSubscriber("turtle1/pose",
				Pose._TYPE);
		subscriber.addMessageListener(new MessageListener<Pose>() {
			@Override
			public void onNewMessage(Pose pose) {
				StringBuilder builder = new StringBuilder();
				builder.append("Pose").append("\n");
				builder.append(" x: ").append(pose.getX()).append("\n");
				builder.append(" y: ").append(pose.getY()).append("\n");
				builder.append(" theta: ").append(pose.getTheta()).append("\n");
				builder.append(" v_l: ").append(pose.getLinearVelocity()).append("\n");
				builder.append(" v_a: ").append(pose.getAngularVelocity()).append("\n");
				System.out.println(builder.toString());
			}
		});
		
		Subscriber<geometry_msgs.Twist> subscriber2 = connectedNode.newSubscriber("turtle1/cmd_vel",
				geometry_msgs.Twist._TYPE);
		subscriber2.addMessageListener(new MessageListener<geometry_msgs.Twist>() {
			@Override
			public void onNewMessage(geometry_msgs.Twist twist) {
				StringBuilder builder = new StringBuilder();
				builder.append("Velocity Cmd").append("\n");
				builder.append(" linear: ")
					.append("[").append(twist.getLinear().getX())
					.append(", ").append(twist.getLinear().getY())
					.append(", ").append(twist.getLinear().getZ())
					.append("] \n");
				builder.append(" angular: ")
				.append("[").append(twist.getAngular().getX())
				.append(", ").append(twist.getAngular().getY())
				.append(", ").append(twist.getAngular().getZ())
				.append("] \n");
				System.out.println(builder.toString());
			}
		});
	}
}