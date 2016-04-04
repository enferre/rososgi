package be.iminds.iot.ros.util;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Deactivate;

public class NativeRosNode {

	protected Process process;
	
	protected final String rosPackage;
	protected final String rosNode;
	protected final List<String> rosMappings = new ArrayList<>();
	
	public NativeRosNode(String pkg, String node){
		this.rosPackage = pkg;
		this.rosNode = node;
	}
	
	public NativeRosNode(String pkg, String node, String... mappings){
		this.rosPackage = pkg;
		this.rosNode = node;
		
		for(String mapping : mappings){
			if(!mapping.contains(":=")){
				System.out.println("Invalid mapping: "+mapping);
				continue;
			} 
			rosMappings.add(mapping);
		}
	}
	
	@Activate
	protected void activate() throws Exception {
		try {
			List<String> cmd = new ArrayList<>();
			cmd.add("rosrun");
			cmd.add(rosPackage);
			cmd.add(rosNode);
			cmd.addAll(rosMappings);
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
	
}
