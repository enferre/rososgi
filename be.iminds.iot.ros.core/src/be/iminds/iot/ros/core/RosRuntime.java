package be.iminds.iot.ros.core;

import java.net.URI;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.ros.node.DefaultNodeMainExecutor;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMain;
import org.ros.node.NodeMainExecutor;

@Component
public class RosRuntime {

	private NodeMainExecutor executor = DefaultNodeMainExecutor.newDefault();
	
	@Reference(cardinality=ReferenceCardinality.MULTIPLE, 
			   policy=ReferencePolicy.DYNAMIC)
	void addNode(NodeMain node) {
		System.out.println("START NODE "+node.getDefaultNodeName());
		try {
			NodeConfiguration nodeConfiguration = NodeConfiguration.newPublic("localhost",new URI("http://localhost:11311"));
			executor.execute(node, nodeConfiguration);
		} catch(Throwable e){
			e.printStackTrace();
		}
	}
	
	void removeNode(NodeMain node){
		executor.shutdownNodeMain(node);
	}
}
