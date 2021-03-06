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
package be.iminds.iot.ros.configurator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

@Component(immediate=true)
public class ROSConfigurator {

	private ConfigurationAdmin ca;
	
	private List<Configuration> configurations = new ArrayList<>();
	
	@Activate
	void activate(){
		// launch on separate thread ... might run into threading issues when running on DS thread
		new Thread(()->{ synchronized(ROSConfigurator.this){
		
			// TODO configurable dir
			File dir = new File(".");
			if(!dir.isDirectory()){
				System.out.println(dir.getAbsolutePath()+" is not a directory");
			}
			
			for(File f : dir.listFiles()){
				if(f.isDirectory())
					continue; // dont recurse
				
				if(!f.getName().endsWith(".config"))
					continue;
				
				try {
					Properties props = new Properties();
					props.load(new FileInputStream(f));
				
					Dictionary<String, String> dict = new Hashtable<>();
					props.entrySet().forEach(e -> dict.put((String)e.getKey(), (String)e.getValue()));
					
					// set ros package
					Configuration nodeConfig = null;
					Configuration subscriberConfig = null;
					
					String name = dict.get("name").replaceAll("( )|#", "_").toLowerCase(); // use name to remap topics
					String type = dict.get("type"); // use type to configure ros node to launch
					switch(type){
						case "youbot":
							nodeConfig = ca.createFactoryConfiguration("be.iminds.iot.ros.youbot.Youbot", null);
							// TODO it seems this configuration gets added twice, which might screw up stuff!
							subscriberConfig = ca.createFactoryConfiguration("be.iminds.iot.robot.youbot.ros.Youbot", null);
							break;
						case "usb_cam":
							dict.put("ros.mappings", "usb_cam/image_raw:="+name+"/image_raw,"
									+ "usb_cam/camera_info:="+name+"/image_raw");
							nodeConfig = ca.createFactoryConfiguration("be.iminds.iot.ros.camera.USBCamera", null);
							subscriberConfig = ca.createFactoryConfiguration("be.iminds.iot.sensor.camera.ros.Camera", null);
							break;
						case "hokuyo":
							if(dict.get("topic")!= null){
								dict.put("ros.mappings", "scan:="+dict.get("topic").substring(1));
							} else {
								dict.put("ros.mappings", "scan:="+name+"/scan");
							}
							nodeConfig = ca.createFactoryConfiguration("be.iminds.iot.ros.range.URG", null);
							subscriberConfig = ca.createFactoryConfiguration("be.iminds.iot.sensor.range.ros.LaserScanner", null);
							break;
						case "rover":
							nodeConfig = ca.createFactoryConfiguration("be.iminds.iot.ros.erlerover.Rover", null);
							subscriberConfig = ca.createFactoryConfiguration("be.iminds.iot.robot.erlerover.ros.Rover", null);
							break;
						case "panda":
							nodeConfig = ca.createFactoryConfiguration("be.iminds.iot.ros.panda.Panda", null);
							subscriberConfig = ca.createFactoryConfiguration("be.iminds.iot.robot.panda.ros.Panda", null);
							break;
						case "moveit":
							// in case of generic move it arm - make sure to launch your own node config in combination?
							subscriberConfig = ca.createFactoryConfiguration("be.iminds.iot.robot.moveit.Arm", null);
							break;
						case "mmwave":
							nodeConfig = ca.createFactoryConfiguration("be.iminds.iot.ros.mmwave.Radar", null);
							subscriberConfig = ca.createFactoryConfiguration("be.iminds.iot.sensor.range3d.ros.Scanner3D", null);
							break;
						default: 
							continue;
					}
					
					if(nodeConfig != null){
						nodeConfig.update(dict);
						configurations.add(nodeConfig);
					}
					
					if(subscriberConfig != null){
						subscriberConfig.update(dict);
						configurations.add(subscriberConfig);
					}
				} catch(IOException e){
					System.err.println("Error reading config file "+f.getAbsolutePath());
				}
			}
		}}).start();
	}
	
	@Deactivate
	void deactivate(){
		synchronized(this){
			for(Configuration c : configurations){
				try {
					c.delete();
				} catch (IOException e) {
				}
			}
		}
	}
	
	@Reference
	void setConfigurationAdmin(ConfigurationAdmin ca){
		this.ca = ca;
	}
	
}
