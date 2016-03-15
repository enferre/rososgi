package be.iminds.iot.ros.simulator.vrep.youbot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class YoubotVREPConvertor {

	// TODO get all these data from config?
	private double[] vrep_min = new double[]{2.94961 , 1.13446, -2.54818,  1.7889625,  2.9234265,      0,       0};
	private double[] vrep_max = new double[]{-2.94961, -1.5708,  2.63545, -1.7889625, -2.9234265, 0.0115, -0.0115};
	
	private double[] youbot_min = new double[]{0.0100693, 0.0100693, -0.015708, 0.0221239, 0.11062, 0		,      0};
	private double[] youbot_max = new double[]{5.84014	, 2.61799  , -5.02655 , 3.4292	 , 5.64159, 0.0115	, 0.0115};
	
	private List<String> youbotArmJoints = Arrays.asList(new String[]{
			"arm_joint_1",
			"arm_joint_2",
			"arm_joint_3",
			"arm_joint_4",
			"arm_joint_5",
			"gripper_finger_joint_l",
			"gripper_finger_joint_r"});
	
	private List<String> vrepArmJoints = new ArrayList<>(7);
	
	public YoubotVREPConvertor(String joint0, String joint1, String joint2, String joint3, String joint4, 
			String gripperL, String gripperR){
		vrepArmJoints.add(joint0);
		vrepArmJoints.add(joint1);
		vrepArmJoints.add(joint2);
		vrepArmJoints.add(joint3);
		vrepArmJoints.add(joint4);
		vrepArmJoints.add(gripperL);
		vrepArmJoints.add(gripperR);
	}
	
	public List<String> getYoubotArmJoints(){
		return youbotArmJoints;
	}
	
	public int getJointIndex(String joint){
		int i = youbotArmJoints.indexOf(joint);
		if(i>=0)
			return i;
		else 
			return vrepArmJoints.indexOf(joint);
	}
	
	public String getYoubotJoint(String vrepJoint){
		return youbotArmJoints.get(vrepArmJoints.indexOf(vrepJoint));
	}
	
	public String getVREPJoint(String youbotJoint){
		return vrepArmJoints.get(youbotArmJoints.indexOf(youbotJoint));
	}
	
	// convert from youbot driver data range to vrep degree data range
	public double convert(double value, int joint){
		double r = (value - youbot_min[joint])/(youbot_max[joint] - youbot_min[joint]);
		double o = r * (vrep_max[joint]-vrep_min[joint]);
		return vrep_min[joint] + o;
	}
	
	// convert from vrep joint state to youbot driver range
	public double invert(double value, int joint){
		double o = value - vrep_min[joint];
		double r = o / (vrep_max[joint]-vrep_min[joint]);
		double y = r * (youbot_max[joint] - youbot_min[joint]);
		return y + youbot_min[joint];
	}
}
