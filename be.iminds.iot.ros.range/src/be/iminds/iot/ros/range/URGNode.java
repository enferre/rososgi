package be.iminds.iot.ros.range;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;

import be.iminds.iot.ros.util.NativeRosNode;

@Component(immediate=true,
		name="be.iminds.iot.ros.range.URG",
		configurationPolicy=ConfigurationPolicy.REQUIRE)
public class URGNode extends NativeRosNode {

}

