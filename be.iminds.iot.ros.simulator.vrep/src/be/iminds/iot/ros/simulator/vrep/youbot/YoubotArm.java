package be.iminds.iot.ros.simulator.vrep.youbot;

import be.iminds.iot.ros.simulator.vrep.VREPJointController;

public class YoubotArm {

	double[] vrep_min = new double[]{2.94961 , 1.13446, -2.54818,  1.7889625,  2.9234265,      0,       0};
	double[] vrep_max = new double[]{-2.94961, -1.5708,  2.63545, -1.7889625, -2.9234265, 0.0115, -0.0115};
	
	double[] youbot_min = new double[]{0.0100693, 0.0100693, -0.015708, 0.0221239, 0.11062, 0		,      0};
	double[] youbot_max = new double[]{5.84014	, 2.61799  , -5.02655 , 3.4292	 , 5.64159, 0.0115	, 0.0115};
	
	int[] joints = new int[7];
	
	private VREPJointController controller;
	
	public YoubotArm(VREPJointController c, int j0, int j1, int j2, int j3, int j4, int gl, int gr){
		controller = c;
		
		joints[0] = j0;
		joints[1] = j1;
		joints[2] = j2;
		joints[3] = j3;
		joints[4] = j4;
		joints[5] = gl;
		joints[6] = gr;
	}
	
	public void setPosition(int joint, double pos){
		controller.setJointPosition(joints[joint], pos);
	}
	
	public void setTargetPosition(int joint, double pos){
		controller.setJointParameter(joints[joint], 2000, 1); // enable dynamic motor state
		controller.setJointParameter(joints[joint], 2001, 1); // enable dynamic motor control loop 
		
		controller.setJointTargetPosition(joints[joint], pos);
	}
	
	public void setTargetVelocity(int joint, double vel){
		controller.setJointParameter(joints[joint], 2000, 1);
		controller.setJointParameter(joints[joint], 2001, 0);
		
		controller.setJointTargetVelocity(joints[joint], vel);
	}
	
	public void setTorque(int joint, double torque){
		controller.setJointParameter(joints[joint], 2000, 1);
		controller.setJointParameter(joints[joint], 2001, 0);
		
		controller.setJointTorque(joints[joint], torque);
	}
	
	public void openGripper(){
		controller.setJointTargetPosition(joints[5], 0.0115f);
		controller.setJointTargetPosition(joints[6], -0.023f);
	}
	
	public void closeGripper(){
		controller.setJointTargetPosition(joints[5], 0.0f);
		controller.setJointTargetPosition(joints[6], -0.0f);
	}
	
	// convert from youbot driver data range to vrep degree data range
	public double convert(double value, int joint){
		double r = (value - youbot_min[joint])/(youbot_max[joint] - youbot_min[joint]);
		double o = r * (vrep_max[joint]-vrep_min[joint]);
		return vrep_min[joint] + o;
	}
	
	// convert from youbot joint uri
	public int getJoint(String joint){
		switch(joint){
		case "arm_joint_1":
			return 0;
		case "arm_joint_2":
			return 1;
		case "arm_joint_3":
			return 2;
		case "arm_joint_4":
			return 3;
		case "arm_joint_5":
			return 4;
		case "gripper_finger_joint_l":
			return 5;
		case "gripper_finger_joint_r":
			return 6;
		}
		return -1;
	}
}
