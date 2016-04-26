package be.iminds.iot.robot.youbot.ros;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeMain;

import be.iminds.iot.robot.api.Arm;
import be.iminds.iot.robot.api.OmniDirectional;

@Component(service = {NodeMain.class})
public class YoubotRosController extends AbstractNodeMain {

	private Arm arm;
	private OmniDirectional base;
	
	private BundleContext context;
	
	@Activate
	void activate(BundleContext context){
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
		base = new BaseImpl(context, connectedNode);
	}
	
	@Override
	public void onShutdown(Node node) {
		try {
			base.stop();
			arm.reset().getValue();
		} catch(Exception e){}
	}

}
