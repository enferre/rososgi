package be.iminds.iot.ros.simulator.vrep;

public class YoubotArm {

	int[] joints = new int[5];
	int[] gripper = new int[2];
	
	private JointController controller;
	
	public YoubotArm(JointController c, int j0, int j1, int j2, int j3, int j4, int gl, int gr){
		controller = c;
		
		joints[0] = j0;
		joints[1] = j1;
		joints[2] = j2;
		joints[3] = j3;
		joints[4] = j4;
		
		gripper[0] = gl;
		gripper[1] = gr;
	}
	
	public void setPosition(int joint, float pos){
		controller.setJointPosition(joints[joint], pos);
	}
	
	public void setTargetPosition(int joint, float pos){
		controller.setJointParameter(joints[joint], 2000, 1); // enable dynamic motor state
		controller.setJointParameter(joints[joint], 2001, 1); // enable dynamic motor control loop 
		
		controller.setJointTargetPosition(joints[joint], pos);
	}
	
	public void setTargetVelocity(int joint, float vel){
		controller.setJointParameter(joints[joint], 2000, 1);
		controller.setJointParameter(joints[joint], 2001, 0);
		
		controller.setJointTargetVelocity(joints[joint], vel);
	}
	
	public void setTorque(int joint, float torque){
		controller.setJointParameter(joints[joint], 2000, 1);
		controller.setJointParameter(joints[joint], 2001, 0);
		
		controller.setJointTorque(joints[joint], torque);
	}
	
	public void openGripper(){
		controller.setJointTargetPosition(gripper[0], 0.025f);
		controller.setJointTargetPosition(gripper[1], -0.05f);
	}
	
	public void closeGripper(){
		controller.setJointTargetPosition(gripper[0], 0.0f);
		controller.setJointTargetPosition(gripper[1], -0.0f);
	}
	
}
