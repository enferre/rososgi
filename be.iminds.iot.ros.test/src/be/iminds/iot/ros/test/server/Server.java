package be.iminds.iot.ros.test.server;

import org.osgi.service.component.annotations.Component;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeMain;
import org.ros.node.service.ServiceResponseBuilder;

@Component(service = NodeMain.class)
public class Server extends AbstractNodeMain {

	@Override
	public GraphName getDefaultNodeName() {
		return GraphName.of("test/server");
	}

	@Override
	public void onStart(ConnectedNode connectedNode) {
		connectedNode.newServiceServer("echo", test_srv.Echo._TYPE,
			new ServiceResponseBuilder<test_srv.EchoRequest, test_srv.EchoResponse>() {
				@Override
				public void build(test_srv.EchoRequest request, test_srv.EchoResponse response) {
					response.setData("Echoed: " + request.getData());
				}
			});
	}
}
