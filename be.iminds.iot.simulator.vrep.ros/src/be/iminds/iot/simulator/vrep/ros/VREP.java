package be.iminds.iot.simulator.vrep.ros;

import java.io.File;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.ros.exception.RemoteException;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeMain;
import org.ros.node.service.ServiceClient;
import org.ros.node.service.ServiceResponseListener;

import be.iminds.iot.simulator.api.Simulator;
import be.iminds.iot.simulator.vrep.ros.youbot.VREPYoubot;
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
import vrep_common.simRosSynchronous;
import vrep_common.simRosSynchronousRequest;
import vrep_common.simRosSynchronousResponse;
import vrep_common.simRosSynchronousTrigger;
import vrep_common.simRosSynchronousTriggerRequest;
import vrep_common.simRosSynchronousTriggerResponse;

@Component(service = {NodeMain.class, Simulator.class},
property = {"osgi.command.scope=vrep", 
	"osgi.command.function=start",
	"osgi.command.function=pause",
	"osgi.command.function=stop",
	"osgi.command.function=tick",
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
	private ServiceClient<simRosSynchronousRequest, simRosSynchronousResponse> syncSim;
	private ServiceClient<simRosSynchronousTriggerRequest, simRosSynchronousTriggerResponse> triggerSim;
	private ServiceClient<simRosLoadSceneRequest, simRosLoadSceneResponse> loadScene;
	
	private ConnectedNode node;
	private VREPYoubot youbot;
	
	private String scene = null;

	// in case we have to start vrep ourselves
	private Process process;
	private String dir = "/opt/vrep";
	
	@Activate
	void activate(BundleContext context){
		scene = context.getProperty("vrep.scene");
		
		String d = context.getProperty("vrep.dir");
		if(d!=null){
			dir = d;
		}
		
		File file = new File(dir);
		if(!file.exists() || !file.isDirectory()){
			dir = null;
		}
	}
	
	@Deactivate
	void deactivate(){
		if(process!=null){
			process.destroy();
		}
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
				syncSim = connectedNode.newServiceClient("/vrep/simRosSynchronous", simRosSynchronous._TYPE);
				triggerSim = connectedNode.newServiceClient("/vrep/simRosSynchronousTrigger", simRosSynchronousTrigger._TYPE);

				loadScene = connectedNode.newServiceClient("/vrep/simRosLoadScene", simRosLoadScene._TYPE);
				
				if(scene!=null){
					loadScene(scene);
				} else {
					load();
				}
			} catch(Exception e){
				if(process==null && dir!=null){
					// try to launch vrep ourselves
					try {
						// add vrep dir to LD_LIBRARY_PATH and start vrep executable
						File file = new File(dir);
						ProcessBuilder builder = new ProcessBuilder(file.getAbsolutePath()+File.separator+"vrep");
						builder.environment().put("LD_LIBRARY_PATH", builder.environment().get("LD_LIBRARY_PATH")+":"+file.getAbsolutePath());
						builder.inheritIO();
						process = builder.start();
					} catch(Exception ex){
						System.err.println("Error launching VREP ");					}
				}
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

	public void start(){
		start(false);
	}
	
	@Override
	public void start(boolean sync) {
		if(sync){
			setSyncMode(true);
		} else {
			setSyncMode(false);
		}
		
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
	
	private void setSyncMode(boolean set){
		final simRosSynchronousRequest request = syncSim.newMessage();
		request.setEnable((byte) (set ? 1 : 0));
		syncSim.call(request, new ServiceResponseListener<simRosSynchronousResponse>() {
			@Override
			public void onSuccess(simRosSynchronousResponse response) {}
			@Override
			public void onFailure(RemoteException e) {
				System.err.println("Error starting simulation");
			}
		});
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
	public void tick() {
		final simRosSynchronousTriggerRequest request = triggerSim.newMessage();
		triggerSim.call(request, new ServiceResponseListener<simRosSynchronousTriggerResponse>() {
			@Override
			public void onSuccess(simRosSynchronousTriggerResponse response) {}
			@Override
			public void onFailure(RemoteException e) {
				System.err.println("Error pausing simulation");
			}
		});
	}

	@Override
	public void stop() {
		setSyncMode(false);
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
		youbot = new VREPYoubot(node, 
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
