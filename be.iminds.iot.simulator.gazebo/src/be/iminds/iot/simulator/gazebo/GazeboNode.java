package be.iminds.iot.simulator.gazebo;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;

import be.iminds.iot.ros.util.NativeRosNode;

/**
 * Native ROS Gazebo node. Starts Gazebo simulator.
 * 
 * @author tverbele
 *
 */
@Component(immediate=true,
	name="be.iminds.iot.simulator.gazebo.Native",
	configurationPolicy=ConfigurationPolicy.REQUIRE)
public class GazeboNode extends NativeRosNode {
	
	public GazeboNode(){
		super("gazebo_ros","gazebo");
	}
	
	protected void deactivate(){
		super.deactivate();
		
		try {
			Runtime.getRuntime().exec("killall gzclient");
			Runtime.getRuntime().exec("killall gzserver");
		} catch(Exception e){
			e.printStackTrace();
		}
	}
}

