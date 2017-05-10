package be.iminds.iot.ros.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import be.iminds.iot.ros.api.Environment;

public class NativeRosNode {

	protected Process process;
	
	protected String rosPackage;
	protected String rosNode;
	protected List<String> rosParameters = new ArrayList<>();
	
	public NativeRosNode(){
	}
	
	public NativeRosNode(String pkg, String node){
		this.rosPackage = pkg;
		this.rosNode = node;
	}
	
	public NativeRosNode(String pkg, String node, String... parameters){
		this.rosPackage = pkg;
		this.rosNode = node;
		
		for(String parameter : parameters){
			if(!parameter.contains(":=")){
				System.out.println("Invalid parameter: "+parameter);
				continue;
			} 
			rosParameters.add(parameter);
		}
	}
	
	@Activate
	protected void activate(Map<String, Object> properties) throws Exception {
		// this also allows to build a ROS node driven by configuration
		for(Entry<String, Object> entry : properties.entrySet()){
			String key = entry.getKey();
			// specific ros properties
			if(key.equals("ros.package")){
				rosPackage = (String) entry.getValue();
			} else if(key.equals("ros.node")){
				rosNode = (String) entry.getValue();
			} else if(key.equals("ros.mappings")){
				String extraMappings = (String) entry.getValue();
				if(extraMappings != null){
					String[] mappings = extraMappings.split(",");
					for(String mapping : mappings){
						if(!mapping.contains(":=")){
							System.out.println("Invalid mapping: "+mapping);
							continue;
						} 
						rosParameters.add(mapping);
					}
				}	
			} else if(!key.contains(".")){ // ignore parameters with "." , most likely OSGi service props
				// add as private parameters for ROS node
				rosParameters.add("_"+key+":="+entry.getValue());
			}
		}
		
		try {
			List<String> cmd = new ArrayList<>();
			cmd.add("rosrun");
			cmd.add(rosPackage);
			cmd.add(rosNode);
			cmd.addAll(rosParameters);
			ProcessBuilder builder = new ProcessBuilder(cmd);
			builder.inheritIO();
			process = builder.start();
		} catch(Exception e){
			System.err.println("Error launching native ros node "+rosPackage+" "+rosNode);
			throw e;
		}
	}
	
	@Deactivate
	protected void deactivate(){
		if(process!=null){
			process.destroy();
		}
	}
	
	@Reference
	void setROSEnvironment(Environment e){
		// make sure ROS core is running before activating a native node
	}
	
}
