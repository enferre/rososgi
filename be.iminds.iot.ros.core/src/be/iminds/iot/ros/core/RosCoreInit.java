package be.iminds.iot.ros.core;

import java.net.Socket;
import java.net.URI;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.ros.RosCore;

import be.iminds.iot.ros.api.Environment;

@Component(immediate=true)
public class RosCoreInit {

	private final static String DEFAULT_URI = "http://localhost:11311";
	
	// choose between Java and native ROS core
	private Process nativeCore;
	private RosCore core;
	
	private Environment env;
	
	@Activate
	void activate(BundleContext context) throws Exception {
		URI masterURI = env.getMasterURI();
		if(masterURI==null){
			masterURI = new URI(DEFAULT_URI);
		}
		boolean start = true;
		try {
			// try to connect first to see whether there is already something running there
			Socket s = new Socket(masterURI.getHost(), masterURI.getPort());
			start = false;
			s.close();
		} catch(Exception e){
			System.out.println("ROS core already running...");
		}
		
		if(start){
			boolean startNative = false;
			String rosCoreNative = context.getProperty("ros.core.native");
			if(rosCoreNative != null){
				startNative = Boolean.parseBoolean(rosCoreNative);
			}
			
			if(startNative){
				// native ROScore process
				ProcessBuilder builder = new ProcessBuilder("roscore");
				builder.inheritIO();
				nativeCore = builder.start();
			} else {
				// rosjava ROScore implementation
				core = RosCore.newPublic(masterURI.getHost(), masterURI.getPort());
				core.start();
				System.out.println("ROS core service [/rosout] started on "+core.getUri());
			}
		}
	}

	@Deactivate
	void deactivate() throws Exception {
		if(core != null)
			core.shutdown();
		
		if(nativeCore != null){
			nativeCore.destroy();
		}
	}

	@Reference
	void setEnvironment(Environment e){
		this.env = e;
	}
}
