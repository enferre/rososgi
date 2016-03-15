package be.iminds.iot.ros.youbot;

import org.osgi.service.component.annotations.Component;

import be.iminds.iot.ros.util.NativeRosNode;

@Component(immediate=true)
public class YoubotDriver extends NativeRosNode {

	public YoubotDriver(){
		super("youbot_driver_ros_interface","youbot_driver_ros_interface");
	}
	
}

