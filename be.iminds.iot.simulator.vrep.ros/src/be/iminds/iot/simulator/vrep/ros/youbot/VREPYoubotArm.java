package be.iminds.iot.simulator.vrep.ros.youbot;

import be.iminds.iot.simulator.vrep.ros.VREPInterface;

public class VREPYoubotArm {

	// VREP handles for all joints
	private int[] joints = new int[7];
	
	// VREP interface
	private VREPInterface vrep;
	
	public VREPYoubotArm(VREPInterface v, int j0, int j1, int j2, int j3, int j4, int gl, int gr){
		vrep = v;
		
		joints[0] = j0;
		joints[1] = j1;
		joints[2] = j2;
		joints[3] = j3;
		joints[4] = j4;
		joints[5] = gl;
		joints[6] = gr;
	}
	
	public void setPosition(int joint, double pos){
		vrep.setJointPosition(joints[joint], pos);
	}
	
	public void setTargetPosition(int joint, double pos){
		vrep.setParameter(joints[joint], 2000, 1); // enable dynamic motor state
		vrep.setParameter(joints[joint], 2001, 1); // enable dynamic motor control loop 
		
		vrep.setJointTargetPosition(joints[joint], pos);
	}
	
	public void setTargetVelocity(int joint, double vel){
		vrep.setParameter(joints[joint], 2000, 1);
		vrep.setParameter(joints[joint], 2001, 0);
		
		vrep.setJointTargetVelocity(joints[joint], vel);
	}
	
	public void setTorque(int joint, double torque){
		vrep.setParameter(joints[joint], 2000, 1);
		vrep.setParameter(joints[joint], 2001, 0);
		
		vrep.setJointTorque(joints[joint], torque);
	}
	
	public void openGripper(){
		vrep.setJointTargetPosition(joints[5], 0.0115f);
		vrep.setJointTargetPosition(joints[6], -0.023f);
	}
	
	public void closeGripper(){
		vrep.setJointTargetPosition(joints[5], 0.0f);
		vrep.setJointTargetPosition(joints[6], -0.0f);
	}

}
