package be.iminds.iot.ros.camera;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;

import be.iminds.iot.ros.util.NativeRosNode;

/**
 * Native ROS USBCamera node. Starts publishing to a image_raw topic that can be 
 * exposed to OSGi via CameraSubscriber
 * 
 * @author tverbele
 *
 */
@Component(immediate=true,
		name="be.iminds.iot.ros.camera.USBCamera",
		configurationPolicy=ConfigurationPolicy.REQUIRE)
public class USBCameraNode extends NativeRosNode {

}

