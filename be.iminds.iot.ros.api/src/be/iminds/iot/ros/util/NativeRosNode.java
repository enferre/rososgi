package be.iminds.iot.ros.util;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Deactivate;

public class NativeRosNode {

	protected Process process;
	
	protected final String rosPackage;
	protected final String rosNode;
	
	public NativeRosNode(String pkg, String node){
		this.rosPackage = pkg;
		this.rosNode = node;
	}
	
	@Activate
	protected void activate() throws Exception {
		try {
			ProcessBuilder builder = new ProcessBuilder("rosrun", rosPackage, rosNode);
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
