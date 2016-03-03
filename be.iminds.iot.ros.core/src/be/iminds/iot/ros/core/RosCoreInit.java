package be.iminds.iot.ros.core;

import java.net.URI;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.ros.EnvironmentVariables;
import org.ros.RosCore;

@Component(immediate=true)
public class RosCoreInit {

	private final static String DEFAULT_URI = "http://localhost:11311";
	private RosCore core;
	
	@Activate
	public void activate(BundleContext context) throws Exception {
		URI uri = null;
		
		// first try env variable
		String env = System.getenv(EnvironmentVariables.ROS_MASTER_URI);
		if(env != null){
			uri = new URI(env);
		} else {
			
			// then try context property
			String ctx = context.getProperty("ros.master.uri");
			if(ctx != null){
				uri = new URI(ctx);
			} else {
				uri = new URI(DEFAULT_URI);
			}
		}
		
		core = RosCore.newPublic(uri.getHost(), uri.getPort());
		core.start();
		System.out.println("ROS core service [/rosout] started on "+core.getUri());
	}

	@Deactivate
	public void deactivate() throws Exception {
		core.shutdown();
	}


}
