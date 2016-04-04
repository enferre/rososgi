package be.iminds.iot.ros.core;

import java.net.URI;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Component;
import org.ros.master.client.MasterStateClient;
import org.ros.master.client.ServiceSystemState;
import org.ros.master.client.SystemState;
import org.ros.master.client.TopicSystemState;
import org.ros.master.client.TopicType;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeMain;

import be.iminds.iot.ros.api.Ros;

@Component(service = {NodeMain.class, Ros.class},
	property = {"osgi.command.scope=ros", 
		"osgi.command.function=nodes",
		"osgi.command.function=topics",
		"osgi.command.function=publishers",
		"osgi.command.function=subscribers",
		"osgi.command.function=services",
		"osgi.command.function=providers"})
public class RosInfo extends AbstractNodeMain implements Ros {

	private MasterStateClient master;
	private ConnectedNode node;

	@Override
	public GraphName getDefaultNodeName() {
		return GraphName.of("rosinfo/");
	}

	@Override
	public void onStart(final ConnectedNode connectedNode) {
		node = connectedNode;
		master = new MasterStateClient(connectedNode, connectedNode.getMasterUri());
		synchronized(this){
			notifyAll();
		}
	}

	@Override
	public URI getMasterURI() {
		waitForInit();
		return master.getUri();
	}

	@Override
	public URI getNodeURI(String node) {
		waitForInit();
		return master.lookupNode(node);
	}

	@Override
	public Collection<String> getNodes() {
		waitForInit();
		// TODO is there a better way to list all nodes?
		SystemState state = master.getSystemState();
		final Set<String> nodes = new HashSet<>();
		state.getTopics().stream().forEach(t -> {
			nodes.addAll(t.getSubscribers());
			nodes.addAll(t.getPublishers());
		});
		return nodes;
	}
	
	@Override
	public Collection<String> getTopics() {
		waitForInit();
		SystemState state = master.getSystemState();
		return state.getTopics().stream().map(t -> t.getTopicName()).collect(Collectors.toList());
	}

	@Override
	public Collection<String> getPublishers(String topic) {
		waitForInit();
		SystemState state = master.getSystemState();
		TopicSystemState tss = state.getTopics().stream().filter(t -> t.getTopicName().equals(topic)).findFirst().get();
		return tss.getPublishers();
	}

	@Override
	public Collection<String> getSubscribers(String topic) {
		waitForInit();
		SystemState state = master.getSystemState();
		TopicSystemState tss = state.getTopics().stream().filter(t -> t.getTopicName().equals(topic)).findFirst().get();
		return tss.getSubscribers();
	}

	@Override
	public String getTopicType(String topic) {
		waitForInit();
		TopicType type = master.getTopicTypes().stream().filter(t -> t.getName().equals(topic)).findFirst().get();
		return type.getMessageType();
	}

	@Override
	public Collection<String> getServices() {
		waitForInit();
		SystemState state = master.getSystemState();
		return state.getServices().stream().map(s -> s.getServiceName()).collect(Collectors.toList());
	}

	@Override
	public Collection<String> getProviders(String service) {
		waitForInit();
		SystemState state = master.getSystemState();
		ServiceSystemState sss = state.getServices().stream().filter(s -> s.getServiceName().equals(service)).findFirst().get();
		return sss.getProviders();
	}
	
	@Override
	public void setParameter(String key, Object value){
		waitForInit();
		if(value instanceof Integer){
			node.getParameterTree().set(key, (Integer)value);
		} else if(value instanceof Double){
			node.getParameterTree().set(key, (Double)value);
		} else if(value instanceof String){
			node.getParameterTree().set(key, (String)value);
		} else if(value instanceof Boolean){
			node.getParameterTree().set(key, (Boolean)value);
		} else if(value instanceof List){
			node.getParameterTree().set(key, (List)value);
		} else if(value instanceof Map){
			node.getParameterTree().set(key, (Map)value);
		} else {
			node.getParameterTree().set(key, String.valueOf(value));
		}
	}	
	
	@Override
	public <T> T getParameter(String key, Class<T> type){
		waitForInit();
		if(type == Integer.class){
			return (T) new Integer(node.getParameterTree().getInteger(key));
		} else if(type == Double.class){
			return (T) new Double(node.getParameterTree().getDouble(key));
		} else if(type == String.class){
			return (T) node.getParameterTree().getString(key);
		} else if(type == Boolean.class){
			return (T) new Boolean(node.getParameterTree().getBoolean(key));
		} else if(type == List.class){
			return (T) node.getParameterTree().getList(key);
		} else if(type == Map.class){
			return (T) node.getParameterTree().getMap(key);
		} 
		return null;
	}
	
	/**
	 * This method ensures that any call to the OSGi service waits until the ROS node
	 * is actually intialized. This is a hacky way of making sure the OSGi and ROS lifecycles
	 * can interwork while using DS 
	 */
	private synchronized void waitForInit() {
		if(master!=null){
			return;
		} else {
			try {
				this.wait();
			} catch(InterruptedException e){}
		}
	}
	
	void nodes(){
		getNodes().stream().forEach(System.out::println);
	}
	
	void topics(){
		getTopics().stream().forEach(System.out::println);
	}
	
	void publishers(String topic){
		getPublishers(topic).stream().forEach(System.out::println);
	}
	
	void subscribers(String topic){
		getPublishers(topic).stream().forEach(System.out::println);
	}
	
	void services(){
		getServices().stream().forEach(System.out::println);
	}
	
	void providers(String service){
		getProviders(service).forEach(System.out::println);
	}
}
