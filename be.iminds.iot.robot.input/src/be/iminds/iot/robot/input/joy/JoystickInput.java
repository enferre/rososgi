package be.iminds.iot.robot.input.joy;

import java.util.Arrays;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeMain;
import org.ros.node.topic.Subscriber;

import be.iminds.iot.robot.api.Arm;
import be.iminds.iot.robot.api.OmniDirectional;
import sensor_msgs.Joy;

@Component(service = NodeMain.class)
public class JoystickInput extends AbstractNodeMain {

	private Arm arm;
	private OmniDirectional base;
	
	private float velocity = 0.2f;
	private float angular = 0.4f;

	private Subscriber<Joy> subscriber;

	private boolean x = false;
	private boolean a = false;
	private boolean b = false;
	private boolean y = false;
	
	@Override
	public GraphName getDefaultNodeName() {
		return GraphName.of("joy/subscriber");
	}

	@Override
	public void onStart(ConnectedNode connectedNode) {
		String topic = "/joy";
		subscriber = connectedNode.newSubscriber(topic,
				Joy._TYPE);
		subscriber.addMessageListener(new MessageListener<Joy>() {
			@Override
			public void onNewMessage(Joy joy) {
				if(!x && joy.getButtons()[0] == 1){
					// grip action
					arm.openGripper()
						.then(p -> arm.setPositions(2.92f, 0.0f, 0.0f, 0.0f, 2.875f))
						.then(p -> arm.setPositions(2.92f, 1.76f, -1.37f, 2.55f))
						.then(p -> arm.closeGripper())
						.then(p -> arm.setPositions(0.01f, 0.8f))
						.then(p -> arm.setPositions(0.01f, 0.8f, -1f, 2.9f))
						.then(p -> arm.openGripper())
						.then(p -> arm.setPosition(1, -1.3f))
						.then(p -> arm.reset());
				}
				
				float vx = joy.getAxes()[1]*velocity;
				float vy = joy.getAxes()[0]*velocity;
				float va = joy.getAxes()[2]*angular;
				
				base.move(vx, vy, va);
				
				
				x = joy.getButtons()[0] == 1;
				a = joy.getButtons()[1] == 1;
				b = joy.getButtons()[2] == 1;
				y = joy.getButtons()[3] == 1;

			}
		});
	}

	@Override
	public void onShutdown(Node node) {
		subscriber.shutdown();
	}
	
	@Reference
	void setArm(Arm arm){
		this.arm = arm;
	}
	
	@Reference
	void setBase(OmniDirectional base){
		this.base = base;
	}

}
