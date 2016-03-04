package be.iminds.iot.ros.core;

import java.net.URI;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Component;
import org.ros.master.client.MasterStateClient;
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
		"osgi.command.function=subscribers"})
public class RosInfo extends AbstractNodeMain implements Ros {

	private MasterStateClient master;

	@Override
	public GraphName getDefaultNodeName() {
		return GraphName.of("rosinfo/");
	}

	@Override
	public void onStart(final ConnectedNode connectedNode) {
		master = new MasterStateClient(connectedNode, connectedNode.getMasterUri());
		SystemState state = master.getSystemState();
		state.getTopics().stream().forEach(t -> System.out.println(t.getTopicName()));
	}

	@Override
	public URI getMasterURI() {
		return master.getUri();
	}

	@Override
	public URI getNodeURI(String node) {
		return master.lookupNode(node);
	}

	@Override
	public Collection<String> getNodes() {
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
		SystemState state = master.getSystemState();
		return state.getTopics().stream().map(t -> t.getTopicName()).collect(Collectors.toList());
	}

	@Override
	public Collection<String> getPublishers(String topic) {
		SystemState state = master.getSystemState();
		TopicSystemState tss = state.getTopics().stream().filter(t -> t.getTopicName().equals(topic)).findFirst().get();
		return tss.getPublishers();
	}

	@Override
	public Collection<String> getSubscribers(String topic) {
		SystemState state = master.getSystemState();
		TopicSystemState tss = state.getTopics().stream().filter(t -> t.getTopicName().equals(topic)).findFirst().get();
		return tss.getSubscribers();
	}

	@Override
	public String getTopicType(String topic) {
		TopicType type = master.getTopicTypes().stream().filter(t -> t.getName().equals(topic)).findFirst().get();
		return type.getMessageType();
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
}
