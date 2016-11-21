package be.iminds.iot.simulator.vrep;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

import be.iminds.iot.simulator.api.Orientation;
import be.iminds.iot.simulator.api.Position;
import be.iminds.iot.simulator.api.Simulator;
import coppelia.BoolW;
import coppelia.FloatWA;
import coppelia.IntW;
import coppelia.IntWA;
import coppelia.StringWA;
import coppelia.remoteApi;

/**
 * Simulator implementation for the VREP simulator
 * 
 * @author tverbele
 *
 */
public class VREP implements Simulator {
	
	private remoteApi server; 
	private int clientID;
	
	private Map<String, Integer> objectHandles = new HashMap<>();
	private Map<String, String> entities = new HashMap<>();
	
	private List<Configuration> configurations = new ArrayList<>();
	private ConfigurationAdmin ca;
	
	public VREP(remoteApi server, ConfigurationAdmin ca){
		this.server = server;
		this.ca = ca;
	}
	
	public void start(){
		start(false);
	}
	
	@Override
	public synchronized void start(boolean sync) {
		checkOk(server.simxSynchronous(clientID, sync));
		
		checkOk(server.simxStartSimulation(clientID, server.simx_opmode_blocking));
		
		configure();
	}
	
	void configure(){
		try {
			entities.entrySet().stream().forEach(e ->{
				String name = e.getKey();
				String type = e.getValue();
				
				if(!objectHandles.containsKey(name)){
					System.out.println("Entitiy "+name+" not existing in the simulator...");
					return;
				}
				
				Hashtable<String, Object> t = new Hashtable<>();
				t.put("name", name);
				
				try {
					Configuration c = ca.createFactoryConfiguration(type, null);
					c.update(t);
					configurations.add(c);
				} catch(Exception ex){
					ex.printStackTrace();
				}
			});
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public synchronized void pause() {
		deconfigure();

		checkOk(server.simxPauseSimulation(clientID, server.simx_opmode_blocking));
	}

	@Override
	public synchronized void stop() {
		deconfigure();

		// stop the simulation:
		checkOk(server.simxStopSimulation(clientID,server.simx_opmode_blocking));
	}

	void deconfigure(){
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
	public void tick() throws TimeoutException {
		int ret = server.simxSynchronousTrigger(clientID);
		if(ret > 1){
			throw new TimeoutException();
		}
		checkOk(ret);
	}

	@Override
	public void loadScene(String file, Map<String, String> entities) {
		File f = new File(file);
		if(!f.exists()){
			System.out.println("File "+file+" does not exist...");
			return;
		}
		checkOk(server.simxLoadScene(clientID, f.getAbsolutePath(), 0, server.simx_opmode_blocking));
		
		this.entities = entities;
		
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
		
		checkOk(server.simxGetObjectPosition(clientID, objectHandle, relativeToObjectHandle, position, server.simx_opmode_blocking));
		
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
		
		checkOk(server.simxSetObjectPosition(clientID, objectHandle, relativeToObjectHandle, position, server.simx_opmode_blocking));
	}

	public void setPosition(String object, float x, float y, float z) {
		setPosition(object, new Position(x, y, z));
	}
	
	public void setPosition(String object, String relativeTo, float x, float y, float z) {
		setPosition(object, relativeTo, new Position(x, y, z));
	}
	
	@Override
	public Orientation getOrientation(String object) {
		return getOrientation(object, null);
	}

	@Override
	public void setOrientation(String object, Orientation o) {
		setOrientation(object, null, o);
	}

	@Override
	public Orientation getOrientation(String object, String relativeTo) {
		Integer objectHandle = objectHandles.get(object);
		if(objectHandle == null){
			System.out.println("Object "+object+" not found...");
			return null;
		}
		Integer relativeToObjectHandle = objectHandles.get(relativeTo);
		if(relativeToObjectHandle == null){
			relativeToObjectHandle = new Integer(-1);
		}
		
		FloatWA orientation = new FloatWA(3);
		
		checkOk(server.simxGetObjectOrientation(clientID, objectHandle, relativeToObjectHandle, orientation, server.simx_opmode_blocking));
		
		return new Orientation(orientation.getArray()[0], orientation.getArray()[1], orientation.getArray()[2]);
	}

	@Override
	public void setOrientation(String object, String relativeTo, Orientation o) {
		Integer objectHandle = objectHandles.get(object);
		if(objectHandle == null){
			System.out.println("Object "+object+" not found...");
			return;
		}
		Integer relativeToObjectHandle = objectHandles.get(relativeTo);
		if(relativeToObjectHandle == null){
			relativeToObjectHandle = new Integer(-1);
		}
		
		FloatWA orientation = new FloatWA(3);
		orientation.getArray()[0]= o.alfa;
		orientation.getArray()[1]= o.beta;
		orientation.getArray()[2]= o.gamma;
		
		checkOk(server.simxSetObjectOrientation(clientID, objectHandle, relativeToObjectHandle, orientation, server.simx_opmode_blocking));

	}
	
	public void setOrientation(String object, float a, float b, float g) {
		setOrientation(object, new Orientation(a, b, g));
	}
	
	public void setOrientation(String object, String relativeTo, float a, float b, float g) {
		setOrientation(object, relativeTo, new Orientation(a, b, g));
	}

	
	void loadHandles(){
		objectHandles.clear();
		
		IntWA objectHandles = new IntWA(1);
		StringWA objectNames = new StringWA(1);
		IntWA intData = new IntWA(1);
		FloatWA floatData = new FloatWA(1);
		
		checkOk(server.simxGetObjectGroupData(clientID, server.sim_appobj_object_type, 0, objectHandles, intData, floatData, objectNames, server.simx_opmode_blocking));
		
		int[] handles = objectHandles.getArray();
		String[] names = objectNames.getArray();
		for(int i=0;i<handles.length;i++){
			String name = names[i];
			int handle = handles[i];
			this.objectHandles.put(name, handle);
			
			// add some default entries...
			if(name.equals("youBot"))
				entities.put("youBot", "be.iminds.iot.robot.youbot.ros.Youbot");
			else if(name.equals("hokuyo"))
				entities.put("hokuyo", "be.iminds.iot.sensor.range.ros.LaserScanner");
			
		}
	}

	@Override
	public boolean checkCollisions(String object) {
		IntW handle = new IntW(0);
		
		checkOk(server.simxGetCollisionHandle(clientID, object, handle, server.simx_opmode_blocking));
		
		BoolW collision = new BoolW(false);
		server.simxReadCollision(clientID, handle.getValue(), collision, server.simx_opmode_blocking);
		return collision.getValue();
	
	}

	private void checkOk(int ret){
		if(ret > 3){
			throw new RuntimeException("Failed to execute VREP call "+ret);
		}
	}
}
