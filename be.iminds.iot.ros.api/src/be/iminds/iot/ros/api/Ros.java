package be.iminds.iot.ros.api;

import java.net.URI;
import java.util.Collection;

public interface Ros {

	URI getMasterURI();
	
	URI getNodeURI(String node);
	
	Collection<String> getNodes();
	
	Collection<String> getTopics();
	
	Collection<String> getPublishers(String topic);
	
	Collection<String> getSubscribers(String topic);
	
	String getTopicType(String topic);
	
}
