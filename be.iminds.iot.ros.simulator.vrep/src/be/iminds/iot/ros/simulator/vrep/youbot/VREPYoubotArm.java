package be.iminds.iot.ros.simulator.vrep.youbot;

import be.iminds.iot.ros.simulator.vrep.VREPJointController;

public class VREPYoubotArm {

	// VREP handles for all joints
	private int[] joints = new int[7];
	
	// VREP Joint control
	private VREPJointController controller;
	
	public VREPYoubotArm(VREPJointController c, int j0, int j1, int j2, int j3, int j4, int gl, int gr){
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
	

}
