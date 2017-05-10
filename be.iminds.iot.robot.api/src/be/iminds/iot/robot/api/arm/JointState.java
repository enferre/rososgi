package be.iminds.iot.robot.api.arm;

public class JointState {

	public String joint;
	public float position;
	public float velocity;
	public float torque;
	
	public JointState(String joint, float p, float v, float t){
		this.joint = joint;
		this.position = p;
		this.velocity = v;
		this.torque = t;
	}
}
