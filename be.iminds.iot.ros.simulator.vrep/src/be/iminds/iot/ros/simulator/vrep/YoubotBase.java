package be.iminds.iot.ros.simulator.vrep;

public class YoubotBase {

	int[] wheels = new int[4];
	
	private JointController controller;
	
	public YoubotBase(JointController c, int fl, int rl, int rr, int fr){
		controller = c;
		
		wheels[0] = fl;
		wheels[1] = rl;
		wheels[2] = rr;
		wheels[3] = fr;
	}
	
	void setVelocity(int wheel, float vel){
		controller.setJointTargetVelocity(wheels[wheel], vel);
	}
	
	void move(float x, float y, float angular){
		controller.setJointTargetVelocity(wheels[0], y - x - angular);
		controller.setJointTargetVelocity(wheels[1], y + x - angular);
		controller.setJointTargetVelocity(wheels[2], y - x + angular);
		controller.setJointTargetVelocity(wheels[3], y + x + angular);
	}
	
}
