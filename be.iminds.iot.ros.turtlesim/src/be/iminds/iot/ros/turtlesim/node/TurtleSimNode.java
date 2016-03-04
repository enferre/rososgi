package be.iminds.iot.ros.turtlesim.node;

import org.osgi.service.component.annotations.Component;

import be.iminds.iot.ros.util.NativeRosNode;

@Component(immediate=true)
public class TurtleSimNode extends NativeRosNode {

	public TurtleSimNode(){
		super("turtlesim","turtlesim_node");
	}
	
}

