package be.iminds.iot.robot.youbot.ros.ik;

import org.osgi.service.component.annotations.Component;

import be.iminds.iot.ros.util.NativeRosNode;

@Component(immediate=true)
public class YoubotIKSolver extends NativeRosNode {

	public YoubotIKSolver(){
		super("ik_solver_service","ik_solver_service");
	}
	
}

