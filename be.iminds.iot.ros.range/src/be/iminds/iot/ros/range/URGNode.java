package be.iminds.iot.ros.range;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;

import be.iminds.iot.ros.util.NativeRosNode;

@Component(immediate=true,
		name="be.iminds.iot.ros.range.URG",
		configurationPolicy=ConfigurationPolicy.REQUIRE)
public class URGNode extends NativeRosNode {

	public URGNode(){
		super("urg_node","urg_node");
	}
	
	protected void activate(Map<String, Object> properties) throws Exception {
		String name = properties.get("name").toString();
		if(name != null){
			properties.put("ros.mappings", "scan:="+name.replaceAll(" ", "_").toLowerCase()+"/scan");
		}
		
		super.activate(properties);
	}
}

