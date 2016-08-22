package be.iminds.iot.simulator.vrep;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import be.iminds.iot.simulator.api.Position;
import be.iminds.iot.simulator.api.Simulator;
import coppelia.FloatWA;
import coppelia.IntWA;
import coppelia.StringWA;
import coppelia.remoteApi;

@Component(
		immediate=true,
		property = {"osgi.command.scope=vrep", 
		"osgi.command.function=start",
		"osgi.command.function=pause",
		"osgi.command.function=stop",
		"osgi.command.function=tick",
		"osgi.command.function=loadScene",
		"osgi.command.function=getPosition",
		"osgi.command.function=setPosition"})
public class VREP implements Simulator {
	
	private remoteApi vrep; 
	private int clientID;
	
	private Map<String, Integer> objectHandles = new HashMap<>();
	
	private Process process;
	private String dir = "/opt/vrep-3.3.1";
	
	private List<Configuration> configurations = new ArrayList<>();
	private ConfigurationAdmin ca;
	
	@Reference
	void setConfigurationAdmin(ConfigurationAdmin ca){
		this.ca = ca;
	}
	
	@Activate
	void activate(BundleContext context) throws Exception {
		vrep = new remoteApi();
		vrep.simxFinish(-1); // just in case, close all opened connections
		clientID = vrep.simxStart("127.0.0.1",19997,true,true, 100,5);
		if(clientID == -1){
			// no vrep running ... try to launch a local vrep process
			String d = context.getProperty("vrep.dir");
			if(d!=null){
				dir = d;
			}
			
			try {
				// add vrep dir to LD_LIBRARY_PATH and start vrep executable
				File file = new File(dir);
				ProcessBuilder builder = new ProcessBuilder(file.getAbsolutePath()+File.separator+"vrep.sh");
				builder.environment().put("LD_LIBRARY_PATH", builder.environment().get("LD_LIBRARY_PATH")+":"+file.getAbsolutePath());
				builder.inheritIO();
				process = builder.start();
			} catch(Exception ex){
				System.err.println("Error launching VREP ");
			}

			int tries = 0;
			while(clientID == -1 && tries++ < 60){
				clientID = vrep.simxStart("127.0.0.1",19997,true,true, 1000, 5);
			}

		}
		
		if(clientID == -1){
			System.out.println("Failed to connect to VREP");
			throw new Exception("Failed to connect to VREP");
		}
		
		String scene = context.getProperty("vrep.scene");
		if(scene != null){
			loadScene(scene);
		} else {
			loadHandles();
		}
	}
	
	@Deactivate
	void deactivate(){
		vrep.simxFinish(clientID);
		
		if(process!=null){
			process.destroy();
		}
	}
	
	public void start(){
		start(false);
	}
	
	@Override
	public void start(boolean sync) {
		configure();
		
		vrep.simxSynchronous(clientID, sync);
		
		vrep.simxStartSimulation(clientID, vrep.simx_opmode_blocking);
		
	}
	
	private void configure(){
		// TODO Should we configure the bundles registering OSGi services here?
		// based on the present objects we could construct multiple configurations?
		try {
			for(String name : objectHandles.keySet()){
				if(name.equals("youBot")){
					Configuration c = ca.createFactoryConfiguration("be.iminds.iot.robot.youbot.ros.Youbot", null);
					c.update(new Hashtable());
					configurations.add(c);
				} else if(name.equals("hokuyo")){
					Configuration c = ca.createFactoryConfiguration("be.iminds.iot.sensor.range.ros.LaserScanner", null);
					c.update(new Hashtable());
					configurations.add(c);
				}
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void pause() {
		deconfigure();
		
		vrep.simxPauseSimulation(clientID, vrep.simx_opmode_blocking);
	}

	@Override
	public void stop() {
		deconfigure();
		
		// stop the simulation:
		vrep.simxStopSimulation(clientID,vrep.simx_opmode_blocking);
	}

	private void deconfigure(){
		for(Configuration c : configurations){
			try {
				c.delete();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		configurations.clear();
	}
	
	@Override
	public void tick() {
		vrep.simxSynchronousTrigger(clientID);
	}

	@Override
	public void loadScene(String file) {
		File f = new File(file);
		if(!f.exists()){
			System.out.println("File "+file+" does not exist...");
			return;
		}
		vrep.simxLoadScene(clientID, f.getAbsolutePath(), 0, vrep.simx_opmode_blocking);
		
		loadHandles();
	}

	@Override
	public Position getPosition(String object) {
		return getPosition(object, null);
	}

	@Override
	public void setPosition(String object, Position p) {
		setPosition(object, null, p);
	}

	@Override
	public Position getPosition(String object, String relativeTo) {
		Integer objectHandle = objectHandles.get(object);
		if(objectHandle == null){
			System.out.println("Object "+object+" not found...");
			return null;
		}
		Integer relativeToObjectHandle = objectHandles.get(relativeTo);
		if(relativeToObjectHandle == null){
			relativeToObjectHandle = new Integer(-1);
		}
		
		FloatWA position = new FloatWA(3);
		
		vrep.simxGetObjectPosition(clientID, objectHandle, relativeToObjectHandle, position, vrep.simx_opmode_blocking);
		
		return new Position(position.getArray()[0], position.getArray()[1], position.getArray()[2]);
	}

	@Override
	public void setPosition(String object, String relativeTo, Position p) {
		Integer objectHandle = objectHandles.get(object);
		if(objectHandle == null){
			System.out.println("Object "+object+" not found...");
			return;
		}
		Integer relativeToObjectHandle = objectHandles.get(relativeTo);
		if(relativeToObjectHandle == null){
			relativeToObjectHandle = new Integer(-1);
		}
		
		FloatWA position = new FloatWA(3);
		position.getArray()[0]= p.x;
		position.getArray()[1]= p.y;
		position.getArray()[2]= p.z;
		
		vrep.simxSetObjectPosition(clientID, objectHandle, relativeToObjectHandle, position, vrep.simx_opmode_blocking);
	}

	public void setPosition(String object, float x, float y, float z) {
		setPosition(object, new Position(x, y, z));
	}
	
	public void setPosition(String object, String relativeTo, float x, float y, float z) {
		setPosition(object, relativeTo, new Position(x, y, z));
	}
	
	private void loadHandles(){
		objectHandles.clear();
		
		IntWA objectHandles = new IntWA(1);
		StringWA objectNames = new StringWA(1);
		IntWA intData = new IntWA(1);
		FloatWA floatData = new FloatWA(1);
		
		int ret = vrep.simxGetObjectGroupData(clientID, vrep.sim_appobj_object_type, 0, objectHandles, intData, floatData, objectNames, vrep.simx_opmode_blocking);
		if (ret==vrep.simx_return_ok){
			int[] handles = objectHandles.getArray();
			String[] names = objectNames.getArray();
			for(int i=0;i<handles.length;i++){
				String name = names[i];
				int handle = handles[i];
				this.objectHandles.put(name, handle);
			}
		}		
	}
}
