package be.iminds.iot.robot.api;

public class JointState {

	public float position;
	public float velocity;
	public float torque;
	
	public JointState(float p, float v, float t){
		this.position = p;
		this.velocity = v;
		this.torque = t;
	}
}
