package be.iminds.iot.robot.erlerover.ros;

import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.ros.exception.RemoteException;
import org.ros.exception.ServiceNotFoundException;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeMain;
import org.ros.node.service.ServiceClient;
import org.ros.node.service.ServiceResponseListener;

import mavros_msgs.ParamSetRequest;
import mavros_msgs.ParamSetResponse;
import mavros_msgs.ParamValue;

@Component(service = {NodeMain.class},
	name="be.iminds.iot.robot.erlerover.ros.Rover",
	configurationPolicy=ConfigurationPolicy.REQUIRE)
public class RoverRosController extends AbstractNodeMain {

	private String name;
	private RoverImpl rover;
	
	private BundleContext context;
	private volatile boolean active = false;
	
	@Activate
	void activate(BundleContext context, Map<String, Object> config){
		this.context = context;
		
		name = config.get("name").toString();
		if(name == null){
			name = "Erle Rover";
		}
	}
	
	@Deactivate
	void deactivate(){
		active = false;
	}
	
	@Override
	public GraphName getDefaultNodeName() {
		return GraphName.of("elrerover/controller");
	}
	
	@Override
	public void onStart(ConnectedNode connectedNode){
		active = true;
		ServiceClient<ParamSetRequest, ParamSetResponse> setParam = null;
		while(setParam == null && active){ // TODO add timeout?
			try {
				setParam = connectedNode.newServiceClient("/mavros/param/set", mavros_msgs.ParamSet._TYPE);
			} catch (ServiceNotFoundException e) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
				}
			}
		}
		if(setParam == null)
			return;
		
		// set SYSID_MYGCS to 1 for override control
		final ParamSetRequest request = setParam.newMessage();
		request.setParamId("SYSID_MYGCS");
		ParamValue val = request.getValue();
		val.setInteger(1);
		request.setValue(val);
		setParam.call(request, new ServiceResponseListener<ParamSetResponse>() {
			@Override
			public void onFailure(RemoteException ex) {
				ex.printStackTrace();
			}

			@Override
			public void onSuccess(ParamSetResponse r) {
				// this brings online Rover service
				try {
					rover = new RoverImpl(name, context, connectedNode);
					rover.register();
				} catch(Exception e){
					System.out.println("Failed to bring online Rover service");
					e.printStackTrace();
				}				
			}
		});
	}
	
	@Override
	public void onShutdown(Node node) {
		try {
			rover.stop();
		} catch(Exception e){}
		
		rover.unregister();
	}

}
