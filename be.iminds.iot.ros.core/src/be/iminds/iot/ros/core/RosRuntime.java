package be.iminds.iot.ros.core;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.ros.node.DefaultNodeMainExecutor;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMain;
import org.ros.node.NodeMainExecutor;

import be.iminds.iot.ros.api.Environment;

@Component
public class RosRuntime {

	private NodeMainExecutor executor = DefaultNodeMainExecutor.newDefault();
	
	private Environment env;
	
	@Reference
	void setEnvironment(Environment e){
		this.env = e;
	}
	
	@Reference(cardinality=ReferenceCardinality.MULTIPLE, 
			   policy=ReferencePolicy.DYNAMIC)
	void addNode(NodeMain node) {
		try {
			NodeConfiguration nodeConfiguration = NodeConfiguration.newPublic(env.getHost(), env.getMasterURI());
			nodeConfiguration.setRosRoot(env.getRoot());
			nodeConfiguration.setRosPackagePath(env.getPackagePath());
			executor.execute(node, nodeConfiguration);
		} catch(Throwable e){
			e.printStackTrace();
		}
	}
	
	void removeNode(NodeMain node){
		executor.shutdownNodeMain(node);
	}
}
