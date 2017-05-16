package be.iminds.iot.ros.erlerover;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;

import be.iminds.iot.ros.util.NativeRosNode;

/**
 * Start MAVROS node connecting to rover, requires APM service to be there
 * 
 * TODO seems not to work ... for now launch it ourselves outside of OSGi?!
 * TODO take a look how this (w/c)ould work on the real rover
 * 
 * @author tverbele
 *
 */
@Component(immediate=true,
		name="be.iminds.iot.ros.erlerover.Rover",
		configurationPolicy=ConfigurationPolicy.REQUIRE)
public class MavrosNode extends NativeRosNode {

	public MavrosNode(){
		super("mavros","mavros_node");
	}
	
	@Reference
	void setAPMService(APMService s){
		// Wait for APM service to be started
	}
}

