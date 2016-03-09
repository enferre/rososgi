package be.iminds.iot.ros.test.pub;

import org.osgi.service.component.annotations.Component;
import org.ros.concurrent.CancellableLoop;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeMain;
import org.ros.node.topic.Publisher;

@Component(service = NodeMain.class)
public class Pub extends AbstractNodeMain {
	private String topic_name = "testtopic";

	@Override
	public GraphName getDefaultNodeName() {
		return GraphName.of("test/publisher");
	}

	@Override
	public void onStart(final ConnectedNode connectedNode) {
		final Publisher<std_msgs.String> publisher = connectedNode.newPublisher(topic_name, std_msgs.String._TYPE);
		connectedNode.executeCancellableLoop(new CancellableLoop() {
			@Override
			protected void setup() {
				// setup
			}

			@Override
			protected void loop() throws InterruptedException {
				std_msgs.String str = publisher.newMessage();
				str.setData("Publish Test message every 10s!");
				publisher.publish(str);
				Thread.sleep(10000);
			}
		});
	}
}
