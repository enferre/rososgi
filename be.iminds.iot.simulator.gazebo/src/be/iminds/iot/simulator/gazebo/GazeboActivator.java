package be.iminds.iot.simulator.gazebo;

import java.util.Hashtable;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeMain;

import be.iminds.iot.simulator.api.Simulator;

/**
 * Check and connect to external Gazebo via ROS.
 * 
 * @author tverbele
 *
 */
@Component(service = {NodeMain.class})
public class GazeboActivator extends AbstractNodeMain{

	private BundleContext context;
	private ServiceRegistration<Simulator> reg;

	@Activate
	void activate(BundleContext context, Map<String, Object> config){
		this.context = context;
	}
	
	@Override
	public GraphName getDefaultNodeName() {
		return GraphName.of("gazebo/activator");
	}
	
	@Override
	public void onStart(ConnectedNode connectedNode){
		long start = System.currentTimeMillis();
		long timeout = 60000;
		while(reg == null && System.currentTimeMillis()-start < timeout){
			try {
				Gazebo gazebo = new Gazebo(connectedNode);
				gazebo.stop();
				
				Hashtable<String, Object> properties = new Hashtable<String, Object>();
				properties.put("osgi.command.scope", "gazebo");
				properties.put("osgi.command.function", new String[]{
						"start",
						"pause",
						"stop",
						"tick",
						"loadScene",
						"getPosition",
						"setPosition",
						"getOrientation",
						"setOrientation",
						"checkCollisions"		
				});
				reg = context.registerService(Simulator.class, gazebo, properties);
			} catch(Exception e){
				//e.printStackTrace();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
				}
			}
		}
		if(reg == null){
			System.out.println("Failed to connect to Gazebo!");
		}
	}
	
	@Override
	public void onShutdown(Node node) {
		if(reg != null){
			reg.unregister();
		}
	}
}
