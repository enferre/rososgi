package be.iminds.iot.ros.api;

import java.io.File;
import java.net.URI;
import java.util.List;

public interface Environment {

	URI getMasterURI();
	
	String getHost();
	
	int getPort();
	
	String getDistro();
	
	String getNamespace();
	
	File getRoot();
	
	List<File> getPackagePath();
	
}
