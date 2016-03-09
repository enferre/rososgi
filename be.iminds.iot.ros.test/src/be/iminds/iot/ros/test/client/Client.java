package be.iminds.iot.ros.test.client;

import org.osgi.service.component.annotations.Component;
import org.ros.exception.RemoteException;
import org.ros.exception.RosRuntimeException;
import org.ros.exception.ServiceNotFoundException;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeMain;
import org.ros.node.service.ServiceClient;
import org.ros.node.service.ServiceResponseListener;

@Component(service = NodeMain.class,
	property = {"osgi.command.scope=test", 
		"osgi.command.function=echo"})
public class Client extends AbstractNodeMain {

	private ServiceClient<test_srv.EchoRequest, test_srv.EchoResponse> serviceClient;

	@Override
	public GraphName getDefaultNodeName() {
		return GraphName.of("test/client");
	}

	@Override
	public void onStart(ConnectedNode connectedNode) {
		int tries = 0;
		while(serviceClient==null){
			try {
				serviceClient = connectedNode.newServiceClient("echo", test_srv.Echo._TYPE);
			} catch (ServiceNotFoundException e) {
				// try a couple of times ... service could not yet be registered when this node is started...
				tries++;
				if(tries < 5){
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
					}
				} else {
					throw new RosRuntimeException(e);
				}
			}
		}
	}

	public void echo(String str) {
		final test_srv.EchoRequest request = serviceClient.newMessage();
		request.setData(str);
		serviceClient.call(request, new ServiceResponseListener<test_srv.EchoResponse>() {
			@Override
			public void onSuccess(test_srv.EchoResponse response) {
				System.out.println(response.getData());
			}

			@Override
			public void onFailure(RemoteException e) {
				System.out.println("Error: " + e.getMessage());
			}
		});
	}
}
