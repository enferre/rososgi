package be.iminds.iot.ros.youbot;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import be.iminds.iot.ros.api.Ros;
import be.iminds.iot.ros.util.NativeRosNode;

@Component(immediate=true)
public class YoubotDriver extends NativeRosNode {

	private Ros ros;
	
	public YoubotDriver(){
		super("youbot_driver_ros_interface","youbot_driver_ros_interface");
	}
	
	public void activate() throws Exception {
		ros.setParameter("youBotHasBase", true);
		ros.setParameter("youBotHasArms", true);

		ros.setParameter("youBotBaseName", "youbot-base");
		ros.setParameter("youBotArmName1", "youbot-manipulator");
		
		super.activate();
	}
	
	@Reference
	void setRos(Ros ros){
		this.ros = ros;
	}
}

