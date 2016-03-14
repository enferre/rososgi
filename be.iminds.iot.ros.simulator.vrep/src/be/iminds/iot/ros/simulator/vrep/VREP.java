package be.iminds.iot.ros.simulator.vrep;

import java.io.File;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.ros.exception.RemoteException;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeMain;
import org.ros.node.service.ServiceClient;
import org.ros.node.service.ServiceResponseListener;

import be.iminds.iot.ros.simulator.vrep.youbot.Youbot;
import be.iminds.iot.simulator.api.Simulator;
import vrep_common.simRosLoadScene;
import vrep_common.simRosLoadSceneRequest;
import vrep_common.simRosLoadSceneResponse;
import vrep_common.simRosPauseSimulation;
import vrep_common.simRosPauseSimulationRequest;
import vrep_common.simRosPauseSimulationResponse;
import vrep_common.simRosStartSimulation;
import vrep_common.simRosStartSimulationRequest;
import vrep_common.simRosStartSimulationResponse;
import vrep_common.simRosStopSimulation;
import vrep_common.simRosStopSimulationRequest;
import vrep_common.simRosStopSimulationResponse;

@Component(service = {NodeMain.class, Simulator.class},
property = {"osgi.command.scope=vrep", 
	"osgi.command.function=start",
	"osgi.command.function=pause",
	"osgi.command.function=stop",
	"osgi.command.function=loadScene",
	"osgi.command.function=position",
	"osgi.command.function=targetPosition",
	"osgi.command.function=targetVelocity",
	"osgi.command.function=torque",
	"osgi.command.function=move",
	"osgi.command.function=open",
	"osgi.command.function=close"})
public class VREP extends AbstractNodeMain implements Simulator {

	// service object for each ROS service used
	private ServiceClient<simRosStartSimulationRequest, simRosStartSimulationResponse> startSim;
	private ServiceClient<simRosStopSimulationRequest, simRosStopSimulationResponse> stopSim;
	private ServiceClient<simRosPauseSimulationRequest, simRosPauseSimulationResponse> pauseSim;
	private ServiceClient<simRosLoadSceneRequest, simRosLoadSceneResponse> loadScene;
	
	private ConnectedNode node;
	private Youbot youbot;
	
	private String scene = null;
	
	@Activate
	void activate(BundleContext context){
		scene = context.getProperty("vrep.scene");
	}
	
	@Override
	public GraphName getDefaultNodeName() {
		return GraphName.of("vrep/simulator");
	}
	
	@Override
	public void onStart(ConnectedNode connectedNode){
		node = connectedNode;
	
		int tries = 0;
		while(startSim==null && tries < 2){
			try {
				startSim = connectedNode.newServiceClient("/vrep/simRosStartSimulation", simRosStartSimulation._TYPE);
				stopSim = connectedNode.newServiceClient("/vrep/simRosStopSimulation", simRosStopSimulation._TYPE);
				pauseSim = connectedNode.newServiceClient("/vrep/simRosPauseSimulation", simRosPauseSimulation._TYPE);
				loadScene = connectedNode.newServiceClient("/vrep/simRosLoadScene", simRosLoadScene._TYPE);
				
				if(scene!=null){
					loadScene(scene);
				} else {
					load();
				}
			} catch(Exception e){
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e1) {
				}
				tries++;
				if(tries == 2){
					System.err.println("Failed to instantiate VREP ROS interface. Check if VREP is running with ROS plugin enabled");
				}
			}
		}
	}

	@Override
	public void start() {
		final simRosStartSimulationRequest request = startSim.newMessage();
		startSim.call(request, new ServiceResponseListener<simRosStartSimulationResponse>() {
			@Override
			public void onSuccess(simRosStartSimulationResponse response) {}
			@Override
			public void onFailure(RemoteException e) {
				System.err.println("Error starting simulation");
			}
		});
		
		enable();
	}

	@Override
	public void pause() {
		final simRosPauseSimulationRequest request = pauseSim.newMessage();
		pauseSim.call(request, new ServiceResponseListener<simRosPauseSimulationResponse>() {
			@Override
			public void onSuccess(simRosPauseSimulationResponse response) {}
			@Override
			public void onFailure(RemoteException e) {
				System.err.println("Error pausing simulation");
			}
		});
		
		disable();
	}

	@Override
	public void stop() {
		final simRosStopSimulationRequest request = stopSim.newMessage();
		stopSim.call(request, new ServiceResponseListener<simRosStopSimulationResponse>() {
			@Override
			public void onSuccess(simRosStopSimulationResponse response) {}
			@Override
			public void onFailure(RemoteException e) {
				System.err.println("Error stopping simulation");
			}
		});
		
		disable();
	}

	@Override
	public void loadScene(String file) {
		File f = new File(file);
		final simRosLoadSceneRequest request = loadScene.newMessage();
		request.setFileName(f.getAbsolutePath());
		loadScene.call(request, new ServiceResponseListener<simRosLoadSceneResponse>() {
			@Override
			public void onSuccess(simRosLoadSceneResponse response) {
				try {
					load();
				} catch (Exception e) {
					System.err.println("Failed to load youbot");
				}
			}
			@Override
			public void onFailure(RemoteException e) {
				System.err.println("Error loading scene "+file);
			}
		});
	}
	
	/**
	 * Load objects  
	 */
	private void load() throws Exception {
		
		// TODO search for youbot objects?
		youbot = new Youbot(node, 
				"youBot",
				"youBotArmJoint0",
				"youBotArmJoint1",
				"youBotArmJoint2",
				"youBotArmJoint3",
				"youBotArmJoint4",
				"youBotGripperJoint1",
				"youBotGripperJoint2",
				"rollingJoint_fl",
				"rollingJoint_rl",
				"rollingJoint_rr",
				"rollingJoint_fr");
	}
	
	/**
	 * Enable objects - register their pub/sub ROS topics
	 */
	private void enable(){
		youbot.enable();
	}
	
	/**
	 * Disable objects - unregister ROS stuff
	 */
	private void disable(){
		youbot.disable();
	}
	
	
	
	// For testing via CLI
	
	public void position(int joint, double p){
		youbot.arm().setPosition(joint, p);
	}
	
	public void targetPosition(int joint, double p){
		youbot.arm().setTargetPosition(joint, p);
	}
	
	public void targetVelocity(int joint, double v){
		youbot.arm().setTargetVelocity(joint, v);
	}
	
	public void torque(int joint, double t){
		youbot.arm().setTorque(joint, t);
	}
	
	public void move(double x, double y, double a){
		youbot.base().move(x, y, a);
	}
	
	public void open(){
		youbot.arm().openGripper();
	}
	
	public void close(){
		youbot.arm().closeGripper();
	}
}

