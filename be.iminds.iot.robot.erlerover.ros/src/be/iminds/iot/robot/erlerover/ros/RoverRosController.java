package be.iminds.iot.robot.erlerover.ros;

import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeMain;

@Component(service = {NodeMain.class},
	name="be.iminds.iot.robot.erlerover.ros.Rover",
	configurationPolicy=ConfigurationPolicy.REQUIRE)
public class RoverRosController extends AbstractNodeMain {

	private String name;
	private RoverImpl rover;
	
	private BundleContext context;
	
	@Activate
	void activate(BundleContext context, Map<String, Object> config){
		this.context = context;
		
		name = config.get("name").toString();
		if(name == null){
			name = "Erle Rover";
		}
		
	}
	
	@Override
	public GraphName getDefaultNodeName() {
		return GraphName.of("elrerover/controller");
	}
	
	@Override
	public void onStart(ConnectedNode connectedNode){
		connectedNode.getTopicMessageFactory();

		// this brings online Rover service
		rover = new RoverImpl(name, context, connectedNode);
		rover.register();
	}
	
	@Override
	public void onShutdown(Node node) {
		try {
			rover.stop();
		} catch(Exception e){}
		
		rover.unregister();
	}

}
