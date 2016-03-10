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
	private RosCore core;
	private Environment env;
	
	@Activate
	void activate(BundleContext context) throws Exception {
		URI masterURI = env.getMasterURI();
		if(masterURI==null){
			masterURI = new URI(DEFAULT_URI);
		}
		try {
			// try to connect first to see whether there is already something running there
			Socket s = new Socket(masterURI.getHost(), masterURI.getPort());
			s.close();
		} catch(Exception e){
			core = RosCore.newPublic(masterURI.getHost(), masterURI.getPort());
			core.start();
			System.out.println("ROS core service [/rosout] started on "+core.getUri());
		}
	}

	@Deactivate
	void deactivate() throws Exception {
		core.shutdown();
	}

	@Reference
	void setEnvironment(Environment e){
		this.env = e;
	}
}
