package be.iminds.iot.ros.joystick;

import org.osgi.service.component.annotations.Component;

import be.iminds.iot.ros.util.NativeRosNode;

/**
 * Native ROS Joy node. Starts publishing Joy messages
 * 
 * @author tverbele
 *
 */
@Component(immediate=true)
public class JoyNode extends NativeRosNode {
	
	public JoyNode(){
		super("joy","joy_node");
	}
	
}

