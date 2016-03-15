package be.iminds.iot.ros.simulator.vrep.youbot;

import be.iminds.iot.ros.simulator.vrep.VREPJointController;

public class VREPYoubotBase {

	int[] wheels = new int[4];
	
	private VREPJointController controller;
	
	public VREPYoubotBase(VREPJointController c, int fl, int rl, int rr, int fr){
		controller = c;
		
		wheels[0] = fl;
		wheels[1] = rl;
		wheels[2] = rr;
		wheels[3] = fr;
	}
	
	public void setVelocity(int wheel, double vel){
		controller.setJointTargetVelocity(wheels[wheel], vel);
	}
	
	public void move(double x, double y, double angular){
		controller.setJointTargetVelocity(wheels[0], -y - x - angular);
		controller.setJointTargetVelocity(wheels[1], -y + x - angular);
		controller.setJointTargetVelocity(wheels[2], -y - x + angular);
		controller.setJointTargetVelocity(wheels[3], -y + x + angular);
	}
	
}
