/*******************************************************************************
 *  ROSOSGi - Bridging the gap between Robot Operating System (ROS) and OSGi
 *  Copyright (C) 2015, 2017  imec - IDLab - UGent
 *
 *  This file is part of DIANNE  -  Framework for distributed artificial neural networks
 *
 *  DIANNE is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *   
 *  Contributors:
 *      Tim Verbelen, Steven Bohez
 *******************************************************************************/
package be.iminds.iot.simulator.vrep;

import java.io.File;
import java.util.HashMap;
import java.util.Hashtable;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import be.iminds.iot.simulator.api.Simulator;
import coppelia.remoteApi;

/**
 * Check and connect to external VREP process and keep heartbeat once connected.
 * 
 * @author tverbele
 *
 */
@Component(immediate=true)
public class VREPActivator {
	
	private remoteApi server;
	private int clientID = -1;

	private VREP vrep;
	private ServiceRegistration<Simulator> reg;
	
	private boolean launch = false;
	private boolean headless = false;
	private boolean log = false;
	private Process process;
	private String dir = "/opt/vrep";
	private String scene;
	
	private ConfigurationAdmin ca;
	private BundleContext context;
	
	private volatile boolean heartbeat = false;
	private int interval = 10000;
	private int port = 19997;
	private String rosMasterURI = null;
	private int timeout = 5000;
	
	@Activate
	void activate(BundleContext context) throws Exception {
		this.context = context;
		
		String r = context.getProperty("ros.master.uri");
		if(r!=null){
			rosMasterURI = context.getProperty("ros.master.uri");
		}
		
		String l = context.getProperty("vrep.launch");
		if(l!=null){
			launch = Boolean.parseBoolean(l);
		}
		
		String i = context.getProperty("vrep.interval");
		if(i!=null){
			interval = Integer.parseInt(i);
		}
		
		String d = context.getProperty("vrep.dir");
		if(d!=null){
			dir = d;
		}
		
		String h = context.getProperty("vrep.headless");
		if(h!=null){
			headless = Boolean.parseBoolean(h);
		}
		
		String p = context.getProperty("vrep.port");
		if(p!=null){
			port = Integer.parseInt(p);
		}
		
		String t = context.getProperty("vrep.timeout");
		if(t!=null){
			timeout = Integer.parseInt(t);
		}
	
		String lg = context.getProperty("vrep.log");
		if(lg!=null){
			log = Boolean.parseBoolean(lg);
		}
		
		scene = context.getProperty("vrep.scene");
		
		
		this.server = new remoteApi();
		this.server.simxFinish(-1); // just in case, close all opened connections

		this.heartbeat = true;
		
		new Thread(() ->{
			while(heartbeat){
				connect();
				
				try {
					Thread.sleep(interval);
				} catch (Exception e) {
				}
			}
		}).start();
		
	}
	
	void connect(){
		if(clientID != -1){
			// check if still connected
			int ret = server.simxGetConnectionId(clientID);
			if(ret != -1){
				// already connected
				return;
			} else {
				// connection lost - tear down
				tearDown();
			}
		}
		
		// try to connect to an already running VREP
		clientID = server.simxStart("127.0.0.1", port, true, true, -timeout, 5);
		if(clientID == -1 && launch){
			// no VREP running ... try to launch a local VREP process
			try {
				// add vrep dir to LD_LIBRARY_PATH and start vrep executable
				File file = new File(dir);
				ProcessBuilder builder = new ProcessBuilder(file.getAbsolutePath()+File.separator+"vrep",
							headless ? "-h" : "",
							port != 19997 ? "-gREMOTEAPISERVERSERVICE_"+port+"_FALSE_TRUE": "");
				builder.environment().put("LD_LIBRARY_PATH", builder.environment().get("LD_LIBRARY_PATH")+":"+file.getAbsolutePath());
				if(rosMasterURI != null){
					builder.environment().put("ROS_MASTER_URI", rosMasterURI.toString());
				}
				if(log)
					builder.inheritIO();
				process = builder.start();
			} catch(Exception ex){
				System.err.println("Error launching VREP ");
			}


			int tries = 0;
			while(clientID == -1 && tries++ < 10){
				clientID = server.simxStart("127.0.0.1", port, true, true, -timeout, 5);
			}	

		}
		
		if(clientID != -1){
			init();
		}
		
	}
	
	void init(){
		vrep = new VREP(server, ca);
		
		if(scene != null){
			vrep.loadScene(scene, new HashMap<String, String>());
		} else {
			vrep.loadHandles();
		}

		Hashtable<String, Object> properties = new Hashtable<String, Object>();
		properties.put("osgi.command.scope", "vrep");
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
		reg = context.registerService(Simulator.class, vrep, properties);
	}
	
	@Deactivate
	void deactivate(){
		this.heartbeat = false;
		
		tearDown();
	}
	
	void tearDown(){
		if(reg != null){
			reg.unregister();
		}
		
		vrep.deconfigure();
		
		this.server.simxFinish(-1);
	}
	
	@Reference
	void setConfigurationAdmin(ConfigurationAdmin ca){
		this.ca = ca;
	}
}
