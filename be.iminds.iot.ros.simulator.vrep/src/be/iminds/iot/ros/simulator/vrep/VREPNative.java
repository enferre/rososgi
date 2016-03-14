package be.iminds.iot.ros.simulator.vrep;

import java.io.File;
import java.io.FileNotFoundException;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

@Component(immediate=true)
public class VREPNative {

	private Process process;
	
	private String VREP_DIR = "/opt/vrep";
	
	@Activate
	void activate(BundleContext context) throws Exception {
		
		String s = context.getProperty("vrep.dir");
		if(s!=null){
			VREP_DIR = s;
		}
		
		if(VREP_DIR.equals("none"))
			return;
		
		File dir = new File(VREP_DIR);
		if(!dir.exists() || !dir.isDirectory()){
			System.err.println("VREP installation directory "+VREP_DIR+" does not exist");
			throw new FileNotFoundException(dir.getName());
		}
			
		try {
			// add vrep dir to LD_LIBRARY_PATH and start vrep executable
			ProcessBuilder builder = new ProcessBuilder(dir.getAbsolutePath()+File.separator+"vrep");
			builder.environment().put("LD_LIBRARY_PATH", builder.environment().get("LD_LIBRARY_PATH")+":"+dir.getAbsolutePath());
			process = builder.start();
		} catch(Exception e){
			System.err.println("Error launching VREP ");
			throw e;
		}
	}
	
	@Deactivate
	void deactivate(){
		if(process!=null){
			process.destroy();
		}
	}
	
}
