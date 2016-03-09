package be.iminds.iot.ros.test.sub;

import org.osgi.service.component.annotations.Component;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeMain;
import org.ros.node.topic.Subscriber;

@Component(service = NodeMain.class)
public class Sub extends AbstractNodeMain {

	@Override
	public GraphName getDefaultNodeName() {
		return GraphName.of("test/subscriber");
	}

	@Override
	public void onStart(ConnectedNode connectedNode) {
		Subscriber<std_msgs.String> subscriber = connectedNode.newSubscriber("testtopic", std_msgs.String._TYPE);
		subscriber.addMessageListener(new MessageListener<std_msgs.String>() {
			@Override
			public void onNewMessage(std_msgs.String message) {
				System.out.println("Sub: \"" + message.getData() + "\"");
			}
		});
	}
}
