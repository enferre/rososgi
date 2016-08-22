package be.iminds.iot.robot.youbot.ros;

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
	name="be.iminds.iot.robot.youbot.ros.Youbot",
	configurationPolicy=ConfigurationPolicy.REQUIRE)
public class YoubotRosController extends AbstractNodeMain {

	private ArmImpl arm;
	private BaseImpl base;
	
	private BundleContext context;
	
	@Activate
	void activate(BundleContext context, Map<String, Object> config){
		this.context = context;
	}
	
	@Override
	public GraphName getDefaultNodeName() {
		return GraphName.of("youbot/controller");
	}
	
	@Override
	public void onStart(ConnectedNode connectedNode){
		connectedNode.getTopicMessageFactory();

		// this brings online arm and base services
		arm = new ArmImpl(context, connectedNode);
		arm.register();
		
		base = new BaseImpl(context, connectedNode);
		base.register();
	}
	
	@Override
	public void onShutdown(Node node) {
		try {
			base.stop();
			arm.reset().getValue();
		} catch(Exception e){}
		
		arm.unregister();
		base.unregister();
	}

}
