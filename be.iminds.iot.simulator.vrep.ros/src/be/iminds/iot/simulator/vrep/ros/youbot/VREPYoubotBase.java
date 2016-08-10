package be.iminds.iot.simulator.vrep.ros.youbot;

import be.iminds.iot.simulator.vrep.ros.VREPInterface;

public class VREPYoubotBase {

	int[] wheels = new int[4];
	double scale = 1000;
	
	private VREPInterface vrep;
	
	public VREPYoubotBase(VREPInterface v, int fl, int rl, int rr, int fr){
		vrep = v;
		
		wheels[0] = fl;
		wheels[1] = rl;
		wheels[2] = rr;
		wheels[3] = fr;
	}
	
	public void setVelocity(int wheel, double vel){
		vrep.setJointTargetVelocity(wheels[wheel], vel);
	}
	
	public void move(double x, double y, double angular){
		vrep.setJointTargetVelocity(wheels[0], (-y + x + angular)*scale);
		vrep.setJointTargetVelocity(wheels[1], (-y - x + angular)*scale);
		vrep.setJointTargetVelocity(wheels[2], (-y + x - angular)*scale);
		vrep.setJointTargetVelocity(wheels[3], (-y - x - angular)*scale);
	}
	
}
