package be.iminds.iot.ros.core;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.ros.EnvironmentVariables;

import be.iminds.iot.ros.api.Environment;

@Component(property = {"osgi.command.scope=ros", 
		"osgi.command.function=env"})
public class RosEnvironment implements Environment {

	private URI masterURI;
	private String distro;
	private String namespace;
	private String root;
	private String packagePath;
	
	@Activate
	void activate(BundleContext context) throws Exception {
		try {
			String uri = getVariable("ROS_MASTER_URI", "ros.master.uri", context);
			masterURI = new URI(uri);
			distro = getVariable("ROS_DISTRO", "ros.distro", context);
			namespace = getVariable("ROS_NAMESPACE", "ros.namespace", context);
			root = getVariable("ROS_ROOT", "ros.root", context);
			packagePath = getVariable("ROS_PACKAGE_PATH", "ros.package.path", context);
		} catch(Exception e){
			System.err.println("Error setting up the ROS environment");
			throw e;
		} 
	}
	
	private String getVariable(String environmentKey, String propertyKey, BundleContext context){
		String env = System.getenv(environmentKey);
		if(env != null){
			return env;
		} else {
			// then try context property
			String ctx = context.getProperty(propertyKey);
			if(ctx != null){
				return ctx;
			}
		}
		return null;
	}
	
	@Override
	public URI getMasterURI() {
		return masterURI;
	}

	@Override
	public String getHost(){
		return masterURI.getHost();
	}
	
	@Override
	public int getPort(){
		return masterURI.getPort();
	}
	
	@Override
	public String getDistro() {
		return distro;
	}

	@Override
	public String getNamespace() {
		return namespace;
	}

	@Override
	public File getRoot() {
		return new File(root);
	}

	@Override
	public List<File> getPackagePath() {
		List<File> files = new ArrayList<>();
		for(String f : packagePath.split(":")){
			files.add(new File(f));
		}
		return files;
	}
	
	
	public void env(){
		StringBuilder builder = new StringBuilder();
		builder.append("ROS Environmnent \n");
		builder.append(" masterURI: ").append(masterURI).append("\n");
		builder.append(" distro: ").append(distro).append("\n");
		builder.append(" namespace: ").append(namespace).append("\n");
		builder.append(" root: ").append(root).append("\n");
		builder.append(" packagePath: ").append(packagePath).append("\n");
		System.out.println(builder.toString());
	}

}
