package be.iminds.iot.simulator.vrep.ros;

import java.io.File;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeMain;

import be.iminds.iot.simulator.api.Position;
import be.iminds.iot.simulator.api.Simulator;
import be.iminds.iot.simulator.vrep.ros.youbot.VREPYoubot;
import geometry_msgs.Point;

@Component(service = {NodeMain.class, Simulator.class},
property = {"osgi.command.scope=vrep", 
	"osgi.command.function=start",
	"osgi.command.function=pause",
	"osgi.command.function=stop",
	"osgi.command.function=tick",
	"osgi.command.function=loadScene",
	"osgi.command.function=getPosition",
	"osgi.command.function=setPosition",
	"osgi.command.function=jointPosition",
	"osgi.command.function=targetPosition",
	"osgi.command.function=targetVelocity",
	"osgi.command.function=torque",
	"osgi.command.function=move",
	"osgi.command.function=open",
	"osgi.command.function=close"})
public class VREP extends AbstractNodeMain implements Simulator {

	private ConnectedNode node;
	private VREPInterface vrep;
	
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
		while(vrep==null && tries < 2){
			try {
				vrep = new VREPInterface(connectedNode);
				
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
		vrep.setSynchronous(sync);
		
		vrep.startSimuation();
		
		enable();
	}
	
	@Override
	public void pause() {
		vrep.pauseSimulation();
		
		disable();
	}
	
	@Override
	public void tick() {
		vrep.trigger();
	}

	@Override
	public void stop() {
		vrep.setSynchronous(false);
		
		vrep.stopSimulation();
		
		disable();
	}

	@Override
	public void loadScene(String file) {
		vrep.loadScene(file);
		
		try {
			load();
		} catch (Exception e) {
		}
	}
	
	@Override
	public Position getPosition(String object) {
		Point p = vrep.getPosition(vrep.getObjectHandle(object), -1);
		return new Position(p.getX(), p.getY(), p.getZ());
	}

	public void setPosition(String object, double x, double y, double z) {
		vrep.setPosition(vrep.getObjectHandle(object), -1, x, y, z);
	}
	
	@Override
	public void setPosition(String object, Position p) {
		vrep.setPosition(vrep.getObjectHandle(object), -1, p.x, p.y, p.z);
	}

	@Override
	public Position getPosition(String object, String relativeTo) {
		Point p = vrep.getPosition(vrep.getObjectHandle(object), vrep.getObjectHandle(relativeTo));
		return new Position(p.getX(), p.getY(), p.getZ());
	}

	@Override
	public void setPosition(String object, String relativeTo, Position p) {
		vrep.setPosition(vrep.getObjectHandle(object), vrep.getObjectHandle(relativeTo), p.x, p.y, p.z);		
	}
	
	public void setPosition(String object, String relativeTo, double x, double y, double z) {
		vrep.setPosition(vrep.getObjectHandle(object), vrep.getObjectHandle(relativeTo), x, y, z);
	}
	
	/**
	 * Load objects  
	 */
	private void load() throws Exception {
		
		// TODO search for youbot objects?
		youbot = new VREPYoubot(node, vrep, 
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
	
	public void jointPosition(int joint, double p){
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

